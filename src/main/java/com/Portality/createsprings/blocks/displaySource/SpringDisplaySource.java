package com.Portality.createsprings.blocks.displaySource;

import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import com.Portality.createsprings.client.CSpringsLang;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class SpringDisplaySource extends PercentOrProgressBarDisplaySource {
    @Override
    protected String getTranslationKey() {
        return "spring";
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        int mode = getMode(context);
        if (mode == 1)
            return super.formatNumeric(context, currentLevel);
        LangBuilder builder = CreateLang.number(currentLevel);
        if (isBE(context))
            builder.space();
        return builder.translate("generic.unit.stress")
                .component();
    }

    private boolean isBE(DisplayLinkContext context){
        BlockEntity sourceBE = context.getSourceBlockEntity();

        if (!(sourceBE instanceof SpringBlockEntity sbe)){
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable Float getProgress(DisplayLinkContext context) {
        BlockEntity sourceBE = context.getSourceBlockEntity();

        if (!(sourceBE instanceof SpringBlockEntity b)){
            return 0f;
        }

        return (float) switch (getMode(context)) {
            case 0, 1 -> b.getProgress(0);
            case 2 -> b.stored;
            case 3 -> b.capacity;
            case 4 -> Math.abs(b.calculateStressApplied() * b.getSpeed());
            case 5 -> Math.abs(b.calculateAddedStressCapacity() * b.getGeneratedSpeed());
            default -> 0f;
        };
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return getMode(context) == 0;
    }

    private int getMode(DisplayLinkContext context) {
        return context.sourceConfig()
                .getInt("Mode");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;
        builder.addSelectionScrollInput(0, 120,
                (si, l) -> si
                        .forOptions(CSpringsLang.translatedOptions("display_source.spring",
                                "progress_bar", "precent", "stored", "capacity", "stress", "gen"))
                        .titled(CSpringsLang.translateDirect("display_source.spring.display")), "Mode");
    }
}
