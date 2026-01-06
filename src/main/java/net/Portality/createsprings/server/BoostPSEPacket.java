package net.Portality.createsprings.server;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

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
