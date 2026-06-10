package com.Portality.createsprings.compat;

import com.Portality.createsprings.blocks.advanced.spring.SpringBlock;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public class SplashCallback implements BlockSubLevelCollisionCallback {
    public static final SplashCallback INSTANCE = new SplashCallback();

    public SplashCallback() {}

    public boolean shouldTriggerFor(final BlockState state) {
        return state.getBlock() instanceof SpringBlock;
    }

    public double getTriggerVelocity() {
        return 5.0;
    }

    public void onHit(final ServerLevel level, final BlockPos pos, final BlockState state, final Vector3d hitPos) {

    }

    @Override
    public CollisionResult sable$onCollision(BlockPos pos, Vector3d hitPos, double impactVelocity) {
        final double triggerVelocity = this.getTriggerVelocity();

        if (impactVelocity * impactVelocity < triggerVelocity * triggerVelocity) {
            return CollisionResult.NONE;
        }

        final SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        final ServerLevel level = system.getLevel();
        final BlockState state = level.getBlockState(pos);

        BlockEntity be = level.getBlockEntity(pos);

        if (this.shouldTriggerFor(state)) {

            if(be instanceof SpringBlockEntity springBlockEntity){
                if((springBlockEntity).splashMode) {
                    springBlockEntity.setGenerating(true);
                    springBlockEntity.setChanged();
                }

            }
        }

        return new BlockSubLevelCollisionCallback.CollisionResult(new Vector3d(0, 0 ,0), false);
    }
}
