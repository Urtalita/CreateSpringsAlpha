package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.*;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Vector;
import java.util.function.Function;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.checkItemInContains;
import static com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator.allPunchcardActions;

public record GrabPunchcard(Vector3f speed) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, GrabPunchcard> STREAM_CODEC = ByteBufCodecs.VECTOR3F.map(
            GrabPunchcard::new, GrabPunchcard::speed
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.GRAB_PUNCHCARD;
    }

    @Override
    public void handle(LocalPlayer player) {
        Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
        Minecraft.getInstance().player.addDeltaMovement(new Vec3(speed.x, speed.y, speed.z));
    }
}
