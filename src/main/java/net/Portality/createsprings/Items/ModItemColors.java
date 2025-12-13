package net.Portality.createsprings.Items;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSpeedSys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ModItemColors {
    public static void register() {
        ItemColors colors = Minecraft.getInstance().getItemColors();

        colors.register(
                ModItemColors::getColorForHat,
                ModItems.HAT
        );

        colors.register(
                ModItemColors::getHeatForSpringTool,
                ModItems.SPRING_DRILL
        );

        colors.register(
                ModItemColors::getHeatForSpringTool,
                ModItems.SPRING_SAW
        );

        colors.register(
                ModItemColors::getHeatForSpringTool,
                ModItems.SPRING_SHOVE
        );
    }

    private static int getHeatForSpringTool(ItemStack stack, int tintIndex){
        int red = (int) (stack.getOrCreateTag().getDouble("Speed") / SpringSpeedSys.MAX_OVERCLOCKED_SPEED * 255);
        return getRGB( 255, 255 - red, 255 - red);
    }

    private static int getColorForHat(ItemStack stack, int tintIndex) {
        if(tintIndex == 1){
            CompoundTag tag = stack.getOrCreateTag();
            if(!tag.contains("red")){
                return getRGB(255, 255, 255);
            }
            return getRGB(tag.getInt("red"), tag.getInt("green"), tag.getInt("blue"));
        }
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.contains("red1")){
            return getRGB(255, 255, 255);
        }
        return getRGB(tag.getInt("red1"), tag.getInt("green1"), tag.getInt("blue1"));
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
