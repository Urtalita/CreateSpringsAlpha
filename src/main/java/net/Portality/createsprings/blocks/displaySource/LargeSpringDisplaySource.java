package net.Portality.createsprings.blocks.displaySource;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.largeSpring.ExtentionBlockEntity;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import net.Portality.createsprings.client.CSpringsLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class LargeSpringDisplaySource extends PercentOrProgressBarDisplaySource {
    @Override
    protected String getTranslationKey() {
        return "large_spring";
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        int mode = getMode(context);
        if (mode == 1)
            return super.formatNumeric(context, currentLevel);
        LangBuilder builder = CreateLang.number(currentLevel);
        if(mode == 4 || mode == 5){
            return builder.component();
        }
        if (isLBE(context))
            builder.space();
        return builder.translate("generic.unit.stress")
                .component();
    }

    private boolean isLBE(DisplayLinkContext context){
        BlockEntity sourceBE = context.getSourceBlockEntity();

        LargeSpringBlockEntity b;
        if (!(sourceBE instanceof ExtentionBlockEntity ebe)){
            if(!(sourceBE instanceof LargeSpringBlockEntity lbe)){return false;}
            b = lbe;
        } else {
            b = ebe.getOrFindBe();
            if(b == null){
                return false;
            }
        }
        return true;
    }

    @Override
    protected @Nullable Float getProgress(DisplayLinkContext context) {
        BlockEntity sourceBE = context.getSourceBlockEntity();

        LargeSpringBlockEntity b;
        if (!(sourceBE instanceof ExtentionBlockEntity ebe)){
            if(!(sourceBE instanceof LargeSpringBlockEntity lbe)){return 0f;}
            b = lbe;
        } else {
            b = ebe.getOrFindBe();
            if(b == null){
                return 0f;
            }
        }

        return (float) switch (getMode(context)) {
            case 0, 1 -> b.progress;
            case 2 -> b.stored;
            case 3 -> b.capacity;
            case 4 -> b.getCurLen();
            case 5 -> b.getLen();
            case 6 -> Math.abs(b.calculateStressApplied() * b.getSpeed());
            case 7 -> Math.abs(b.calculateAddedStressCapacity() * b.getGeneratedSpeed());
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
                                "progress_bar", "precent", "stored", "capacity", "len", "max_len", "stress", "gen"))
                        .titled(CSpringsLang.translateDirect("display_source.spring.display")), "Mode");
    }
}
