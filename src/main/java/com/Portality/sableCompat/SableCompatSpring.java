package com.Portality.sableCompat;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlock;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import com.Portality.createsprings.blocks.displaySource.CSpringsDisplaySources;
import com.Portality.createsprings.items.advanced.Spring.SpringItem;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.callback.ExplosiveBlockCallback;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

import static com.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;
import static com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity.canBreakBySpring;
import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class SableCompatSpring {
    public static float PUSH_SCALE = 25f;
    public static double STABILISATION_FACTOR = 0.25;

    public static void registerSpring() {
        CSpringsBlocks.SPRING = CSPRINGS_REGISTRATE.block("spring", SableSpringBlock::new)
                .initialProperties(SharedProperties::copperMetal)
                .transform(pickaxeOnly())
                .properties(p -> p.noOcclusion().isRedstoneConductor((s, l, pos) -> false))
                .transform(displaySource(CSpringsDisplaySources.CHARGE))
                .item(SpringItem::new)
                .build()
                .blockstate(BlockStateGen.directionalBlockProvider(false))
                //.onRegister(movementBehaviour(new SpringMovement()))
                .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                .register();
    }

    public static boolean launchEntitiesInFront(SpringBlockEntity be, Vec3 vector){
        if(Sable.HELPER.getContaining(be) instanceof ServerSubLevel serverSubLevel){
            Direction facing = be.getBlockState().getValue(FACING).getOpposite();
            BlockPos targetPos = be.getBlockPos().relative(facing);

            AABB searchArea = new AABB(targetPos);
            List<Entity> entities = be.getLevel().getEntitiesOfClass(Entity.class, searchArea);

            vector = new Vec3(1, 1, 1).scale(vector.length());
            Vec3 vectorFrom = serverSubLevel.logicalPose().transformPosition(be.getBlockPos().getCenter());
            Vec3 vectorTo = serverSubLevel.logicalPose().transformPosition(be.getFront().getCenter());
            vector = vectorTo.subtract(vectorFrom).multiply(vector);

            for (Entity entity : entities) {
                entity.addDeltaMovement(vector);
                entity.hurtMarked = true;
            }
            return true;
        }
        return false;
    }

    public static class SubLevelSpringAssemblyHelper implements SubLevelAssemblyHelper.FrontierPredicate {
        private SpringBlockEntity be;
        private AABB aabb;
        Set<SeatEntity> seats = new HashSet<>();

        SubLevelSpringAssemblyHelper(SpringBlockEntity be){
            this.be = be;
            AABB aabb = new AABB(be.getBlockPos()).inflate(1);
            this.aabb = aabb.move(new BlockPos(be.getFacing().getOpposite().getNormal()));
        }

        @Override
        public boolean isValidConnection(BlockPos originPos, BlockState originState, BlockPos pos, BlockState state, @Nullable Direction directionFrom) {
            if(originState.getBlock() instanceof SeatBlock){
                List<SeatEntity> seats = be.getLevel().getEntitiesOfClass(SeatEntity.class, new AABB(originPos));
                this.seats.addAll(seats);
            }

            if (be.getLevel().getBlockEntity(pos) instanceof SpringBlockEntity springBlockEntity){
                return !(springBlockEntity.getPhase() > 0);
            }

            if(aabb.contains(pos.getCenter())){return false;}
            return true;
        }
    };

    public static void pushSubLevels(SpringBlockEntity be){
        if(be.getLevel().isClientSide()) return; //entry point

        final Vec3i normal = be.getFacing().getNormal();
        float scale = PUSH_SCALE;
        scale = (float) (scale * be.getVectorSpeedForLaunch().length());
        SubLevel subLevelOn = Sable.HELPER.getContaining(be);

        Vector3d worldImpulse = new Vector3d(
                normal.getX() * scale,
                normal.getY() * scale,
                normal.getZ() * scale
        );

        for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(be.getLevel(), getAreaForDetection(be))) {
            if (subLevel instanceof ServerSubLevel serverSubLevel) {

                Vector3d localImpulse = new Vector3d(worldImpulse);
                serverSubLevel.logicalPose().orientation().transformInverse(localImpulse);
                Vector3d worldHitPos = new Vector3d(be.getFront().getCenter().toVector3f());
                Vector3d localHitPos = serverSubLevel.logicalPose().transformPositionInverse(worldHitPos);

                applyImpulseToSubLevel(serverSubLevel, localImpulse, stabilisePosition(localHitPos), be);
            }
        }

        if (!(subLevelOn instanceof ServerSubLevel serverSubLevel)) return;

        Pose3d pose = subLevelOn.logicalPose();
        Vec3 position = pose.transformPosition(be.getFront().getCenter());

        if (!hasSublevelOrBlock(BlockPos.containing(position), scale, normal, serverSubLevel, be)){
            if (!splitAndShootBlock(serverSubLevel, be)){return;}
        }

        Vec3i moveVector = be.getFacing().getOpposite().getNormal();
        Vector3d springImpulse = new Vector3d(scale, scale, scale).mul(new Vector3d(moveVector.getX(), moveVector.getY(), moveVector.getZ()));

        applyImpulseToSubLevel(serverSubLevel, springImpulse, be.getBlockPos().getCenter(), be);
    }

    public static Vec3 stabilisePosition(Vec3 localHitPos){
        double stabilisationFactorTransformed = 1 / STABILISATION_FACTOR;

        Vec3 stabilizedLocalHitPos = new Vec3(
                Math.round(localHitPos.x * stabilisationFactorTransformed) / stabilisationFactorTransformed,
                Math.round(localHitPos.y * stabilisationFactorTransformed) / stabilisationFactorTransformed,
                Math.round(localHitPos.z * stabilisationFactorTransformed) / stabilisationFactorTransformed
        );

        return stabilizedLocalHitPos;
    }

    public static Vec3 stabilisePosition(Vector3d localHitPos){
        double stabilisationFactorTransformed = 1 / STABILISATION_FACTOR;

        Vec3 stabilizedLocalHitPos = new Vec3(
                Math.round(localHitPos.x * stabilisationFactorTransformed) / stabilisationFactorTransformed,
                Math.round(localHitPos.y * stabilisationFactorTransformed) / stabilisationFactorTransformed,
                Math.round(localHitPos.z * stabilisationFactorTransformed) / stabilisationFactorTransformed
        );

        return stabilizedLocalHitPos;
    }

    private static boolean splitAndShootBlock(ServerSubLevel serverSubLevelOn, SpringBlockEntity be){
        BlockPos pos = be.getFront();
        if(!canBreakBySpring(pos, be.getLevel(), (float) be.stored)){return false;}

        if(!(be.getLevel() instanceof ServerLevel serverLevel)){return false;}
        @Nullable ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(serverLevel);
        if(container == null) return false;

        SubLevelSpringAssemblyHelper helper = new SubLevelSpringAssemblyHelper(be);
        SubLevelAssemblyHelper.@NotNull GatherResult result = SubLevelAssemblyHelper.gatherConnectedBlocks(pos, serverLevel, 256_000, helper);
        if(result.blocks() == null){return false;}
        ArrayList<BlockPos> assemblyBlocks = new ArrayList<>(result.blocks());

        BlockState state = serverSubLevelOn.getLevel().getBlockState(be.getFront());

        final BoundingBox3d aabb = getAreaForDetection(be);
        ServerSubLevel addedServerSubLevel = SubLevelAssemblyHelper.assembleBlocks(serverLevel, be.getFront(), assemblyBlocks, aabb.expand(8).chunkBoundsFrom());
        RigidBodyHandle handle = RigidBodyHandle.of(addedServerSubLevel);

        if(state.getBlock() instanceof BlockWithSubLevelCollisionCallback collisionCallback){
            if(collisionCallback.sable$getCallback() instanceof ExplosiveBlockCallback){
                Vec3i facing = be.getFacing().getNormal();
                Vector3d facing3d = new Vector3d(facing.getX(), facing.getY(), facing.getZ());
                Vector3d pointDirection = serverSubLevelOn.logicalPose().orientation().transform(facing3d);
                handle.teleport(addedServerSubLevel.logicalPose().position().add(pointDirection.mul(0.1)), serverSubLevelOn.logicalPose().orientation());
            }
        }
        
        movePassengersOnSeats(helper, serverSubLevelOn, addedServerSubLevel, be);
        be.disableBreakingBlocks = true;
        be.createdSubLevel = addedServerSubLevel.getUniqueId();
        be.sendData();
        return true;
    }

    public static void pushCreatedSubLevels(SpringBlockEntity be){ //happends after 1 tick
        if(be.createdSubLevel == null){return;}

        final Vec3i normal = be.getFacing().getNormal();
        double scale = PUSH_SCALE;
        scale = scale * be.getVectorSpeedForLaunch().length();

        if(!(be.getLevel() instanceof ServerLevel serverLevel)){
            be.createdSubLevel = null;
            return;
        }
        @Nullable ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(serverLevel);
        if(container == null) {
            be.createdSubLevel = null;
            return;
        }

        @Nullable SubLevel addedServerSubLevel = container.getSubLevel(be.createdSubLevel);
        if((addedServerSubLevel instanceof ServerSubLevel serverSubLevel)){
            Vector3d impulse = new Vector3d(scale, scale, scale).mul(normal.getX(), normal.getY(), normal.getZ());
            SubLevel subLevelOn = Sable.HELPER.getContaining(be);

            if (subLevelOn != null) {
                if(subLevelOn instanceof ServerSubLevel){
                    applyLinerImpulseToSubLevel(serverSubLevel, impulse, be);
                }
            }
        }
        be.createdSubLevel = null;
    }

    public static void movePassengersOnSeats(SubLevelSpringAssemblyHelper helper, ServerSubLevel subLevelOn, ServerSubLevel SubLevelOther, SpringBlockEntity be){
        Set<SeatEntity> seats = helper.seats;
        if(seats.isEmpty()){return;}

        for(SeatEntity seat : seats){
            BlockPos newPosition = BlockPos.containing(transformFromOneShipToAnotherBlockPos(subLevelOn, SubLevelOther, seat.position()));
            if(be.getLevel().getBlockState(newPosition).getBlock() instanceof SeatBlock){
                if(seat.getFirstPassenger() == null) continue;
                SeatBlock.sitDown(be.getLevel(), newPosition, seat.getFirstPassenger());
            }
        }
    }

    private static boolean hasSublevelOrBlock(BlockPos pos, float scale, Vec3i normal, ServerSubLevel serverSubLevelOn, SpringBlockEntity be){
        AABB detectionBox = getAreaForDetection(be).toMojang();
        Pose3d pose = serverSubLevelOn.logicalPose();
        boolean ret = false;

        ArrayList<Vec3> pointsToCheck = new ArrayList<>();
        pointsToCheck.add(detectionBox.getMinPosition());
        pointsToCheck.add(detectionBox.getMinPosition().add(detectionBox.getXsize(), 0, 0));
        pointsToCheck.add(detectionBox.getMinPosition().add(0, detectionBox.getYsize(), 0));
        pointsToCheck.add(detectionBox.getMinPosition().add(0, 0, detectionBox.getXsize()));

        pointsToCheck.add(detectionBox.getMaxPosition());
        pointsToCheck.add(detectionBox.getMaxPosition().add(detectionBox.getXsize() * -1, 0, 0));
        pointsToCheck.add(detectionBox.getMaxPosition().add(0, detectionBox.getYsize() * -1, 0));
        pointsToCheck.add(detectionBox.getMaxPosition().add(0, 0, detectionBox.getXsize() * -1));

        for(Vec3 point : pointsToCheck){
            BlockPos inWorld = BlockPos.containing(pose.transformPosition(point));
            if(!be.getLevel().getBlockState(inWorld).isAir()){
                ret = true;
            }
        }

        for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(be.getLevel(),
                new BoundingBox3d(pose.transformPosition(detectionBox.getMinPosition()), pose.transformPosition(detectionBox.getMaxPosition())))) {

            if(subLevel == serverSubLevelOn) continue;
            if (subLevel instanceof ServerSubLevel serverSubLevel) {
                Vector3d impulse = new Vector3d(scale, scale, scale).mul(normal.getX(), normal.getY(), normal.getZ());
                Vec3  affectedPos = transformFromOneShipToAnother(serverSubLevelOn, serverSubLevel, be);
                applyImpulseToSubLevel(serverSubLevel, impulse, affectedPos, be);
                ret = true;
            }
        }

        return ret;
    }

    public static void applyImpulseToSubLevel(ServerSubLevel level, Vector3d impulse, Vec3 affected, BlockEntity be) {
        RigidBodyHandle handle = RigidBodyHandle.of(level);
        handle.applyImpulseAtPoint(JOMLConversion.toJOML(affected), impulse);
        //handle.applyLinearImpulse(impulse);
        level.applyQueuedForces(SubLevelPhysicsSystem.get(be.getLevel()), handle, 1);
    }

    public static void applyImpulseToSubLevel(ServerSubLevel level, Vector3d impulse, Vector3d affected, BlockEntity be) {
        RigidBodyHandle handle = RigidBodyHandle.of(level);
        handle.applyImpulseAtPoint(affected, impulse);
        //handle.applyLinearImpulse(impulse);
        level.applyQueuedForces(SubLevelPhysicsSystem.get(be.getLevel()), handle, 1);
    }

    public static void applyLinerImpulseToSubLevel(ServerSubLevel level, Vector3d impulse, BlockEntity be) {
        RigidBodyHandle handle = RigidBodyHandle.of(level);
        handle.applyLinearImpulse(impulse);
        level.applyQueuedForces(SubLevelPhysicsSystem.get(be.getLevel()), handle, 1);
    }

    private static Vec3 transformToTargetLocal(SubLevel from, SubLevel to, Vec3 posLocal, SpringBlockEntity be) {
        Vec3 worldPos = (from != null) ? from.logicalPose().transformPosition(posLocal) : posLocal;
        return to.logicalPose().transformPositionInverse(worldPos);
    }

    public static BoundingBox3d getAreaForDetection(SpringBlockEntity be){
        Direction facing = be.getFacing();
        BlockPos front = be.getFront();

        float thickness = 0.5f;
        Vec3 shiftVector = new Vec3(thickness, thickness, thickness);
        final AABB area = new AABB(front.getCenter().add(shiftVector.scale(-1)), front.relative(facing, 2).getCenter().add(shiftVector));
        final BoundingBox3d aabb = new BoundingBox3d(area);
        return aabb;
    }

    public static Vec3 transformFromOneShipToAnother(ServerSubLevel on, ServerSubLevel to, SpringBlockEntity be){
        Vec3 position = be.getFront().getCenter();
        Vec3 globalPos = on.logicalPose().transformPosition(position);
        Vec3 finalPos = to.logicalPose().transformPositionInverse(globalPos);
        return finalPos;
    }

    private static Vec3 transformFromOneShipToAnotherBlockPos(ServerSubLevel on, ServerSubLevel to, Vec3 pos){
        Vec3 globalPos = on.logicalPose().transformPosition(pos);
        Vec3 finalPos = to.logicalPose().transformPositionInverse(globalPos);
        return finalPos;
    }
}
