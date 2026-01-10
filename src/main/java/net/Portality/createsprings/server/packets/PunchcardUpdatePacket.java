package net.Portality.createsprings.server.packets;


import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardItem;
import net.Portality.createsprings.datagen.CSpringsAdvancements;
import net.Portality.createsprings.sounds.CSpringsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

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
                    if(stack.getOrCreateTag().getBoolean("Programmed") != updatedTag.getBoolean("Programmed")){
                        ctx.get().getSender().level().playSound(null, ctx.get().getSender().getOnPos(),
                                CSpringsSounds.PUNCHCARD.get(),
                                SoundSource.NEUTRAL, 1, 1F);
                        CSpringsAdvancements.PROGRAMMER.awardTo(player);

                        if(CheckForAchievement(updatedTag)){
                            CSpringsAdvancements.PEA_SHOOTER.awardTo(player);
                        }
                    }
                    stack.setTag(updatedTag);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean CheckForAchievement(CompoundTag tag){
        for (String key : tag.getAllKeys()){
            if(tag.getString(key).equals("potatoCannonShoot:0")){
                return true;
            }
        }
        return false;
    }
}
