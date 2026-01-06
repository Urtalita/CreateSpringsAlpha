package net.Portality.createsprings.client;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.server.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
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
}
