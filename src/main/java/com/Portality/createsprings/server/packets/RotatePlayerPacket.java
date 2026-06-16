package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public record RotatePlayerPacket(float xRot) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, RotatePlayerPacket> STREAM_CODEC = ByteBufCodecs.FLOAT.map(
            RotatePlayerPacket::new, RotatePlayerPacket::xRot
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.ROTATE_PLAYER;
    }

    @Override
    public void handle(LocalPlayer player) {
        player.setXRot(xRot);
    }
}
