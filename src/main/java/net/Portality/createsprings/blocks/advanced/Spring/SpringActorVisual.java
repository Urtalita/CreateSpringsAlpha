package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringInstance;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class SpringActorVisual extends ActorVisual {
    private SpringInstance springInstance;
    public float progress = 0;
    public float prevProgress = 0;

    public SpringActorVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        super(visualizationContext, simulationWorld, movementContext);
        springInstance = new SpringInstance(visualizationContext.instancerProvider(), context.state, context.localPos, false);

        CompoundTag be = context.contraption.getActorAt(context.localPos).left.nbt();
        if(be != null){
            progress = be.getFloat("Stored") / Config.spring_capacity;
            prevProgress = progress;
        }

        setlight(springInstance.plate);
        springInstance.rings.forEach(this::setlight);
    }

    @Override
    public void tick() {
        super.tick();
    }

    private void setlight(OrientedInstance orientedInstance) {
        orientedInstance.light(localBlockLight(), 0);
    }

    @Override
    public void beginFrame() {
        super.beginFrame();
        CompoundTag be = context.contraption.getActorAt(context.localPos).left.nbt();
        if(be != null){
            progress = be.getFloat("Stored") / Config.spring_capacity;
            prevProgress = progress;
        }
        springInstance.animate(Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, progress));
    }

    @Override
    protected void _delete() {
        springInstance.deleteSpring();
    }
}
