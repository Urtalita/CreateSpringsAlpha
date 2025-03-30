package net.Portality.createsprings.Entities.Projectile;

import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;

public class SpringProjectile extends AbstractArrow {
    private int bounceCount = 0;
    private static final double BOUNCE_FACTOR = 0.9; // Коэффициент отскока
    private static final int MAX_BOUNCES = 10; // Максимальное число отскоков
    private static final float ARC_HEIGHT = 0.05f; // Высота дуги
    private static final double GRAVITY = -0.08;

    public SpringProjectile(Level pLevl) {
        super(ModEntities.SPRING_PROJECTILE.get(),pLevl);
    }

    public SpringProjectile(Level pLevl, LivingEntity livingEntity) {
        super(ModEntities.SPRING_PROJECTILE.get(), livingEntity, pLevl);
    }

    public SpringProjectile(EntityType<SpringProjectile> springProjectileEntityType, Level level) {
        super(springProjectileEntityType, level);
        this.setBaseDamage(10);
    }

    @Override
    protected boolean tryPickup(Player player) {
        if (!player.isCreative()) {
            if (!level().isClientSide) {
                ItemStack spring = new ItemStack(ModBlocks.SPRING.asItem());
                if (!player.getInventory().add(spring)) {
                    player.drop(spring, false);
                }
            }
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isInWall() && !this.onGround()) {
            this.pushOutOfBlock();
            this.correctPosition();
        }

        if(true){
            if(this.getDeltaMovement().length() > 0.1){

                if (this.level().isClientSide) {
                    this.level().addParticle(
                            ParticleTypes.FIREWORK,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            0,
                            0.1,
                            0);
                }
            } else {
                if(this.getDeltaMovement().length() != 0){
                    this.setOnGround(true);
                }
            }
        }

        if (bounceCount < MAX_BOUNCES) {

        }
    }



    private void correctPosition() {
        AABB bb = this.getBoundingBox();
        for (int i = 0; i < 3; i++) {
            BlockPos pos = new BlockPos(
                    (int) Math.floor(bb.minX + i * (bb.maxX - bb.minX) / 2),
                    (int) Math.floor(bb.minY + i * (bb.maxY - bb.minY) / 2),
                    (int) Math.floor(bb.minZ + i * (bb.maxZ - bb.minZ) / 2)
            );

            if (!this.level().isEmptyBlock(pos)) {
                pushOutOfBlock();
                break;
            }
        }
    }

    private void pushOutOfBlock() {
        Direction escapeDir = findEscapeDirection();
        if (escapeDir != null) {
            Vec3 motion = Vec3.atLowerCornerOf(escapeDir.getNormal()).scale(0.3);
            this.setDeltaMovement(this.getDeltaMovement().add(motion.scale(0.5)));
            this.setPos(
                    this.getX() + motion.x,
                    this.getY() + motion.y,
                    this.getZ() + motion.z
            );
        } else {
            this.setPos(this.getX(), this.getY() + 0.5, this.getZ());
        }
    }

