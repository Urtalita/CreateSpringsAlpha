package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public enum BoostPSEPacket implements ServerboundPacketPayload {
    INSTANCE;

    public static final StreamCodec<ByteBuf, BoostPSEPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void handle(ServerPlayer player) {
        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(player);
        if(item != null){
            ItemStack stack = player.getItemBySlot(PortativeSteamEngineItem.SLOT);
            int boosted = PortativeSteamEngineItem.getOverdriveProgress(stack);
            if(boosted < 99){
                if(!PortativeSteamEngineItem.getOverdrive(stack)){
                    if(PortativeSteamEngineItem.getSpeed(stack) == 0){
                        return;
                    }
                }

                stack.set(CSpringsDataComponents.OVERDRIVE, !PortativeSteamEngineItem.getOverdrive(stack));
                if(boosted <= 0){
                    stack.set(CSpringsDataComponents.OVERDRIVE_PROGRESS, 1);
                }
            }
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSE_BOOST;
    }
}
