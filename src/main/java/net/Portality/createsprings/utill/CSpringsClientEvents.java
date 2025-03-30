package net.Portality.createsprings.utill;

import com.simibubi.create.foundation.events.ClientEvents;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringDrill.SpringDrill;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSaw.SpringSaw;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSpeedSys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.createmod.ponder.PonderClient.isGameActive;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CSpringsClientEvents {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (!isGameActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            for (ItemStack stack : mc.player.getAllSlots()) {
                if (stack.getItem() instanceof SpringDrill || stack.getItem() instanceof SpringSaw) {
                    CSpringsScrollValueHandler.tick(stack);
                }
            }
        }
    }


}
