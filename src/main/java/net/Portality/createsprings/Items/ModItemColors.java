package net.Portality.createsprings.Items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ModItemColors {
    public static void register() {
        ItemColors colors = Minecraft.getInstance().getItemColors();

        colors.register(
                ModItemColors::getColorForTintIndex, // Красный для индекса 0
                ModItems.HAT // Убедитесь, что это правильный предмет
        );
    }

    private static int getColorForTintIndex(ItemStack stack, int tintIndex) {
        if(tintIndex == 1){
            CompoundTag tag = stack.getOrCreateTag();
            if(!tag.contains("red")){
                return getHatARGB(255, 255, 255);
            }
            return getHatARGB(tag.getInt("red"), tag.getInt("green"), tag.getInt("blue"));
        }
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.contains("red1")){
            return getHatARGB(255, 255, 255);
        }
        return getHatARGB(tag.getInt("red1"), tag.getInt("green1"), tag.getInt("blue1"));
    }

    public static int getHatARGB(int red, int green, int blue){
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
