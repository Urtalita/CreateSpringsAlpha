package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public enum PushOffPacket implements ClientboundPacketPayload {
    INSTANCE;

    public static final StreamCodec<ByteBuf, PushOffPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PUSH_OFF_PACKET;
    }

    @Override
    public void handle(LocalPlayer player) {
        Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
        Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.getViewVector(1f).scale(-1));
    }
}
