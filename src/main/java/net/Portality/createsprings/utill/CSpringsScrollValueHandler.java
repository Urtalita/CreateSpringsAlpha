package net.Portality.createsprings.utill;


import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.PhysicalFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CSpringsScrollValueHandler {

    public static float getScroll(ItemStack stack, float partialTicks, float speed) {
        CompoundTag tag = stack.getOrCreateTag();
        return Mth.lerp(partialTicks, tag.getFloat("LastScroll"), tag.getFloat("Scroll"));
    }

    @OnlyIn(Dist.CLIENT)
    public static void tick(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        float lastScroll = tag.getFloat("Scroll");

        tag.putFloat("LastScroll", lastScroll);
        tag.putFloat("Scroll", lastScroll + Mth.lerp((Minecraft.getInstance().level.getGameTime() - 1) % 40 / 40f ,tag.getFloat("LastSpeed"), tag.getFloat("Speed")));
    }
}
