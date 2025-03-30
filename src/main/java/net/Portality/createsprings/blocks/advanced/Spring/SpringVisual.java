package net.Portality.createsprings.blocks.advanced.Spring;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Quaternionf;

import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public class SpringVisual extends ShaftVisual<SpringBlockEntity> implements SimpleDynamicVisual  {

    private static int SPRING_LEN = 12;
    private final OrientedInstance plate;
    private final List<OrientedInstance> rings = new ArrayList<>();
    private final List<Float> ringPos = new ArrayList<>();
    private final Vec3i movementDirection;
    protected final double xRot;
    protected final double yRot;
    final Direction facing;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;

    public SpringVisual(VisualizationContext context, SpringBlockEntity blockEntity, float pt) {
        super(context, blockEntity, pt);

        facing = blockEntity.getBlockState().getValue(DirectionalKineticBlock.FACING);
        yRot = AngleHelper.horizontalAngle(facing);
        xRot = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;

        rotationAxis = getRotationAxis();

        blockOrientation = getBlockStateOrientation(facing);

        movementDirection = facing.getOpposite().getNormal();

        plate = createInstance(CSpringsPartalModels.SPRING_PLATE);
        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.SPRING_PIECE));
            ringPos.add((2f+i)/16f);
        }

        applyBaseTransformations(plate);
        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
        }
        rings.forEach(this::applyBaseTransformations);
    }

    private OrientedInstance createInstance(PartialModel model) {
        return instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model)).createInstance();
    }

    private void applyBaseTransformations(OrientedInstance instance) {
        instance.rotateYDegrees((float) yRot)
                .rotateXDegrees((float) xRot)
                .rotateXDegrees(-90);
    }

    private void applyBaseRotation(OrientedInstance instance, int index) {
        Quaternionf rot = rotationAxis.rotationDegrees(45 + index * 90);
        instance.rotation(rot).setChanged();
    }

    private Axis getRotationAxis(){
        if(facing == Direction.UP || facing == Direction.DOWN){
            return Axis.YN;
        } else if (facing == Direction.EAST || facing == Direction.WEST){
            return Axis.XP;
        }
        return Axis.ZN;
    }

    public void beginFrame(Context context) {
        float progress = blockEntity.getProgress();

        MoveToPos(1/16f, 8/16f, plate, progress);

        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(progress, rings.get(i), i);
        }
    }

    private void updateRingPosition(float progress, OrientedInstance ring, int ringIndex) {
        MoveToPos(ringPos.get(ringIndex) + 1/16f, (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f , ring, progress);
    }

    private void MoveToPos(float StartPos, float EndPos, OrientedInstance orientedInstance, float Progress){
        MoveWithoutVectors(StartPos + (EndPos - StartPos) * Progress ,orientedInstance);
    }

    private void MoveWithoutVectors(float Moving, OrientedInstance instance){
        float offset = 1 - Moving - 0.5f;
        BlockPos pos = getVisualPosition();
        instance.position(
                (pos.getX() + movementDirection.getX() * offset),
                (pos.getY() + movementDirection.getY() * offset),
                (pos.getZ() + movementDirection.getZ() * offset)
        ).setChanged();
    }

    static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation;

        if (facing.getAxis().isHorizontal()) {
            orientation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.getOpposite()));
        } else {
            orientation = new Quaternionf();
        }

        orientation.mul(Axis.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)));
        return orientation;
    }

    @Override
    protected void _delete() {
        super._delete();
        plate.delete();
        rings.forEach(OrientedInstance::delete);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(plate);
        rings.forEach(this::relight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(plate);
        rings.forEach(consumer);
    }
}
