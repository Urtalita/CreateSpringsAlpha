package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.Portality.createsprings.utill.CSpringsPartalModels;

import java.util.function.Consumer;

public class SpringCoilVisual extends SingleAxisRotatingVisual<SpringCoilBlockEntity> {
    public SpringCoilVisual(VisualizationContext context, SpringCoilBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(CSpringsPartalModels.LARGE_SPRING_COIL));
    }
}
