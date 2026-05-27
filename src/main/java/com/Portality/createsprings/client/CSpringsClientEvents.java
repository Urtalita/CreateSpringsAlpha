package com.Portality.createsprings.client;

import com.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultTargetHandler;
import com.Portality.createsprings.items.SpringStufs.ClientSpringAnimation;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.EngineArmorLayer;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.EngineBrokenArmorLayer;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import static net.createmod.ponder.PonderClient.isGameActive;

@EventBusSubscriber(Dist.CLIENT)
public class CSpringsClientEvents {

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance()
                    .getEntityRenderDispatcher();
            EngineArmorLayer.registerOnAll(dispatcher);
            EngineBrokenArmorLayer.registerOnAll(dispatcher);
        }
    }

    @SubscribeEvent
    public static void onTick(LevelTickEvent.Post event) {
        if (!event.getLevel().isClientSide()) return;

        if (!isGameActive())
            return;

        CatapultTargetHandler.tick();
        SpringSpeedClientHandler.onTick();
        ClientSpringAnimation.onTick();


        if(ClientForgeHandler.shakingTicks > 0){
            ClientForgeHandler.shakingTicks--;
            ClientForgeHandler.strength = ClientForgeHandler.DECAY * ClientForgeHandler.strength;
        }
    }
}