package net.Portality.createsprings.utill;

import net.Portality.createsprings.Items.advanced.SpringStufs.SpringDrill.SpringDrill;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSaw.SpringSaw;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringShowel.SpringShove;
import net.Portality.createsprings.server.hat.ServerHatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
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
            ItemStack stack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
            animationTick(stack);
            stack = mc.player.getItemInHand(InteractionHand.OFF_HAND);
            animationTick(stack);
        }
    }

    private static void animationTick(ItemStack stack){
        Item item = stack.getItem();
        if (item instanceof SpringDrill || item instanceof SpringSaw || item instanceof SpringShove) {
            CSpringsScrollValueHandler.tick(stack);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        ServerHatHandler.tick();
    }
}
