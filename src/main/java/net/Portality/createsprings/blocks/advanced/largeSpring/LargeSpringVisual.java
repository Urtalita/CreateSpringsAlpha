package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlockEntity;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LargeSpringVisual extends ShaftVisual<LargeSpringBlockEntity> implements SimpleDynamicVisual {
    private final List<OrientedInstance> rings = new ArrayList<>();
    private final List<OrientedInstance> rings_corners = new ArrayList<>();
    private final OrientedInstance up_plate;
    private final OrientedInstance down_plate;
    private final int SPRING_LEN;

    public LargeSpringVisual(VisualizationContext context, LargeSpringBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        SPRING_LEN = blockEntity.getLen() * 4;

        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.LARGE_SPRING_COIL));
            rings_corners.add(createInstance(CSpringsPartalModels.LARGE_SPRING_COIL_CORNER));
        }
        up_plate = createInstance(CSpringsPartalModels.LARGE_SPRING_PLATE).rotateXDegrees(90);
        down_plate = createInstance(CSpringsPartalModels.LARGE_SPRING_PLATE).rotateXDegrees(90);

        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
            applyBaseRotationCorner(rings_corners.get(i), i);
        }
    }

    private void applyBaseRotationCorner(OrientedInstance instance, int index){
        if(index % 4 == 1){
            instance.rotateYDegrees(-90);
            return;
        } else if(index % 4 == 3){
            instance.rotateYDegrees(90);
            return;
        }
        instance.rotateYDegrees(90 * index);
    }

    private void applyBaseRotation(OrientedInstance instance, int index) {
        instance.rotateXDegrees(90)
                .rotateZDegrees(90 * index);
    }

    private OrientedInstance createInstance(PartialModel model) {
        return instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model)).createInstance();
    }

    @Override
    public void beginFrame(Context context) {
        float progres = 1 - blockEntity.getProgres();
        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(rings.get(i), i, progres);
            updateCornerPosition(rings_corners.get(i), i, progres);
        }

        down_plate.position(
                (pos.getX()),
                (pos.getY() - 7/16f ),
                (pos.getZ())
        ).setChanged();

        up_plate.position(
                (pos.getX()),
                (pos.getY() + calculateLen(progres, SPRING_LEN) - 3/16f*(1-progres)),
                (pos.getZ())
        ).setChanged();
    }

    private void updateRingPosition(OrientedInstance ring, int ringIndex, float progres) {
        int x = 0;
        int z = 0;

        int index = ringIndex % 4;
        switch (index){
            case 0: x = 1; break;
            case 1: z = 1; break;
            case 2: x = -1; break;
            case 3: z = -1; break;
        }

        ring.position(
                (pos.getX() + x),
                (pos.getY() + calculateLen(progres, ringIndex)),
                (pos.getZ() + z)
        ).setChanged();
    }

    private void updateCornerPosition(OrientedInstance corner, int ringIndex, float progres) {
        int x = 0;
        int z = 0;

        int index = ringIndex % 4;
        switch (index){
            case 0: x = 1;z = 1; break;
            case 1: x = -1;z = 1; break;
            case 2: x = -1;z = -1; break;
            case 3: x = 1;z = -1; break;
        }

        corner.position(
                (pos.getX() + x),
                (pos.getY() + calculateLen(progres, ringIndex)),
                (pos.getZ() + z)
        ).setChanged();
    }

    private float calculateLen(float progres, int ringIndex){
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
