package net.Portality.compat.vs;

import net.Portality.createsprings.server.SpringSplashEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class CSpringsVsCompatibility {
    @SubscribeEvent
    public static void onSpringSplash(SpringSplashEvent event) {
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        SpringForceData data = new SpringForceData(
                event.getDir(),
                event.getPos(),
                event.getSuSecSplashed() / 10000f,
                5
        );

        Vec3 impactBP = event.getPos().getCenter();
        SpringForceAttachment attachment = SpringForceAttachment.get(serverLevel, new Vector3d(impactBP.x, impactBP.y, impactBP.z));

        if(attachment != null){
            LoadedServerShip ship = VSGameUtilsKt.getLoadedShipManagingPos(serverLevel, VectorConversionsMCKt.toJOMLD(event.getPos()));

            ShipTransform transform = ship.getTransform();

            Vector3d posInShip = VectorConversionsMCKt.toJOMLD(event.getPos()).add(event.getDir())
                    .add(0.5, 0.5, 0.5, new Vector3d())
                    .sub(transform.getPositionInShip());

            Vector3d posInShip2 = VectorConversionsMCKt.toJOMLD(event.getPos()).add(event.getDir()).add(event.getDir())
                    .add(0.5, 0.5, 0.5, new Vector3d())
                    .sub(transform.getPositionInShip());

            Vector3d posInWorld = ship.getShipToWorld().transformPosition(posInShip);
            Vector3d posInWorld2 = ship.getShipToWorld().transformPosition(posInShip2);

            BlockPos BPInWorld = new BlockPos((int) posInWorld.x, (int) posInWorld.y, (int) posInWorld.z);
            BlockPos BPInWorld2 = new BlockPos((int) posInWorld2.x, (int) posInWorld2.y, (int) posInWorld2.z);

            if(!serverLevel.getBlockState(BPInWorld).isAir() || !serverLevel.getBlockState(BPInWorld2).isAir()){
                attachment.addSpringThruster(event.getPos(), data);
            }
        }
    }
}
