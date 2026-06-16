package com.Portality.createsprings.blocks.advanced.spring;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class SpringMovement implements MovementBehaviour {
    private SpringActorVisual visual;
    HashMap<UUID, HashMap<BlockPos, Float>> storedInSprings = new HashMap<>();

    @Override
    public @Nullable ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        visual = new SpringActorVisual(visualizationContext, simulationWorld, movementContext);
        return visual;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        MovementBehaviour.super.renderInContraption(context, renderWorld, matrices, buffer);
        if (!VisualizationManager.supportsVisualization(context.world)){
            HashMap<BlockPos, Float> contraption = storedInSprings.get(context.contraption.entity.getUUID());
            if(contraption == null){
                SpringRenderer.renderSpringInContraption(context, renderWorld, matrices, buffer, null);
                return;
            }
            SpringRenderer.renderSpringInContraption(context, renderWorld, matrices, buffer, contraption.get(context.localPos));
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

    public void setProgress(UUID contraption, BlockPos localPos, CompoundTag compoundTag){
        if(storedInSprings.containsValue(contraption)){
            storedInSprings.get(contraption).put(localPos, compoundTag.getFloat("Stored"));
        } else {
            HashMap<BlockPos, Float> pair = new HashMap<>();
            pair.put(localPos, compoundTag.getFloat("Stored"));
            storedInSprings.put(contraption, pair);
        }
        if(visual != null){
            visual.setProgress(contraption, localPos, compoundTag);
        }
    }
}
