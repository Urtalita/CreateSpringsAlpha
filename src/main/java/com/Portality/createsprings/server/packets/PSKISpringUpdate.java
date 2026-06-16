package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringMovement;
import com.Portality.createsprings.blocks.advanced.spring.SpringMovement;
import com.Portality.createsprings.server.CSpringsPackets;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.UUID;

public record PSKISpringUpdate(UUID contraption, BlockPos localPos, CompoundTag updatedEntity) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PSKISpringUpdate> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            PSKISpringUpdate::contraption,
            BlockPos.STREAM_CODEC,
            PSKISpringUpdate::localPos,
            ByteBufCodecs.COMPOUND_TAG,
            PSKISpringUpdate::updatedEntity,
            PSKISpringUpdate::new
    );

    @Override
    public void handle(LocalPlayer player) {
        MovementBehaviour behaviour = MovementBehaviour.REGISTRY.get(CSpringsBlocks.SPRING.get());
        if(behaviour instanceof SpringMovement springMovement){
            springMovement.setProgress(contraption, localPos, updatedEntity);
        }

        behaviour = MovementBehaviour.REGISTRY.get(CSpringsBlocks.LARGE_SPRING.get());
        if(behaviour instanceof LargeSpringMovement springMovement){
            springMovement.setProgress(contraption, localPos, updatedEntity);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSKI_SPRING_UPDATE;
    }
}
