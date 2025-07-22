package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.ConnectedToPSKIInfo;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;

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
}
