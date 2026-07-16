package com.Portality.createsprings.server.packets;

import com.Portality.createsprings.client.CSpringsGuiTextures;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record PSEServerUpdate(int state, boolean overdrive, int boosted) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PSEServerUpdate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PSEServerUpdate::state,
            ByteBufCodecs.BOOL,
            PSEServerUpdate::overdrive,
            ByteBufCodecs.INT,
            PSEServerUpdate::boosted,
            PSEServerUpdate::new
    );

    @Override
    public void handle(ServerPlayer player) {
        if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemBySlot(EquipmentSlot.CHEST);
            setData(engineStack, state, overdrive, boosted);
        }

        if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            setData(engineStack, state, overdrive, boosted);
        }

        if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof PortativeSteamEngineItem){
            ItemStack engineStack = player.getItemInHand(InteractionHand.OFF_HAND);
            setData(engineStack, state, overdrive, boosted);
        }
    }

    private void setData(ItemStack stack, int state, boolean overdrive, int boosted){
        if(PortativeSteamEngineItem.getSpeed(stack) > state * 15){
            stack.set(CSpringsDataComponents.ENGINE_SPEED, state * 15);
        }

        stack.set(CSpringsDataComponents.TARGET_SPEED, state * 15);
        stack.set(CSpringsDataComponents.ENGINE_MODE, state * 15);
        stack.set(CSpringsDataComponents.OVERDRIVE, overdrive);

        stack.set(CSpringsDataComponents.OVERDRIVE_PROGRESS, boosted);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSE_SERVER_UPDATE;
    }
}
