package net.Portality.createsprings.server;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultTargetHandler;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class CatapultPlacementPacket extends SimplePacketBase {
    private BlockPos pos;
    private BlockPos target;
    private BlockPos secondTarget;

    public CatapultPlacementPacket(BlockPos pos, BlockPos secondPos, BlockPos target) {
        this.pos = pos;
        this.target = target;
        this.secondTarget = secondPos;
    }

    public CatapultPlacementPacket(BlockPos pos, BlockPos target) {
        this.pos = pos;
        this.target = target;
        this.secondTarget = target;
    }

    public CatapultPlacementPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        target = buffer.readBlockPos();
        secondTarget = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBlockPos(target);
        buffer.writeBlockPos(secondTarget);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
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
        });
        return true;
    }

    public static class ClientBoundRequest extends SimplePacketBase {

        BlockPos pos;

        public ClientBoundRequest(BlockPos pos) {
            this.pos = pos;
        }

        public ClientBoundRequest(FriendlyByteBuf buffer) {
            this.pos = buffer.readBlockPos();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeBlockPos(pos);
        }

        @Override
        public boolean handle(NetworkEvent.Context context) {

            context.enqueueWork(
                    () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CatapultTargetHandler.flushSettings(pos)));

            return true;
        }

    }
}
