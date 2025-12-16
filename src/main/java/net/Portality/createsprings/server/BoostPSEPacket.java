package net.Portality.createsprings.server;

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
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class BoostPSEPacket extends SimplePacketBase {

    public BoostPSEPacket(FriendlyByteBuf buffer) {}

    public BoostPSEPacket() {}

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(player);
        if(item != null){
            ItemStack stack = player.getItemBySlot(PortativeSteamEngineItem.SLOT);
            int boosted = stack.getOrCreateTag().getInt("boosted");
            if(boosted < 99){
                if(!stack.getOrCreateTag().getBoolean("boost")){
                    if(stack.getOrCreateTag().getFloat("targetSpeed") == 0){
                        return true;
                    }
                }

                stack.getOrCreateTag().putBoolean("boost", !stack.getOrCreateTag().getBoolean("boost"));
                if(boosted <= 0){
                    stack.getOrCreateTag().putInt("boosted", 1);
                }
            }
            return true;
        }
        return false;
    }
}
