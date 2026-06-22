package com.Portality.sableCompat;

import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import com.Portality.createsprings.utill.Helpers.RenderHelper;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.callback.ExplosiveBlockCallback;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity.canBreakBySpring;
import static com.Portality.sableCompat.SableCompatSpring.*;

public class SableCompatLargeSpring {
    public static final double accelerationLimit = 150;

    public static class SubLevelLargeSpringAssemblyHelper implements SubLevelAssemblyHelper.FrontierPredicate {
        private LargeSpringBlockEntity be;
        private AABB aabb;
        Set<SeatEntity> seats = new HashSet<>();

        SubLevelLargeSpringAssemblyHelper(LargeSpringBlockEntity be){
            this.be = be;
            AABB aabb = new AABB(be.getBlockPos()).inflate(2);
            this.aabb = aabb.move(new BlockPos(be.getFacing().getOpposite().getNormal().multiply(2)));
            this.aabb = aabb.move(new BlockPos(be.getFacing().getNormal().multiply(be.getCurLen())));
        }

        @Override
        public boolean isValidConnection(BlockPos originPos, BlockState originState, BlockPos pos, BlockState state, @Nullable Direction directionFrom) {
            if(originState.getBlock() instanceof SeatBlock){
                List<SeatEntity> seats = be.getLevel().getEntitiesOfClass(SeatEntity.class, new AABB(originPos));
                this.seats.addAll(seats);
            }

            if(aabb.contains(pos.getCenter())){return false;}
            return true;
        }
    };

    public static void pushSubLevels(LargeSpringBlockEntity lbe){
        if(lbe.getLevel().isClientSide()) return; //entry point

        final Vec3i normal = lbe.getFacing().getNormal();
        float scale = PUSH_SCALE;
        scale = (float) (scale * lbe.getPushStrength());
        SubLevel subLevelOn = Sable.HELPER.getContaining(lbe);

        Vector3d worldImpulse = new Vector3d(
                normal.getX() * scale,
                normal.getY() * scale,
                normal.getZ() * scale
        );

        for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(lbe.getLevel(), getAreaForDetection(lbe))) {
            if (subLevel instanceof ServerSubLevel serverSubLevel) {

                Vector3d localImpulse = new Vector3d(worldImpulse);
                serverSubLevel.logicalPose().orientation().transformInverse(localImpulse);
                Vector3d worldHitPos = new Vector3d(lbe.getBlockPos().relative(lbe.getFacing(), lbe.getCurLen()).getCenter().toVector3f());
                Vector3d localHitPos = serverSubLevel.logicalPose().transformPositionInverse(worldHitPos);

                applyLimitedImpulseToSubLevel(serverSubLevel, localImpulse, stabilisePosition(localHitPos), lbe);
            }
        }

        if (!(subLevelOn instanceof ServerSubLevel serverSubLevel)) return;

        if (!hasSublevelOrBlock( scale, normal, serverSubLevel, lbe)){
            //if (!splitAndShootBlock(serverSubLevel, lbe, lbe.getBlockPos())){return;}
            return;
        }

        Vec3i moveVector = lbe.getFacing().getOpposite().getNormal();
        Vector3d springImpulse = new Vector3d(scale, scale, scale).mul(new Vector3d(moveVector.getX(), moveVector.getY(), moveVector.getZ()));

