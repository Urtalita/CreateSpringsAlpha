package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultTargetHandler;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlockEntity;
import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record CatapultPlacementPacket(BlockPos pos, BlockPos target, BlockPos secondTarget) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, CatapultPlacementPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, CatapultPlacementPacket::pos,
            // Используем ByteBufCodecs.optional для передачи null-значений
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), p -> java.util.Optional.ofNullable(p.target),
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), p -> java.util.Optional.ofNullable(p.secondTarget),
            (pos, targetOpt, secondOpt) -> new CatapultPlacementPacket(pos, targetOpt.orElse(null), secondOpt.orElse(null))
    );

    @Override
    public void handle(ServerPlayer player) {
        if (player == null)
            return;
        Level world = player.level();
        if (world == null || !world.isLoaded(pos))
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpringCatapultBlockEntity springCatapultBlockEntity) {
            if (target != null) {
                springCatapultBlockEntity.addTarget(target, true);

                if (secondTarget != null && !target.equals(secondTarget)) {
                    springCatapultBlockEntity.setTargetInQueue(secondTarget);
                }
            }
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.CATAPULT_TARGET;
    }

    public record ClientBoundRequest(BlockPos pos) implements ClientboundPacketPayload {
        public static final StreamCodec<ByteBuf, CatapultPlacementPacket.ClientBoundRequest> STREAM_CODEC = BlockPos.STREAM_CODEC.map(
                CatapultPlacementPacket.ClientBoundRequest::new, CatapultPlacementPacket.ClientBoundRequest::pos
        );

        @Override
        public PacketTypeProvider getTypeProvider() {
            return CSpringsPackets.CATAPULT_TARGET_CLIENT;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void handle(LocalPlayer player) {
            CatapultTargetHandler.flushSettings(pos);
        }
    }
}
