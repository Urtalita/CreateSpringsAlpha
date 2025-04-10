package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpringCoilVisual extends SingleAxisRotatingVisual<SpringCoilBlockEntity> implements SimpleDynamicVisual {
    private final List<OrientedInstance> rings = new ArrayList<>();
    private final List<OrientedInstance> rings_corners = new ArrayList<>();
    private static final int SPRING_LEN = 4;

    public SpringCoilVisual(VisualizationContext context, SpringCoilBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(CSpringsPartalModels.LARGE_SPRING_COIL));

        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.LARGE_SPRING_COIL));
            rings_corners.add(createInstance(CSpringsPartalModels.LARGE_SPRING_COIL_CORNER));
        }

        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
            applyBaseRotationCorner(rings_corners.get(i), i);
        }
    }

    private void applyBaseRotationCorner(OrientedInstance instance, int index){
        if(index == 1){
            instance.rotateYDegrees(-90);
            return;
        } else if(index == 3){
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
        rotatingModel.setVisible(!blockEntity.getAssembled());
        rotatingModel.setChanged();
        boolean controller = blockEntity.isController();

        for (int i = 0; i < rings.size(); i++) {
            rings.get(i).setVisible(controller);
            rings_corners.get(i).setVisible(controller);
        }

        if(!controller){
            return;
        }

        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(rings.get(i), i);
            updateCornerPosition(rings_corners.get(i), i);
        }
    }

    private void updateRingPosition(OrientedInstance ring, int ringIndex) {
        int x = 0;
        int z = 0;

        switch (ringIndex){
            case 0: x = 1; break;
            case 1: z = 1; break;
            case 2: x = -1; break;
            case 3: z = -1; break;
        }

        ring.position(
                (pos.getX() + x),
                (pos.getY() + ringIndex/4f - 0.5f),
                (pos.getZ() + z)
        ).setChanged();
    }

    private void updateCornerPosition(OrientedInstance corner, int ringIndex) {
        int x = 0;
        int z = 0;

        switch (ringIndex){
            case 0: x = 1;z = 1; break;
            case 1: x = -1;z = 1; break;
            case 2: x = -1;z = -1; break;
            case 3: x = 1;z = -1; break;
        }

        corner.position(
                (pos.getX() + x),
                (pos.getY() + ringIndex/4f - 0.5f),
                (pos.getZ() + z)
        ).setChanged();
    }

    @Override
    protected void _delete() {
        super._delete();
        rings.forEach(OrientedInstance::delete);
        rings_corners.forEach(OrientedInstance::delete);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        rings.forEach(this::relight);
        rings_corners.forEach(this::relight);
    }
}
