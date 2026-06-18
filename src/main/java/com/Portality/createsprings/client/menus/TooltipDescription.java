package com.Portality.createsprings.client.menus;

import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import static net.minecraft.ChatFormatting.*;

public class TooltipDescription extends AbstractSimiWidget {

    private Supplier<ArrayList<Component>> tip;
    private String nameKey;
    public ArrayList<AbstractSimiWidget> borderingWidgets;

    public TooltipDescription(int x, int y, int w, int h, Supplier<ArrayList<Component>> tip, String nameKey) {
        this(x, y, w, h, tip, nameKey, new ArrayList<>());
    }

    public TooltipDescription(int x, int y, int w, int h, Supplier<ArrayList<Component>> tip, String nameKey, ArrayList<AbstractSimiWidget> intersectingWidgets) {
        super(x, y, w, h);
        this.tip = tip;
        this.nameKey = "createsprings.tooltip." + nameKey;
        this.borderingWidgets = intersectingWidgets;
    }

    @Override
    public void tick() {
        super.tick();
        toolTip.clear();
        boolean shift = AllKeys.shiftDown();
        toolTip.addAll(addShiftThing());
        toolTip.add(Component.translatable(nameKey).withStyle(YELLOW));
        if(!shift){return;}
        toolTip.addAll(tip.get());
    }

    public void updateIntersected(AbstractSimiWidget[] updated){
        borderingWidgets.clear();
        borderingWidgets.addAll(Arrays.asList(updated));
    }

    public void updateIntersected(AbstractSimiWidget updated){
        borderingWidgets.clear();
        borderingWidgets.add(updated);
    }

    public void updateIntersected(ArrayList updated){
        borderingWidgets.clear();
        borderingWidgets.addAll(updated);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if(intersectingWidget(mouseX, mouseY)) return;
        super.renderTooltip(graphics, mouseX, mouseY, partialTicks);
    }

    private boolean intersectingWidget(int mouseX, int mouseY){
        if(!isHovered()) return false;
        if(borderingWidgets.isEmpty()) return false;

        for(AbstractSimiWidget widget : borderingWidgets){
            if(pointingOnWidget(widget, mouseX, mouseY)) return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for(AbstractSimiWidget widget : borderingWidgets){
            if(pointingOnWidget(widget, (int) mouseX, (int) mouseY)){
                if(widget instanceof ScrollInput input){
                    return input.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public static boolean pointingOnWidget(AbstractSimiWidget firstWidget, int mouseX, int mouseY){
        return firstWidget.isHovered();
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
