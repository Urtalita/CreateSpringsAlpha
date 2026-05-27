package com.Portality.createsprings.server.packets;
import com.Portality.createsprings.client.menus.PortativeEngine.PortativeSteamEngineMenu;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsPackets;
import com.simibubi.create.foundation.networking.LeftClickPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum OpenPSEPacket implements MenuProvider, ServerboundPacketPayload {
    INSTANCE;

    public static final StreamCodec<ByteBuf, OpenPSEPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Component getDisplayName() {
        return Component.literal("");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        if(!(heldItem.getItem() instanceof PortativeSteamEngineItem)){
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            return PortativeSteamEngineMenu.create(id, inv, stack);
        }
        return PortativeSteamEngineMenu.create(id, inv, heldItem);
    }

    @Override
    public void handle(ServerPlayer player) {
        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(player);
        if(item != null){
            ItemStack stack = player.getItemBySlot(PortativeSteamEngineItem.SLOT);
            player.openMenu(this, buf -> {
                ItemStack.STREAM_CODEC.encode(buf, stack);
            });
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CSpringsPackets.PSE_OPEN;
    }
}
