package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public enum DashPSEPacket implements ServerboundPacketPayload {
    INSTANCE;
    public static final StreamCodec<ByteBuf, DashPSEPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void handle(ServerPlayer player) {
        PortativeSteamEngineItem.steamDash(player, player.serverLevel());
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSE_DASH;
    }
}
