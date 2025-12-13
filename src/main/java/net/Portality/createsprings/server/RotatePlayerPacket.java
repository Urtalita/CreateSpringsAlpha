package net.Portality.createsprings.server;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class RotatePlayerPacket extends SimplePacketBase {
    private final float xRot;

    public RotatePlayerPacket(FriendlyByteBuf buffer) {
        xRot = buffer.readFloat();
    }

    public RotatePlayerPacket(float xRot) {
        this.xRot = xRot;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(xRot);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        Player player = Minecraft.getInstance().player;
        player.setXRot(xRot);
        return false;
    }
}
