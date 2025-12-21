package net.Portality.createsprings.server;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

public class PushOffPacket extends SimplePacketBase {
    public PushOffPacket(FriendlyByteBuf buffer) {}

    public PushOffPacket() {}

    @Override
    public void write(FriendlyByteBuf buffer) {}

    @Override
    public boolean handle(NetworkEvent.Context context) {
        Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
        Minecraft.getInstance().player.addDeltaMovement(Minecraft.getInstance().player.getViewVector(1f).scale(-1));
        return true;
    }
}
