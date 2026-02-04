package net.Portality.createsprings.client;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.server.*;
import net.Portality.createsprings.server.packets.ActivatePunchcard;
import net.Portality.createsprings.server.packets.BoostPSEPacket;
import net.Portality.createsprings.server.packets.DashPSEPacket;
import net.Portality.createsprings.server.packets.OpenPSEPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    public static int shakingTicks = 0;
    public static final float DEAFULT_STRENGHT = (float) (Math.PI / 2);
    public static float strength = DEAFULT_STRENGHT;
    public static final float DECAY = 0.95f;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        if(Keybindings.INSTANCE.PSEOpenKey.consumeClick()){

            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                CSpringsPackets.getChannel().send(PacketDistributor.SERVER.noArg(), new OpenPSEPacket());
            }
        }

        if(Keybindings.INSTANCE.PSEBoostKey.consumeClick()){
            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                CSpringsPackets.getChannel().send(PacketDistributor.SERVER.noArg(), new BoostPSEPacket());
            }
        }

        if(Keybindings.INSTANCE.PSEDashKey.consumeClick()){
            PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
            if(item != null){
                CSpringsPackets.getChannel().send(PacketDistributor.SERVER.noArg(), new DashPSEPacket());
            }
        }

        if(Keybindings.INSTANCE.ActivatePunchcard.consumeClick()){
            CSpringsPackets.getChannel().send(PacketDistributor.SERVER.noArg(), new ActivatePunchcard());
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

            // Пример простой тряски через синус
            float deltaPitch = (float) Math.sin(time * 0.05) * strength;
            float deltaYaw = (float) Math.cos(time * 0.05) * strength;

            event.setPitch(event.getPitch() + deltaPitch);
            event.setYaw(event.getYaw() + deltaYaw);
        }
    }
}