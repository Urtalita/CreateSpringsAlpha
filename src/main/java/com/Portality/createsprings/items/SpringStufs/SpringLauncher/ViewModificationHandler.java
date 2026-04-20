package com.Portality.createsprings.items.SpringStufs.SpringLauncher;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

public class ViewModificationHandler {
    @SubscribeEvent
    public static void onFovUpdate(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        ItemStack usingItem = player.getUseItem();


        if (usingItem.getItem() instanceof SpringLauncher) {
            if(((SpringLauncher) usingItem.getItem()).isSpyglass(event)){
                float fov = event.getNewFovModifier();
                event.setNewFovModifier(fov * SpringLauncher.ZOOM_FOV_MODIFIER);
            }
        }
    }
}
