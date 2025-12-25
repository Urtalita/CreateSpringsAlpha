package net.Portality.createsprings.server.packets;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.Spring.SpringMovement;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringMovement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class PSKISpringUpdate extends SimplePacketBase {
    private final UUID contraption;
    private final BlockPos localPos;
    private final CompoundTag updatedEntity;

    public PSKISpringUpdate(FriendlyByteBuf buffer) {
        contraption = buffer.readUUID();
        localPos = buffer.readBlockPos();
        updatedEntity = buffer.readNbt();
    }

    public PSKISpringUpdate(UUID contraption, BlockPos localPos, BlockEntity updatedEntity) {
        this.contraption = contraption;
        this.localPos = localPos;
        this.updatedEntity = updatedEntity.saveWithoutMetadata();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(contraption);
        friendlyByteBuf.writeBlockPos(localPos);
        friendlyByteBuf.writeNbt(updatedEntity);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        MovementBehaviour behaviour = MovementBehaviour.REGISTRY.get(ModBlocks.SPRING.get());
        if(behaviour instanceof SpringMovement springMovement){
            springMovement.setProgress(contraption, localPos, updatedEntity);
        }

        behaviour = MovementBehaviour.REGISTRY.get(ModBlocks.LARGE_SPRING.get());
        if(behaviour instanceof LargeSpringMovement springMovement){
            springMovement.setProgress(contraption, localPos, updatedEntity);
        }
        return true;
    }
}
