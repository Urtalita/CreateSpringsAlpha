package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record PSEClientUpdate(CompoundTag updated) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PSEClientUpdate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            PSEClientUpdate::updated,
            PSEClientUpdate::new
    );

    @Override
    public void handle(LocalPlayer player) {
        if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemBySlot(EquipmentSlot.CHEST);

            CustomData customData = CustomData.of(updated);
            engineStack.set(DataComponents.CUSTOM_DATA, customData);
        }

        if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            CustomData customData = CustomData.of(updated);
            engineStack.set(DataComponents.CUSTOM_DATA, customData);
        }

        if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemInHand(InteractionHand.OFF_HAND);

            CustomData customData = CustomData.of(updated);
            engineStack.set(DataComponents.CUSTOM_DATA, customData);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSE_CLIENT_UPDATE;
    }
}
