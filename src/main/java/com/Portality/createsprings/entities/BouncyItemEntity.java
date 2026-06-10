package com.Portality.createsprings.entities;

import com.Portality.createsprings.entities.Packages.HatPackageEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public class BouncyItemEntity extends ItemEntity {
    public BouncyItemEntity(EntityType<? extends ItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BouncyItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<BouncyItemEntity> boxBuilder = (EntityType.Builder<BouncyItemEntity>) builder;
        return boxBuilder.sized(0.25F, 0.25F).setShouldReceiveVelocityUpdates(true);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void move(MoverType moverType, Vec3 movementVector) {
        Vec3 Velocity = this.getDeltaMovement();
        super.move(moverType, movementVector);
        Vec3 postVelocity = this.getDeltaMovement();

        double bounceX = postVelocity.x;
        double bounceY = postVelocity.y;
        double bounceZ = postVelocity.z;

        double elasticity = 0.95D;
        double minimumVelocity = 0.1D;

        if (this.horizontalCollision && Math.abs(Velocity.x) > minimumVelocity && postVelocity.x == 0) {
            bounceX = -Velocity.x * elasticity;
        }

        if (Math.abs(Velocity.y) > minimumVelocity && postVelocity.y == 0) {
            bounceY = -Velocity.y * elasticity;
        }

        if (this.horizontalCollision && Math.abs(Velocity.z) > minimumVelocity && postVelocity.z == 0) {
            bounceZ = -Velocity.z * elasticity;
        }

        if (bounceX != postVelocity.x || bounceY != postVelocity.y || bounceZ != postVelocity.z) {
            this.setDeltaMovement(bounceX, bounceY, bounceZ);
        }
    }

}
