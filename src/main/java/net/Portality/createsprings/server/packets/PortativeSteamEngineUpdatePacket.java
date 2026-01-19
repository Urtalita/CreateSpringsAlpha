package net.Portality.createsprings.server.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.datagen.CSpringsAdvancements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class PortativeSteamEngineUpdatePacket extends SimplePacketBase {
    private final CompoundTag updatedTag;

    public PortativeSteamEngineUpdatePacket(CompoundTag updatedTag) {
        this.updatedTag = updatedTag;
    }

    public PortativeSteamEngineUpdatePacket(FriendlyByteBuf buf) {
        this.updatedTag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(updatedTag);
    }

    @Override
    public boolean handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                InteractionHand hand = player.getUsedItemHand();
                ItemStack stack = player.getItemInHand(hand);
                if(stack.getItem() instanceof PortativeSteamEngineItem){
                    stack.setTag(updatedTag);
                }

                stack = player.getItemBySlot(PortativeSteamEngineItem.SLOT);
                if(stack.getItem() instanceof PortativeSteamEngineItem){
                    if(updatedTag.getInt("boosted") > 99 && !updatedTag.getBoolean("boost")){return;} //copper nugget dupe fix

                    stack.setTag(updatedTag);
                }

                if(stack.getOrCreateTag().getBoolean("boost")){
                    CSpringsAdvancements.OVERDRIVE.awardTo(player);
                }
            }
        });
        return true;
    }
}
