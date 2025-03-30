package net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher;

import cpw.mods.modlauncher.Launcher;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class MouseSensitivityHandler {
    private static final float ZOOM_SENSITIVITY_MODIFIER = 0.1f;
    private static double originalSensitivity = -1;
    private static boolean isZooming = false;

    // Отслеживаем использование предмета
    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        // Добавляем проверку на клиентскую сторону и тип предмета
        if (event.getEntity().level().isClientSide
                && event.getItem().getItem() instanceof SpringLauncher) {

            Minecraft minecraft = Minecraft.getInstance();
            if(((SpringLauncher) event.getItem().getItem()).isSpyglass(event)){
                if (minecraft.options != null) {
                    if(((SpringLauncher) event.getItem().getItem()).isSpyglass(event)){
                        originalSensitivity = minecraft.options.sensitivity().get();
                        minecraft.options.sensitivity().set(originalSensitivity * ZOOM_SENSITIVITY_MODIFIER);
                    }
                }
            }
        }
    }

    // Восстанавливаем чувствительность
    @SubscribeEvent
    public static void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getItem().getItem() instanceof SpringLauncher
                && !event.getEntity().level().isClientSide) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options != null && originalSensitivity != -1) {
            minecraft.options.sensitivity().set(originalSensitivity);
            originalSensitivity = -1;
        }
    }
}