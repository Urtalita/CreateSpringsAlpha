package com.Portality.createsprings.client;


import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsPackets;
import com.Portality.createsprings.server.packets.*;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CreateSprings.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientForgeHandler {
    public static int shakingTicks = 0;
    public static final float DEAFULT_STRENGHT = (float) (Math.PI / 2);
    public static float strength = DEAFULT_STRENGHT;
    public static final float DECAY = 0.95f;

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event){
        if(CSpringsKeybindings.INSTANCE.PSEOpenKey.consumeClick()){

            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                PacketDistributor.sendToServer(OpenPSEPacket.INSTANCE);
            }
        }

        if(CSpringsKeybindings.INSTANCE.PSEBoostKey.consumeClick()){
            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                PacketDistributor.sendToServer(BoostPSEPacket.INSTANCE);
            }
        }

        if(CSpringsKeybindings.INSTANCE.PSEDashKey.consumeClick()){
            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                PacketDistributor.sendToServer(DashPSEPacket.INSTANCE);
            }
        }

        if(CSpringsKeybindings.INSTANCE.PSEReleaseKey.consumeClick()){
            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                PacketDistributor.sendToServer(ReleasePSEPacket.INSTANCE);
            }
        }

        if(CSpringsKeybindings.INSTANCE.ActivatePunchcard.consumeClick()){
            PacketDistributor.sendToServer(ActivatePunchcard.INSTANCE);
        }
    }

    public static void start(int ticks){
        shakingTicks = ticks;
        strength = DEAFULT_STRENGHT;
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if(shakingTicks > 0){
            long time = System.currentTimeMillis();

            float deltaPitch = (float) Math.sin(time * 0.05) * strength;
            float deltaYaw = (float) Math.cos(time * 0.05) * strength;

            event.setPitch(event.getPitch() + deltaPitch);
            event.setYaw(event.getYaw() + deltaYaw);
        }
    }
}