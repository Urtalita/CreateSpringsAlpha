package net.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class PSKInstance {
    private final InstancerProvider instancerProvider;
    public final BlockPos instancePos;
    private final Vec3i movementDirection;
    public final Direction facing;
    public final float angleX;
    public final float angleY;

    private boolean lit;
    TransformedInstance top;
    RotatingInstance rotatingPulley;

    public PSKInstance(InstancerProvider instancerProvider, BlockState blockState, BlockPos instancePos, boolean lit) {
        this.instancerProvider = instancerProvider;
        this.instancePos = instancePos;
        facing = blockState.getValue(KineticInterfaceBlock.FACING);
        this.lit = lit;

        angleX = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
        angleY = AngleHelper.horizontalAngle(facing);

        movementDirection = facing.getOpposite().getNormal();

        top = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.INTERFACE_TOP))
                .createInstance();

        rotatingPulley = instancerProvider.instancer(AllInstanceTypes.ROTATING, Models.partial(CSpringsPartalModels.INTERFACE_PULLEY))
                .createInstance()
                .rotateToFace(Direction.SOUTH.getOpposite(), blockState.getValue(BlockStateProperties.FACING).getOpposite())
                .setRotationAxis(facing.getAxis())
                .setRotationalSpeed(0)
                .setPosition(instancePos);
    }

    public void beginFrame(float progress) {
        float offset = progress * 16 * 2;
        float pulleyOffset = offset / 16f / 2f;


        if(offset >= 8f && offset <= 12f){
            pulleyOffset = offset / 16 / 2;
            offset = 8;
        }
        if(offset > 12f){
            if(offset <= 22f){
                pulleyOffset = offset / 16 / 2;
            } else {
                pulleyOffset = 22f / 16f / 2f;
            }
            offset -= 6;
        }

        if(offset >= 12f){offset = 12;}

        offset = offset / 16f / 2f;

        MoveWithoutVectors(offset * -1, top);
        MoveWithoutVectors(pulleyOffset * -1, rotatingPulley);
    }

    public void setActorRotation(float speed){
        rotatingPulley.setRotationalSpeed(speed * RotatingInstance.SPEED_MULTIPLIER);
    }

    public void MoveWithoutVectors(float Moving, TransformedInstance instance){
        instance.setIdentityTransform()
                .translate(instancePos)
                .center()
                .rotateYDegrees(angleY)
                .rotateXDegrees(angleX + 90)
                .uncenter();

        instance.translate(
                (0),
                (0),
                (Moving)
        ).setChanged();
    }

    public void MoveWithoutVectors(float Moving, RotatingInstance instance){
        BlockPos pos = instancePos;
        instance.x = pos.getX() + movementDirection.getX() * Moving;
        instance.y = pos.getY() + movementDirection.getY() * Moving;
        instance.z = pos.getZ() + movementDirection.getZ() * Moving;
        instance.setChanged();
    }

    public void tick(boolean lit) {
        if (this.lit != lit) {
            this.lit = lit;
            instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.INTERFACE_TOP))
                    .stealInstance(top);
        }
    }

    public void remove() {
        rotatingPulley.delete();
        top.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(rotatingPulley);
        consumer.accept(top);
    }

    public void update(KineticBlockEntity blockEntity) {
        rotatingPulley.setup(blockEntity).setChanged();
    }
}
