package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.*;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock.LEN;
import static net.Portality.createsprings.utill.Helpers.RenderHelper.*;

public class LargeSpringVisual extends ShaftVisual<LargeSpringBlockEntity> implements SimpleDynamicVisual {
    private final List<OrientedInstance> rings = new ArrayList<>();
    private final List<OrientedInstance> rings_corners = new ArrayList<>();
    private final OrientedInstance up_plate;
    private final OrientedInstance down_plate;
    final Direction facing;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;
    private final int SPRING_LEN;
    private int prevLen;

    public LargeSpringVisual(VisualizationContext context, LargeSpringBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        SPRING_LEN = blockEntity.getLen() * 4;
        prevLen = blockEntity.getLen() * 4;

        facing = blockEntity.getBlockState().getValue(DirectionalKineticBlock.FACING);

        rotationAxis = getRotationAxis(facing);
        blockOrientation = getBlockStateOrientation(facing);


        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.LARGE_SPRING_COIL_ROTATED, instancerProvider()));
            rings_corners.add(createInstance(CSpringsPartalModels.LARGE_SPRING_COIL_CORNER, instancerProvider()));
        }
        up_plate = createInstance(CSpringsPartalModels.LARGE_SPRING_PLATE, instancerProvider());
        down_plate = createInstance(CSpringsPartalModels.LARGE_SPRING_PLATE, instancerProvider());

        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
            applyBaseTransformations(rings.get(i), facing);
            applyBaseRotationCorner(rings_corners.get(i), i);
            applyBaseTransformations(rings_corners.get(i), facing);
        }

        applyPlateBaseTransformations(up_plate, facing);
        applyPlateBaseTransformations(down_plate, facing);

        setPos(0, 0, down_plate, -7/16f );

        animate(partialTick);
    }

    private void applyPlateBaseTransformations(OrientedInstance instance, Direction facing){
        RenderHelper.applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
    }

    private void applyBaseRotationCorner(OrientedInstance instance, int index){
        Direction.Axis axis = facing.getAxis();
        if(axis == Direction.Axis.Z){
            instance.rotateDegrees(90 * index, axis);
            return;
        } else if (axis == Direction.Axis.X){
            instance.rotateDegrees(90 * index, axis);
            instance.rotateDegrees(-90, axis);
            return;
        }
        if(index % 4 == 1){
            instance.rotateDegrees(-90, axis);
            return;
        } else if(index % 4 == 3){
            instance.rotateDegrees(90, axis);
            return;
        }
        instance.rotateDegrees(90 * index, axis);
    }

    private void applyBaseRotation(OrientedInstance instance, int index) {
        Direction.Axis axis = facing.getAxis();
        instance.rotateDegrees(90 * index, axis);
    }

    private void applyBaseTransformations(OrientedInstance instance, Direction facing){
        if(facing == Direction.UP || facing == Direction.DOWN){
            return;
        } else if(facing == Direction.EAST || facing == Direction.WEST){
            instance.rotateZDegrees(-90);
            return;
        }
        instance.rotateXDegrees(-90);
    }

    @Override
    public void beginFrame(Context context) {
        animate(context.partialTick());
    }

    private void animate(float partialTick){
        int len = blockEntity.getLen() * 4;
        float progres = 1 - blockEntity.getProgres(partialTick);

        if(len != prevLen){
            rings.forEach(this::setInvisible);
            rings_corners.forEach(this::setInvisible);
            prevLen = len;
        }

        for (int i = 0; i < len; i++) {
            updateRingPosition(rings.get(i), i, progres, len);
            updateCornerPosition(rings_corners.get(i), i, progres, len);
            rings.get(i).setVisible(true);
            rings_corners.get(i).setVisible(true);
        }
        setPos(0, 0, up_plate, calculateLen(progres, len) - 4/16f*(1-progres));
    }

    private void setInvisible(OrientedInstance instance){
        instance.setVisible(false);
    }

    private void updateRingPosition(OrientedInstance ring, int ringIndex, float progres, int len) {
        int x = 0;
        int z = 0;

        int index = ringIndex % 4;
        switch (index){
            case 0: x = 1; break;
            case 1: z = 1; break;
            case 2: x = -1; break;
            case 3: z = -1; break;
        }

        setPos(x, z, ring, calculateLen(progres, ringIndex));
    }

    private void updateCornerPosition(OrientedInstance corner, int ringIndex, float progres, int len) {
        int x = 0;
        int z = 0;

        int index = ringIndex % 4;
        switch (index){
            case 0: x = 1;z = 1; break;
            case 1: x = -1;z = 1; break;
            case 2: x = -1;z = -1; break;
            case 3: x = 1;z = -1; break;
        }

        setPos(x, z, corner, calculateLen(progres, ringIndex));
    }

    private void setPos(int x, int z, OrientedInstance instance, float len){
        float dierectionFactor = 1;
        BlockPos pos = getVisualPosition();
        if(facing == Direction.DOWN || facing == Direction.WEST || facing == Direction.NORTH){
            dierectionFactor = -1;
        }
        if(facing == Direction.UP || facing == Direction.DOWN){
            instance.position(
                    (pos.getX() + x),
                    (pos.getY() + len * dierectionFactor),
                    (pos.getZ() + z)
            ).setChanged();
            return;
        } else if(facing == Direction.EAST || facing == Direction.WEST){
            instance.position(
                    (pos.getX() + len * dierectionFactor),
                    (pos.getY() + x),
                    (pos.getZ() + z)
            ).setChanged();
            return;
        }
        instance.position(
                (pos.getX() + x),
                (pos.getY() + z),
                (pos.getZ() + len * dierectionFactor)
        ).setChanged();
    }

    public static float calculateLen(float progres, int ringIndex){
        return ringIndex/4f*progres  + (ringIndex / 4) / 2f * (1-progres) - 3/16f;
    }

    @Override
    protected void _delete() {
        super._delete();
        up_plate.delete();
        down_plate.delete();
        rings.forEach(OrientedInstance::delete);
        rings_corners.forEach(OrientedInstance::delete);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(up_plate);
        relight(down_plate);
        rings.forEach(this::relight);
        rings_corners.forEach(this::relight);
    }
}
