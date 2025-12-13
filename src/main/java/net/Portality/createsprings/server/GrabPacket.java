package net.Portality.createsprings.server;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

public class GrabPacket extends SimplePacketBase {
    private final Vector3f speed;

    public GrabPacket(FriendlyByteBuf buffer) {
        speed = buffer.readVector3f();
    }

    public GrabPacket(Vec3 speed) {
        this.speed = speed.toVector3f();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVector3f(speed);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
        Minecraft.getInstance().player.addDeltaMovement(new Vec3(speed.x, speed.y, speed.z));
        return true;
    }
}
