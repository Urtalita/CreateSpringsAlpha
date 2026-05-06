package com.Portality.createsprings.blocks.advanced.largeSpring;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.utill.Helpers.RenderHelper;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static com.Portality.createsprings.utill.Helpers.RenderHelper.*;

public class LargeSpringVisual extends ShaftVisual<LargeSpringBlockEntity> implements SimpleDynamicVisual {
    private final List<OrientedInstance> rings = new ArrayList<>();
    private final List<OrientedInstance> rings_corners = new ArrayList<>();
    private OrientedInstance up_plate;
    private OrientedInstance down_plate;
    Direction facing;
    Axis rotationAxis;
    Quaternionf blockOrientation;
    private int SPRING_LEN;
    private int prevLen;

    public LargeSpringVisual(VisualizationContext context, LargeSpringBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        createSpringVisual(blockEntity.getBlockState().getValue(DirectionalKineticBlock.FACING), blockEntity.getLen() * 4, 1 - blockEntity.getProgres(partialTick));
    }

    public void createSpringVisual(Direction facing, int len, float progres){
        SPRING_LEN = ModConfigs.common().SPRING_LEN.get() * 4;
        prevLen = ModConfigs.common().SPRING_LEN.get() * 4;

        this.facing = facing;

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

        animate(len, progres);
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
    public void beginFrame(DynamicVisual.Context context) {
        animate(blockEntity.getLen() * 4, 1 - blockEntity.getProgres(context.partialTick()));
    }

    public void animate(int len, float progres){

        setPos(0, 0, up_plate, calculateLen(progres, len) - 4/16f*(1-progres));

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
        deleteSpring();
    }

    public void deleteSpring(){
        up_plate.delete();
        down_plate.delete();
        rings.forEach(OrientedInstance::delete);
        rings_corners.forEach(OrientedInstance::delete);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        updateLightOnSpring();
    }

    public void updateLightOnSpring(){
        relight(up_plate);
        relight(down_plate);
        rings.forEach(this::relight);
        rings_corners.forEach(this::relight);
    }
}
