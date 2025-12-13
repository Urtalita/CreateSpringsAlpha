package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.Portality.createsprings.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpringMovement implements MovementBehaviour {
    private SpringActorVisual visual;

    @Override
    public @Nullable ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        visual = new SpringActorVisual(visualizationContext, simulationWorld, movementContext);
        return visual;
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
        if(visual != null){
            visual.setProgress(contraption, localPos, compoundTag);
        }
    }
}
