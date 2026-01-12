package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.Portality.createsprings.blocks.advanced.Spring.SpringRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LargeSpringMovement implements MovementBehaviour {
    LargeSpringActorVisual visual;
    @Override
    public @Nullable ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        visual = new LargeSpringActorVisual(visualizationContext, simulationWorld, movementContext);
        return visual;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        MovementBehaviour.super.renderInContraption(context, renderWorld, matrices, buffer);
        if (!VisualizationManager.supportsVisualization(context.world))
            LargeSpringRenderer.renderSpringInContraption(context, renderWorld, matrices, buffer, null);
    }

    public void updateRender(float progress){
        if(visual != null){
            visual.progress = 1 - progress;
        }
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void tick(MovementContext context) {
        MovementBehaviour.super.tick(context);
        if(visual != null){
            visual.prevProgress = visual.progress;
        }
    }

    public void setProgress(UUID contraption, BlockPos localPos, CompoundTag updatedEntity) {
        if(visual != null){
            visual.setProgress(contraption, localPos, updatedEntity);
        }
    }
}
