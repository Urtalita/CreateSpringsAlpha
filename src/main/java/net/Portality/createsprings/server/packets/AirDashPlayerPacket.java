package net.Portality.createsprings.server.packets;

import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class AirDashPlayerPacket extends SimplePacketBase {

    public AirDashPlayerPacket(FriendlyByteBuf buffer) {

    }

    public AirDashPlayerPacket() {

    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {

    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            spawnParticles();
        }));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnParticles() {
        LocalPlayer player = Minecraft.getInstance().player;
        player.addDeltaMovement(new Vec3(0, 0.8f, 0));
        RandomSource source = player.level().getRandom();
        for(int i = 0; i < 7; i++){
            Direction facing = Direction.DOWN;
            Vec3 offset = VecHelper.rotate((new Vec3(0.0, 0.0, 1.0)).add(VecHelper.offsetRandomly(Vec3.ZERO, source, 1.0F).multiply(1.0, 1.0, 0.0).normalize().scale(0.5)), (double) AngleHelper.verticalAngle(facing), Direction.Axis.X);
            offset = VecHelper.rotate(offset, AngleHelper.horizontalAngle(facing), Direction.Axis.Y);
            Vec3 v = offset.scale(0.5).add(player.position().add(0, 1.5f, 0));
            Vec3 m = offset.subtract(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.75));
            player.level().addParticle(new SteamJetParticleData(1.0F), v.x, v.y, v.z, m.x, m.y, m.z);
        }
    }
}
