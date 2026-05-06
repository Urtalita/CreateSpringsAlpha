package com.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;

import java.util.UUID;

public class LargeSpringActorVisual extends ActorVisual {
    private LargeSpringInstance largeSpringInstance;
    public float progress = 0;
    public float prevProgress = 0;

    public LargeSpringActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        super(visualizationContext, world, context);
        largeSpringInstance = new LargeSpringInstance(visualizationContext.instancerProvider(), context.state, context.localPos, false);

        CompoundTag be = context.contraption.getActorAt(context.localPos).left.nbt();
        if(be != null){
            progress = 1 - be.getFloat("progres");
            prevProgress = progress;
        }

        setlight(largeSpringInstance.down_plate);
        setlight(largeSpringInstance.up_plate);
        largeSpringInstance.rings_corners.forEach(this::setlight);
        largeSpringInstance.rings.forEach(this::setlight);
    }

    private void setlight(OrientedInstance orientedInstance) {
        orientedInstance.light(localBlockLight(), 0);
    }

    @Override
    public void beginFrame() {
        super.beginFrame();
        largeSpringInstance.animate(context.state.getValue(LargeSpringBlock.LEN) * 4, Mth.lerp(AnimationTickHolder.getPartialTicks(), prevProgress, progress));
    }

    @Override
    protected void _delete() {
        largeSpringInstance.deleteSpring();
    }

    public void setProgress(UUID contraption, BlockPos localPos, CompoundTag updatedEntity) {
        if(context.contraption.entity != null){
            if(contraption.equals(context.contraption.entity.getUUID())){
                if(localPos.equals(context.localPos)){
                    float prog = 1 - updatedEntity.getFloat("stored") / updatedEntity.getFloat("capacity");
                    this.prevProgress = this.progress;
                    this.progress = prog;
                }
            }
        }
    }
}