    private Direction findEscapeDirection() {
        BlockPos currentPos = this.blockPosition();
        for (Direction dir : Direction.values()) {
            BlockPos checkPos = currentPos.relative(dir);
            if (this.level().isEmptyBlock(checkPos)) {
                return dir;
            }
        }
        return null;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (bounceCount < MAX_BOUNCES) {
            Direction direction = result.getDirection();
            Vec3 normal = new Vec3(
                    direction.getStepX(),
                    direction.getStepY(),
                    direction.getStepZ()
            );

            if(redirectProjectile(this, 10)){
                return;
            }

            Vec3 newMotion = this.getDeltaMovement()
                    .subtract(normal.scale(2 * this.getDeltaMovement().dot(normal)))
                    .scale(BOUNCE_FACTOR);

            if (newMotion.length() > 0.15) {
                Vec3 newPos = result.getLocation().add(normal.scale(0.1));
                this.setPos(newPos.x, newPos.y, newPos.z);
                this.setDeltaMovement(newMotion);
                bounceCount++;
            } else {
                super.onHitBlock(result);
                this.inGround = true;
                this.setDeltaMovement(Vec3.ZERO);
            }

            // Звук и частицы
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.SHIELD_BLOCK,
                    SoundSource.NEUTRAL, 0.5F, 1.2F);

            bounceCount++;

            Level level = this.level();

            if (level.isClientSide) {
                for (int i = 0; i < 8; i++) {
                    level.addParticle(
                            ParticleTypes.ITEM_SLIME,
                            this.getX(),
                            this.getY() + 0.2,
                            this.getZ(),
                            (random.nextDouble() - 0.5) * 0.2,
                            0.1,
                            (random.nextDouble() - 0.5) * 0.2
                    );
                }
            }
        } else {
            super.onHitBlock(result);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.isRemoved() || level().isClientSide) return;
        Entity target = result.getEntity();
        DamageSource damageSource = this.damageSources().arrow(this, this.getOwner());
        if (bounceCount < MAX_BOUNCES) {

            if (target instanceof LivingEntity livingTarget) {
                livingTarget.hurt(damageSource, (float) (this.getBaseDamage() * this.getDeltaMovement().length() * 2));
            }

            Vec3 normal = target.position().subtract(this.position()).normalize();

            Vec3 newPos = this.position()
                    .add(normal.scale(0.1));
            this.setPos(newPos.x, newPos.y, newPos.z);

            if(redirectProjectile(this, 5, target)){
                return;
            }

            Vec3 newMotion = this.getDeltaMovement()
                    .subtract(normal.scale(2 * this.getDeltaMovement().dot(normal)))
                    .scale(BOUNCE_FACTOR);

            this.setDeltaMovement(newMotion);

            bounceCount++;

            if (this.level().isClientSide) {
                for (int i = 0; i < 8; i++) {
                    this.level().addParticle(
                            ParticleTypes.ITEM_SLIME,
                            this.getX(),
                            this.getY() + 0.2,
                            this.getZ(),
                            (random.nextDouble() - 0.5) * 0.2,
                            0.1,
                            (random.nextDouble() - 0.5) * 0.2
                    );
                }
            }

        } else {
            super.onHitEntity(result);
        }
    }

    private boolean redirectProjectile(Projectile projectile, double radius, @Nullable Entity excludedEntity) {
        Vec3 projectilePos = projectile.position();
        LivingEntity owner = (LivingEntity) projectile.getOwner();

        // Сохраняем исходные параметры движения
        Vec3 currentMotion = projectile.getDeltaMovement();
        double originalSpeed = currentMotion.length();
        if(originalSpeed < 0.5) return false;

        AABB searchArea = new AABB(
                projectilePos.x - radius,
                projectilePos.y - radius,
                projectilePos.z - radius,
                projectilePos.x + radius,
                projectilePos.y + radius,
                projectilePos.z + radius
        );

        LivingEntity target = findBestTarget(projectile, searchArea, excludedEntity, owner);
        if(target == null) return false;

        Vec3 predictedPos = calculateInterceptionPoint(projectile, target, originalSpeed);
        Vec3 direction = predictedPos.subtract(projectilePos).normalize();

        double horizontalDistance = Math.sqrt(
                Math.pow(predictedPos.x - projectilePos.x, 2) +
                        Math.pow(predictedPos.z - projectilePos.z, 2)
        );

        double flightTime = Math.max(horizontalDistance / (originalSpeed * 0.9), 0.5);

        double verticalVelocity = (predictedPos.y - projectilePos.y + 0.5 * -GRAVITY * flightTime * flightTime) / flightTime;

        Vec3 newMotion = new Vec3(
                direction.x * originalSpeed,
                verticalVelocity,
                direction.z * originalSpeed
        ).scale(0.95);

        projectile.setDeltaMovement(newMotion);
        return true;
    }

    private Vec3 calculateInterceptionPoint(Projectile projectile, Entity target, double projectileSpeed) {
        Vec3 targetPos = target.position();
        Vec3 targetVel = target.getDeltaMovement();
        Vec3 projectilePos = projectile.position();

        double a = targetVel.x * targetVel.x + targetVel.z * targetVel.z - projectileSpeed * projectileSpeed;
        double b = 2 * (targetVel.x * (targetPos.x - projectilePos.x) +
                targetVel.z * (targetPos.z - projectilePos.z));
        double c = Math.pow(targetPos.x - projectilePos.x, 2) +
                Math.pow(targetPos.z - projectilePos.z, 2);

        double discriminant = b*b - 4*a*c;
        if(discriminant < 0) return targetPos;

        double t = (-b + Math.sqrt(discriminant)) / (2*a);
        t = Math.max(t, 0.1);

        return targetPos.add(
                targetVel.x * t,
                targetVel.y * t + 0.5 * -GRAVITY * t * t,
                targetVel.z * t
        );
    }

    private LivingEntity findBestTarget(Projectile projectile, AABB area, Entity excluded, LivingEntity owner) {
        return projectile.level().getEntitiesOfClass(LivingEntity.class, area)
                .stream()
                .filter(e -> e != excluded && e != owner && e.isAlive())
                .filter(e -> isTargetVisible(projectile, e))
                .min(Comparator.comparingDouble(e ->
                        projectile.distanceToSqr(e) +
                                Math.abs(e.getY() - projectile.getY())
                ))
                .orElse(null);
    }

    private boolean isTargetVisible(Projectile projectile, Entity target) {
        Vec3 start = projectile.position().add(0, 0.5, 0);
        Vec3 end = getEntityCenter(target);

        ClipContext context = new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                projectile
        );

        return projectile.level().clip(context).getType() == HitResult.Type.MISS;
    }

    private boolean redirectProjectile(Projectile projectile, double radius) {
        return redirectProjectile(projectile, radius, null);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModBlocks.SPRING.get().asItem());
    }

    private Vec3 getEntityCenter(Entity entity) {
        AABB aabb = entity.getBoundingBox();
        return new Vec3(
                aabb.minX + (aabb.maxX - aabb.minX) * 0.5,
                aabb.minY + (aabb.maxY - aabb.minY) * 0.5,
                aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5
        );
    }
}
