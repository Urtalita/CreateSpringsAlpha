package net.Portality.createsprings.blocks.displaySource;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.displayLink.target.NixieTubeDisplayTarget;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.stream.Stream;

public class SpringDisplaySource extends NumericSingleLineDisplaySource {
    @Override
    protected String getTranslationKey() {
        return "spring";
    }

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof SpringBlockEntity sbe))
            return EMPTY_LINE;

        MutableComponent text = Component.literal(String.valueOf(Math.round(sbe.getStored())));

        try {
            String line = text.getString();
            Integer.valueOf(line);
            context.flapDisplayContext = Boolean.TRUE;
        } catch (NumberFormatException e) {
        }

        return text;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        if (isNumeric(context))
            return "Charge";
        return super.getFlapDisplayLayoutName(context);
    }

    protected boolean isNumeric(DisplayLinkContext context) {
        return context.flapDisplayContext == Boolean.TRUE;
    }
}
