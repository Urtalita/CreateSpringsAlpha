package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.Portality.createsprings.config.ModConfigs;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.UUID;

public class SpringActorVisual extends ActorVisual {
    private SpringInstance springInstance;
    public float progress = 0;
    public float prevProgress = 0;

    public SpringActorVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        super(visualizationContext, simulationWorld, movementContext);
        springInstance = new SpringInstance(visualizationContext.instancerProvider(), context.state, context.localPos, false);

        CompoundTag be = context.contraption.getActorAt(context.localPos).left.nbt();
        if(be != null){
            progress = be.getFloat("Stored") / ModConfigs.common().SPRING_CAPACITY.get();
            prevProgress = progress;
        }

        setlight(springInstance.plate);
        springInstance.rings.forEach(this::setlight);
        springInstance.animateInContraption(Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, progress));
    }

    public void setProgress(UUID contraption, BlockPos localPos, CompoundTag compoundTag){
        if(context.contraption.entity != null){
            if(contraption.equals(context.contraption.entity.getUUID())){
                if(localPos.equals(context.localPos)){
                    float prog = compoundTag.getFloat("Stored") / ModConfigs.common().SPRING_CAPACITY.get();
                    this.prevProgress = this.progress;
                    this.progress = prog;
                }
            }
        }
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
        springInstance.animateInContraption(Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, progress));
    }

    @Override
    protected void _delete() {
        springInstance.deleteSpringInContraption();
    }
}
