package net.Portality.createsprings.server.packets;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.menus.PortativeEngine.PortativeSteamEngineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class OpenPSEPacket extends SimplePacketBase implements MenuProvider {

    public OpenPSEPacket(FriendlyByteBuf buffer) {}

    public OpenPSEPacket() {}

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(player);
        if(item != null){
            ItemStack stack = player.getItemBySlot(PortativeSteamEngineItem.SLOT);
            NetworkHooks.openScreen(player, this, (buf) -> {
                buf.writeItem(stack);
            });
            return true;
        }
        return false;
    }

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
}
