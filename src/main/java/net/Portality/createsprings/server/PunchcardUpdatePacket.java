package net.Portality.createsprings.server;


import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.function.Supplier;

public class PunchcardUpdatePacket {
    private final CompoundTag updatedTag;

    public PunchcardUpdatePacket(CompoundTag updatedTag) {
        this.updatedTag = updatedTag;
    }

    public PunchcardUpdatePacket(FriendlyByteBuf buf) {
        this.updatedTag = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        for (int i = 0; i < 5; i++) {
            buf.writeNbt(updatedTag);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                InteractionHand hand = player.getUsedItemHand();
                ItemStack stack = player.getItemInHand(hand);
                if(stack.getItem() instanceof PunchcardItem){
                    stack.setTag(updatedTag);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
