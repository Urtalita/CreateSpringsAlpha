package net.Portality.createsprings.blocks.advanced.test;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import org.jetbrains.annotations.Nullable;

public class TestVisual extends ShaftVisual {
    TransformedInstance instance;
    public TestVisual(VisualizationContext visualizationContext, TestBlockEntity testBlockEntity, float v) {
        super(visualizationContext, testBlockEntity, v);

        instance = visualizationContext.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.DRILL_HEAD)).createInstance();
        instance.scale(16);
        instance.translate(getVisualPosition());
        instance.setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(instance);
    }

    @Override
    protected void _delete() {
        super._delete();
        instance.delete();
    }
}
