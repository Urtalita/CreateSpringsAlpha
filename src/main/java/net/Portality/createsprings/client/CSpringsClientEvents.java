package net.Portality.createsprings.client;

import net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine.EngineArmorLayer;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultTargetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.createmod.ponder.PonderClient.isGameActive;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CSpringsClientEvents {
    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance()
                    .getEntityRenderDispatcher();
            EngineArmorLayer.registerOnAll(dispatcher);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (!isGameActive())
            return;

        Level world = Minecraft.getInstance().level;

        CatapultTargetHandler.tick();
    }
}
