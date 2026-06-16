package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.*;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Function;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.checkItemInContains;
import static com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator.allPunchcardActions;

public enum AirDashPlayerPacket implements ClientboundPacketPayload {
    INSTANCE;

    public static final StreamCodec<ByteBuf, AirDashPlayerPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.ACTIVATE_PUNCHCARD;
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnParticles() {
        LocalPlayer player = Minecraft.getInstance().player;
        player.addDeltaMovement(new Vec3(0, 0.8f, 0));
        RandomSource source = player.level().getRandom();
        for(int i = 0; i < 7; i++){
            Direction facing = Direction.DOWN;
            Vec3 offset = VecHelper.rotate((new Vec3(0.0, 0.0, 1.0)).add(VecHelper.offsetRandomly(Vec3.ZERO, source, 1.0F).multiply(1.0, 1.0, 0.0).normalize().scale(0.5)), (double) AngleHelper.verticalAngle(facing), Direction.Axis.X);
            offset = VecHelper.rotate(offset, AngleHelper.horizontalAngle(facing), Direction.Axis.Y);
            Vec3 v = offset.scale(0.5).add(player.position().add(0, 1.5f, 0));
            Vec3 m = offset.subtract(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.75));
            player.level().addParticle(new SteamJetParticleData(1.0F), v.x, v.y, v.z, m.x, m.y, m.z);
        }
    }

    @Override
    public void handle(LocalPlayer player) {
        spawnParticles();
    }
}
