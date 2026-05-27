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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record PSEClientUpdate(ItemStack updated) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PSEClientUpdate> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            PSEClientUpdate::updated,
            PSEClientUpdate::new
    );
    @Override
    public void handle(LocalPlayer player) {
        if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemBySlot(EquipmentSlot.CHEST);
            engineStack.set(DataComponents.CUSTOM_DATA, updated.get(DataComponents.CUSTOM_DATA));
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSE_CLIENT_UPDATE;
    }
}
