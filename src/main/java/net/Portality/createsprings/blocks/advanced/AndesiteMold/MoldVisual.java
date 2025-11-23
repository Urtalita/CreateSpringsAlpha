package net.Portality.createsprings.blocks.advanced.AndesiteMold;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static net.Portality.createsprings.utill.Helpers.RenderHelper.createInstance;

public class MoldVisual extends AbstractBlockEntityVisual implements SimpleDynamicVisual {
    private final OrientedInstance mold;
    private final OrientedInstance coil;

    public MoldVisual(VisualizationContext visualizationContext, MoldBlockEntity moldBlockEntity, float v) {
        super(visualizationContext, moldBlockEntity, v);
        mold = createInstance(CSpringsPartalModels.MOLD, instancerProvider());
        mold.position(getVisualPosition().getX(), getVisualPosition().getY(), getVisualPosition().getZ());
        applyBaseTransformations(mold, moldBlockEntity.getBlockState().getValue(FACING));
        mold.rotateDegrees(90, Direction.Axis.X);

        coil = createInstance(CSpringsPartalModels.LARGE_SPRING_COIL, instancerProvider());
        coil.position(getVisualPosition().getX(), getVisualPosition().getY(), getVisualPosition().getZ());
        RenderHelper.applyBaseTransformations(coil, moldBlockEntity.getBlockState().getValue(FACING));
    }

    private void applyBaseTransformations(OrientedInstance instance, Direction facing){
        RenderHelper.applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
    }

    @Override
    public void collectCrumblingInstances(Consumer consumer) {

    }

    @Override
    public void updateLight(float v) {
        relight(mold);
        relight(coil);
    }

    @Override
    protected void _delete() {
        mold.delete();
        coil.delete();
    }

    @Override
    public void beginFrame(Context context) {
        if(blockEntity instanceof MoldBlockEntity moldBlockEntity){
            if(!moldBlockEntity.filled){
                coil.setVisible(false);
            } else {
                coil.setVisible(true);
            }
        }
    }
}
