package net.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KineticInterfaceVisual extends OrientedRotatingVisual<KineticInterfaceBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {
    private final PSKInstance instance;

    public KineticInterfaceVisual(VisualizationContext context, KineticInterfaceBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Direction.SOUTH.getOpposite(),
                blockEntity.getBlockState().getValue(BlockStateProperties.FACING), Models.partial(AllPartialModels.SHAFT_HALF));

        instance = new PSKInstance(visualizationContext.instancerProvider(), blockState, getVisualPosition(), isLit());
        instance.setActorRotation(blockEntity.getSpeed());
        instance.beginFrame(blockEntity.getExtensionDistance(partialTick));
    }

    private boolean isLit() {
        return blockEntity.isConnected();
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        instance.update(blockEntity);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        instance.collectCrumblingInstances(consumer);
    }

    @Override
    public void updateLight(float v) {
        super.updateLight(v);
        relight(instance.rotatingPulley, instance.top);
    }

    @Override
    protected void _delete() {
        super._delete();
        instance.remove();
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        instance.beginFrame(blockEntity.getExtensionDistance(context.partialTick()));
    }

    @Override
    public void tick(TickableVisual.Context ctx) {
        instance.tick(isLit());
    }
}
