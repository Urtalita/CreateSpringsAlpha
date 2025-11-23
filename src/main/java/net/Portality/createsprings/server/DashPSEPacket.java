package net.Portality.createsprings.server;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class DashPSEPacket extends SimplePacketBase {

    public DashPSEPacket(FriendlyByteBuf buffer) {}

    public DashPSEPacket() {}

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        PortativeSteamEngineItem.steamDash(player, context.getSender().serverLevel());
        return false;
    }
}
