package com.Portality.createsprings.blocks.advanced.spring;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.utill.Helpers.RenderHelper;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static com.Portality.createsprings.utill.Helpers.RenderHelper.*;

public class SpringInstance {
    public OrientedInstance plate;
    public OrientedInstance secondPlate;
    public final List<OrientedInstance> rings = new ArrayList<>();
    private final List<Float> ringPos = new ArrayList<>();
    public static int SPRING_LEN = SpringVisual.SPRING_LEN;
    private Vec3 movementDirection;
    final Direction facing;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;
    final Vec3 instancePos;

    public SpringInstance(InstancerProvider instancerProvider, BlockState state, BlockPos localPos, boolean b) {

        facing = state.getValue(DirectionalKineticBlock.FACING);
        instancePos = localPos.getCenter().add(-0.5f, -0.5f, -0.5f);

        rotationAxis = getRotationAxis(facing);

        blockOrientation = getBlockStateOrientation(facing);

        movementDirection = Vec3.atLowerCornerOf(facing.getOpposite().getNormal());

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

    public SpringInstance(InstancerProvider instancerProvider, Direction facing, Vec3 localPos, boolean b) {
        instancePos = localPos;
        this.facing = facing;

        rotationAxis = getRotationAxis(facing);

        blockOrientation = getBlockStateOrientation(facing);

        movementDirection = Vec3.atLowerCornerOf(facing.getOpposite().getNormal());

        plate = createInstance(CSpringsPartalModels.SPRING_PLATE, instancerProvider);
        secondPlate = createInstance(CSpringsPartalModels.SPRING_PLATE, instancerProvider);
        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.SPRING_PIECE, instancerProvider));
            ringPos.add((2f+i)/16f);
        }

        applyBaseTransformations(secondPlate, facing);
        applyBaseTransformations(plate, facing);
        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
            applyBaseTransformations(rings.get(i), facing);
        }
    }

    public static void applyBaseTransformations(OrientedInstance instance, Direction facing){
        RenderHelper.applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
    }

    private void applyBaseRotation(OrientedInstance instance, int index) {
        Quaternionf rot = rotationAxis.rotationDegrees(45 + index * 90);
        instance.rotation(rot).setChanged();
    }

    public void rotate(float yAngle, float xAngle){
        double horizontalAngleRad = Math.toRadians(yAngle);
        double verticalAngleRad = Math.toRadians(xAngle);

        double x = Math.sin(horizontalAngleRad) * Math.cos(verticalAngleRad);
        double y = Math.sin(verticalAngleRad);
        double z = Math.cos(horizontalAngleRad) * Math.cos(verticalAngleRad);

        movementDirection = new Vec3(x, y, z);

        setRotation(plate, yAngle, xAngle);
        setRotation(secondPlate, yAngle, xAngle);

        for(int i = 0; i< rings.size(); i++){
            setRotation(rings.get(i), yAngle, xAngle);
            rings.get(i).rotateZDegrees(i * 90);
        }
    }

    private void setRotation(OrientedInstance instance, float yAngle, float xAngle){
        instance.identityRotation();
        instance.rotateYDegrees(yAngle);
        instance.rotateXDegrees(-xAngle);
    }

    public void animate(float progress) {
        MoveToPos(2/16f, 9/16f, plate, progress, movementDirection, instancePos);
        MoveToPos(16/16f, 16/16f, secondPlate, progress, movementDirection, instancePos);

        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(progress, rings.get(i), i);
        }
    }

    public void animateInContraption(float progress) {
        MoveToPos(1/16f, 8/16f, plate, progress, movementDirection, instancePos);

        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(progress, rings.get(i), i);
        }
    }

    private void updateRingPosition(float progress, OrientedInstance ring, int ringIndex) {
        MoveToPos(ringPos.get(ringIndex) + 1/16f, (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f , ring, progress, movementDirection, instancePos);
    }

    public void deleteSpring() {
        secondPlate.delete();
        plate.delete();
        rings.forEach(OrientedInstance::delete);
    }

    public void deleteSpringInContraption() {
        plate.delete();
        rings.forEach(OrientedInstance::delete);
    }
}
