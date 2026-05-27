package com.Portality.createsprings.items;

import com.Portality.createsprings.items.SpringStufs.SpringSpeedSys;
import com.Portality.createsprings.items.advanced.hat.ColorComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class CSpringsItemColors {
    public static void register() {
        ItemColors colors = Minecraft.getInstance().getItemColors();

        colors.register(
                CSpringsItemColors::getColorForHat,
                CSpringsItems.HAT
        );

        colors.register(
                CSpringsItemColors::getHeatForSpringTool,
                CSpringsItems.SPRING_DRILL
        );

        colors.register(
                CSpringsItemColors::getHeatForSpringTool,
                CSpringsItems.SPRING_SAW
        );

        colors.register(
                CSpringsItemColors::getHeatForSpringTool,
                CSpringsItems.SPRING_SHOVE
        );
    }

    private static int getHeatForSpringTool(ItemStack stack, int tintIndex){
        int red = (int) (SpringSpeedSys.getSpeed(stack) / SpringSpeedSys.MAX_OVERCLOCKED_SPEED * 255);
        return getRGB( 255, 255 - red, 255 - red);
    }

    private static int getColorForHat(ItemStack stack, int tintIndex) {

        if (tintIndex == 1) {
            Color color = ColorComponent.getColour(stack);
            return getRGB(color.getRed(), color.getGreen(), color.getBlue());
        }

        return getRGB(0, 0, 0);
    }

    public static int getRGB(int red, int green, int blue){
        return getARGB(255, red, green, blue);
    }

    public static int getARGB(int alpha, int red, int green, int blue) {
        // Обрезаем значения до 8 бит и составляем цвет
        return ((alpha & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8)  |
                (blue & 0xFF);
    }
}
