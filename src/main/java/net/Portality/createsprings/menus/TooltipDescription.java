package net.Portality.createsprings.menus;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

public class TooltipDescription extends AbstractSimiWidget {

    private Supplier<ArrayList<Component>> tip;

    public TooltipDescription(int x, int y, int w, int h, Supplier<ArrayList<Component>> tip) {
        super(x, y, w, h);
        this.tip = tip;
    }

    @Override
    public void tick() {
        super.tick();
        toolTip.clear();
        boolean shift = AllKeys.shiftDown();
        toolTip.addAll(addShiftThing());
        if(!shift){return;}
        toolTip.addAll(tip.get());
    }

    public static ArrayList<Component> addShiftThing(){
        ArrayList<Component> toolTip = new ArrayList<>();
        String[] holdDesc = CreateLang.translateDirect("tooltip.holdForDescription", "$")
                .getString()
                .split("\\$");
        MutableComponent keyShift = CreateLang.translateDirect("tooltip.keyShift");
        boolean shift = AllKeys.shiftDown();

        MutableComponent tabBuilder = Component.empty();
        tabBuilder.append(Component.literal(holdDesc[0]).withStyle(DARK_GRAY));
        tabBuilder.append(keyShift.plainCopy()
                .withStyle(shift ? WHITE : GRAY));
        tabBuilder.append(Component.literal(holdDesc[1]).withStyle(DARK_GRAY));

        toolTip.add(tabBuilder);
        return toolTip;
    }

    public static ArrayList<Component> splitAndFormat(MutableComponent component){
        int chunkSize = 7;
        ArrayList<Component> chunks = new ArrayList<>();
        String[] words = component.getString().split(" ");
        int length = words.length;

        for (int i = 0; i < length; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, length);
            StringBuilder chunk = new StringBuilder();
            for(int k = i; k < endIndex; k++){
                chunk.append(" ").append(words[k]);
            }
            chunks.add(formatStr(Component.literal(chunk.toString())));
        }
        return chunks;
    }

    public static Component formatStr(Component start){
        String[] startString = start.getString().split("_");
        MutableComponent end = Component.empty();

        int counter = 0;
        for(String subString : startString){
            if(counter % 2 == 0){
                end.append(Component.literal(subString).withStyle(GOLD));
            } else {
                end.append(Component.literal(subString).withStyle(YELLOW));
            }
            counter++;
        }
        return end;
    }
}