        applyLimitedImpulseToSubLevel(serverSubLevel, springImpulse, lbe.getBlockPos().getCenter(), lbe);
    }

    private static boolean splitAndShootBlock(ServerSubLevel serverSubLevelOn, LargeSpringBlockEntity be, BlockPos shiftedPos){
        BlockPos pos = shiftedPos.relative(be.getFacing(), be.getCurLen());
        if(!canBreakBySpring(pos, be.getLevel(), (float) be.stored)){return false;}

        if(!(be.getLevel() instanceof ServerLevel serverLevel)){return false;}
        @Nullable ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(serverLevel);
        if(container == null) return false;

        SubLevelLargeSpringAssemblyHelper helper = new SubLevelLargeSpringAssemblyHelper(be);
        SubLevelAssemblyHelper.@NotNull GatherResult result = SubLevelAssemblyHelper.gatherConnectedBlocks(pos, serverLevel, 256_000, helper);
        if(result.blocks() == null){return false;}
        ArrayList<BlockPos> assemblyBlocks = new ArrayList<>(result.blocks());

        BlockState state = serverSubLevelOn.getLevel().getBlockState(pos);

        final BoundingBox3d aabb = getAreaForDetection(be);
        ServerSubLevel addedServerSubLevel = SubLevelAssemblyHelper.assembleBlocks(serverLevel, pos, assemblyBlocks, aabb.expand(8).chunkBoundsFrom());
        RigidBodyHandle handle = RigidBodyHandle.of(addedServerSubLevel);

        if(state.getBlock() instanceof BlockWithSubLevelCollisionCallback collisionCallback){
            if(collisionCallback.sable$getCallback() instanceof ExplosiveBlockCallback){
                Vec3i facing = be.getFacing().getNormal();
                Vector3d facing3d = new Vector3d(facing.getX(), facing.getY(), facing.getZ());
                Vector3d pointDirection = serverSubLevelOn.logicalPose().orientation().transform(facing3d);
                handle.teleport(addedServerSubLevel.logicalPose().position().add(pointDirection.mul(0.1)), serverSubLevelOn.logicalPose().orientation());
            }
        }

        //movePassengersOnSeats(helper, serverSubLevelOn, addedServerSubLevel, be);
        be.disableBreakingBlocks = true;
        be.createdSubLevel = addedServerSubLevel.getUniqueId();
        be.sendData();
        return true;
    }

    private static boolean hasSublevelOrBlock(float scale, Vec3i normal, ServerSubLevel serverSubLevelOn, LargeSpringBlockEntity lbe){
        AABB detectionBox = getAreaForDetection(lbe).toMojang();
        Pose3d pose = serverSubLevelOn.logicalPose();
        boolean ret = false;

        ArrayList<Vec3> pointsToCheck = new ArrayList<>();
        Direction firstPerpendicular = Direction.get(Direction.AxisDirection.POSITIVE, RenderHelper.getPerpendicularAxis(lbe.getFacing()));
        Direction secondPerpendicular = Direction.get(Direction.AxisDirection.POSITIVE, RenderHelper.getPerpendicularAxis(lbe.getFacing()));

        for(int h = 0; h <= lbe.getLen(); h++){
            for(int x = -1; x <= 1; x++){
                for(int z = -1; z <= 1; z++){
                    BlockPos current = lbe.getBlockPos().relative(lbe.getFacing(), h).relative(firstPerpendicular, x).relative(secondPerpendicular, z);

                    for(int i = 0; i < 8; i++){
                        int halfCounter = i / 2;
                        int quarterCounter = i / 4;

                        Vec3 shift = new Vec3(Math.pow(-1, i) * -0.5d, Math.pow(-1, halfCounter) * -0.5d, Math.pow(-1, quarterCounter) * -0.5d);
                        pointsToCheck.add(current.getCenter().add(shift));
                    }
                }
            }
        }

        for(Vec3 point : pointsToCheck){
            BlockPos inWorld = BlockPos.containing(pose.transformPosition(point));
            if(!lbe.getLevel().getBlockState(inWorld).isAir()){
                ret = true;
            }
        }

        for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(lbe.getLevel(),
                new BoundingBox3d(pose.transformPosition(detectionBox.getMinPosition()), pose.transformPosition(detectionBox.getMaxPosition())))) {

            if(subLevel == serverSubLevelOn) continue;
            if (subLevel instanceof ServerSubLevel serverSubLevel) {
                Vector3d impulse = new Vector3d(scale, scale, scale).mul(normal.getX(), normal.getY(), normal.getZ());
                Vec3  affectedPos = transformFromOneShipToAnother(serverSubLevelOn, serverSubLevel, lbe);
                applyLimitedImpulseToSubLevel(serverSubLevel, impulse, affectedPos, lbe);
                ret = true;
            }
        }

        return ret;
    }

    public static void applyLimitedImpulseToSubLevel(ServerSubLevel level, Vector3d impulse, Vec3 affected, BlockEntity be){
        double theoreticalAcceleration = impulse.length() / level.getMassTracker().getMass();
        if (theoreticalAcceleration > accelerationLimit) {
            double reductionFactor = theoreticalAcceleration / accelerationLimit;
            impulse.div(reductionFactor);
        }

        applyImpulseToSubLevel(level, impulse, affected, be);
    }

    public static Vec3 transformFromOneShipToAnother(ServerSubLevel on, ServerSubLevel to, LargeSpringBlockEntity be){
        Vec3 position = be.getBlockPosition().relative(be.getFacing(), be.getLen()).getCenter();
        Vec3 globalPos = on.logicalPose().transformPosition(position);
        Vec3 finalPos = to.logicalPose().transformPositionInverse(globalPos);
        return finalPos;
    }

    public static BoundingBox3d getAreaForDetection(LargeSpringBlockEntity lbe){
        return new BoundingBox3d(lbe.getAreaForDetection());
    }

    public static BlockPos getApplyedForcePos(LargeSpringBlockEntity lbe){
        return lbe.getBlockPos().relative(lbe.getFacing(), lbe.getCurLen());
    }
}
