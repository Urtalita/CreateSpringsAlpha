package net.Portality.createsprings.utill;


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
        if (!tag.contains("LastScroll")) {
            tag.putFloat("LastScroll", 0.0f);
            tag.putFloat("Scroll", 0.0f);
        }
        return Mth.lerp(partialTicks, tag.getFloat("LastScroll"), tag.getFloat("Scroll"));
    }

    @OnlyIn(Dist.CLIENT)
    public static void tick(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        float lastScroll = tag.getFloat("Scroll");
        float speed = tag.getFloat("Speed");

        if (speed != 0) {
            float tickDelta = Minecraft.getInstance().getDeltaFrameTime();
            tag.putFloat("LastScroll", lastScroll);
            tag.putFloat("Scroll", lastScroll + speed * tickDelta);
        }
    }
}
