package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import org.jetbrains.annotations.Nullable;

public class LargeSpringMovement implements MovementBehaviour {
    LargeSpringActorVisual visual;
    @Override
    public @Nullable ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        visual = new LargeSpringActorVisual(visualizationContext, simulationWorld, movementContext);
        return visual;
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
}
