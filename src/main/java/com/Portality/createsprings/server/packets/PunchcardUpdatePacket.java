package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.datagen.advancement.CSpringsAdvancements;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record PunchcardUpdatePacket(CompoundTag updatedTag) implements ServerboundPacketPayload {

    public static final StreamCodec<ByteBuf, PunchcardUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, PunchcardUpdatePacket::updatedTag,
            PunchcardUpdatePacket::new
    );

    public boolean CheckForAchievement(CompoundTag tag){
        for (String key : tag.getAllKeys()){
            if(tag.getString(key).equals("potatoCannonShoot:0")){
                return true;
            }
        }
        return false;
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player != null) {
            InteractionHand hand = player.getUsedItemHand();
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getItem() instanceof PunchcardItem){
                if(stack.getOrDefault(CSpringsDataComponents.PUNCHCARD, new CompoundTag()).getBoolean("Programmed") != updatedTag.getBoolean("Programmed")) {
                    player.level().playSound(null, player.getOnPos(),
                            CSpringsSounds.PUNCHCARD.get(),
                            SoundSource.NEUTRAL, 1, 1F);
                    CSpringsAdvancements.PROGRAMMER.awardTo(player);

                    if (CheckForAchievement(updatedTag)) {
                        CSpringsAdvancements.PEA_SHOOTER.awardTo(player);
                    }

                    player.setItemInHand(hand, CSpringsDataComponents.punchcardFromTag(updatedTag, player.serverLevel()));
                }
            }
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PUNCHCARD_UPDATE;
    }
}
