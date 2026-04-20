package com.Portality.createsprings.blocks.advanced.SpringCoil;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.utill.Helpers.RenderHelper;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import static com.Portality.createsprings.utill.Helpers.RenderHelper.MoveWithoutVectors;

public class SpringCoilVisual extends SingleAxisRotatingVisual<SpringCoilBlockEntity> implements SimpleDynamicVisual {

    private final OrientedInstance plate;
    private Vec3i movementDirection;
    private Direction facing;
    private boolean initialise = true;

    public SpringCoilVisual(VisualizationContext context, SpringCoilBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(CSpringsPartalModels.LARGE_SPRING_COIL));
        plate = createInstance(CSpringsPartalModels.SPRING_PLATE);
        plate.setVisible(false);

        animate();
    }

    private void applyBaseTransformations(OrientedInstance instance, Direction facing){
        RenderHelper.applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
    }

    private OrientedInstance createInstance(PartialModel model) {
        return instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model)).createInstance();
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        animate();
    }

    private void animate(){
        if(blockEntity.plate){
            if(initialise){
                plate.setVisible(true);
                initialise = false;
                facing = blockEntity.plateFacing;
                movementDirection = facing.getOpposite().getNormal();
                applyBaseTransformations(plate, facing);
            }
            MoveWithoutVectors(0, plate, movementDirection, getVisualPosition());
        }
    }

    @Override
    protected void _delete() {
        super._delete();
        plate.delete();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(plate);
    }
}
