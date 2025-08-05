package net.Portality.createsprings.blocks.advanced.Spring;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static net.Portality.createsprings.utill.Helpers.RenderHelper.*;
import static net.Portality.createsprings.utill.Helpers.RenderHelper.createInstance;

public class SpringInstance {
    public OrientedInstance plate;
    public final List<OrientedInstance> rings = new ArrayList<>();
    private final List<Float> ringPos = new ArrayList<>();
    public static int SPRING_LEN = SpringVisual.SPRING_LEN;
    private final Vec3i movementDirection;
    final Direction facing;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;
    final BlockPos instancePos;

    public SpringInstance(InstancerProvider instancerProvider, BlockState state, BlockPos localPos, boolean b) {
        facing = state.getValue(DirectionalKineticBlock.FACING);
        instancePos = localPos;

        rotationAxis = getRotationAxis(facing);

        blockOrientation = getBlockStateOrientation(facing);

        movementDirection = facing.getOpposite().getNormal();

        plate = createInstance(CSpringsPartalModels.SPRING_PLATE, instancerProvider);
        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.SPRING_PIECE, instancerProvider));
            ringPos.add((2f+i)/16f);
        }

        applyBaseTransformations(plate, facing);
        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
            applyBaseTransformations(rings.get(i), facing);
        }
    }

    private void applyBaseTransformations(OrientedInstance instance, Direction facing){
        RenderHelper.applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
    }

    private void applyBaseRotation(OrientedInstance instance, int index) {
        Quaternionf rot = rotationAxis.rotationDegrees(45 + index * 90);
        instance.rotation(rot).setChanged();
    }

    public void animate(float progress) {
        MoveToPos(1/16f, 8/16f, plate, progress, movementDirection, instancePos);

        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(progress, rings.get(i), i);
        }
    }

    private void updateRingPosition(float progress, OrientedInstance ring, int ringIndex) {
        MoveToPos(ringPos.get(ringIndex) + 1/16f, (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f , ring, progress, movementDirection, instancePos);
    }

    public void deleteSpring() {
        plate.delete();
        rings.forEach(OrientedInstance::delete);
    }
}
