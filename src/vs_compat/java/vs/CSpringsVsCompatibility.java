package vs;

import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.server.SpringSplashEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CSpringsVsCompatibility {
    @SubscribeEvent
    public static void onSpringSplash(SpringSplashEvent event) {
        if(!(ModConfigs.common().VS_COMPATIBILITY.get())){return;}
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        SpringForceData data = new SpringForceData(
                event.getDir(),
                event.getPos(),
                event.getSuSecSplashed() / 10000f,
                5
        );

        Vec3 impactBP = event.getPos().getCenter();
        SpringForceAttachment attachment = SpringForceAttachment.get(serverLevel, new org.joml.Vector3d(impactBP.x, impactBP.y, impactBP.z));

        Direction direction = event.getDirection();
        direction = direction.getOpposite();

        if(attachment != null){
            if(!event.isLargeSpring()){
                Vec3 posInShip = event.getPos().relative(direction).getCenter();
                Vec3 posInShip2 = event.getPos().relative(direction, 2).getCenter();

                Vec3 posInWorld = org.valkyrienskies.mod.common.VSGameUtilsKt.toWorldCoordinates(serverLevel, posInShip);
                Vec3 posInWorld2 = org.valkyrienskies.mod.common.VSGameUtilsKt.toWorldCoordinates(serverLevel, posInShip2);

                BlockPos BPInWorld = new BlockPos((int) posInWorld.x, (int) posInWorld.y, (int) posInWorld.z);
                BlockPos BPInWorld2 = new BlockPos((int) posInWorld2.x, (int) posInWorld2.y, (int) posInWorld2.z);

                if(!serverLevel.getBlockState(BPInWorld).isAir() || !serverLevel.getBlockState(BPInWorld2).isAir()){
                    attachment.addSpringThruster(event.getPos(), data);
                }
            } else {
                //large spring

                for (int x = -1; x <= 1; x++){
                    for (int y = -1; y <= 1; y++){
                        for (int z = -1; z <= 1; z++){
                            Vec3 startPosInShip = event.getPos().offset(x, y, z).getCenter();
                            Vec3 posInWorld = org.valkyrienskies.mod.common.VSGameUtilsKt.toWorldCoordinates(serverLevel, startPosInShip);
                            BlockPos BPInWorld = new BlockPos((int) posInWorld.x, (int) posInWorld.y, (int) posInWorld.z);

                            if(!serverLevel.getBlockState(BPInWorld).isAir()){
                                data = new SpringForceData(
                                        event.getDir(),
                                        event.getPos(),
                                        event.getSuSecSplashed() / 10000f,
                                        2
                                );
                                attachment.addSpringThruster(event.getPos(), data);
                            }
                        }
                    }
                }
            }
        }



        AABB aabb = new AABB(event.getPos().relative(event.getDirection(), 2).east().north(), event.getPos().relative(event.getDirection().getOpposite(), 2).south().west());
        Iterable<org.valkyrienskies.core.api.ships.Ship> ships =
                org.valkyrienskies.mod.common.ValkyrienSkiesMod.getApi().getShipsIntersecting(serverLevel, org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML(aabb));

        org.valkyrienskies.core.api.world.ServerShipWorld world = org.valkyrienskies.mod.common.ValkyrienSkiesMod.getApi().getServerShipWorld(serverLevel.getServer());

        if(world == null) return;

        org.valkyrienskies.core.api.ships.QueryableShipData<org.valkyrienskies.core.api.ships.LoadedServerShip> serverShips = world.getLoadedShips();

        for(org.valkyrienskies.core.api.ships.Ship ship : ships){
            long id = ship.getId();

            if(!serverShips.contains(id)) return;
            org.valkyrienskies.core.api.ships.LoadedServerShip loadedServerShip = serverShips.getById(id);
            if(loadedServerShip == null) return;
            SpringForceAttachment springForceAttachment = SpringForceAttachment.get(loadedServerShip);

            Vec3i invertedDir = event.getDirection().getOpposite().getNormal();
            data = new SpringForceData(
                    new org.joml.Vector3d(invertedDir.getX(), invertedDir.getY(), invertedDir.getZ()),
                    BlockPos.ZERO,
                    event.getSuSecSplashed() / 10000f,
                    5
            );

            springForceAttachment.addSpringThruster(BlockPos.ZERO, data);
        }
    }
}
