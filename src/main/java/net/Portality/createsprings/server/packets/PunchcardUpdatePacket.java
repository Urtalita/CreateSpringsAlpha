package net.Portality.createsprings.server.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardItem;
import net.Portality.createsprings.datagen.CSpringsAdvancements;
import net.Portality.createsprings.sounds.CSpringsSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class PunchcardUpdatePacket extends SimplePacketBase {
    private final CompoundTag updatedTag;

    public PunchcardUpdatePacket(FriendlyByteBuf buffer) {
        updatedTag = buffer.readNbt();
    }

    public PunchcardUpdatePacket(CompoundTag tag) {
        updatedTag = tag;
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
                if(stack.getItem() instanceof PunchcardItem){
                    if(stack.getOrCreateTag().getBoolean("Programmed") != updatedTag.getBoolean("Programmed")){
                        ctx.getSender().level().playSound(null, ctx.getSender().getOnPos(),
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
        return true;
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
