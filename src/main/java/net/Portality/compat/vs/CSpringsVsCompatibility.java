package net.Portality.compat.vs;

import net.Portality.createsprings.server.SpringSplashEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3d;
import org.valkyrienskies.mod.api.ValkyrienSkies;

public class CSpringsVsCompatibility {
    @SubscribeEvent
    public static void onSpringSplash(SpringSplashEvent event) {
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        SpringForceData data = new SpringForceData(
                event.getDir(),
                event.getPos(),
                event.getSuSecSplashed() / 100f,
                5
        );

        Direction direction = Direction.fromDelta((int) event.getDir().x, (int) event.getDir().y, (int) event.getDir().z);
        Vec3 impactBP = event.getPos().relative(direction).getCenter();
        SpringForceAttachment attachment = SpringForceAttachment.get(serverLevel, new Vector3d(impactBP.x, impactBP.y, impactBP.z));

        if(attachment != null){
            attachment.addSpringThruster(BlockPos.ZERO, data);
        }
    }
}
