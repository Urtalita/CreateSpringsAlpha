package com.Portality.createsprings.entities.Projectile;
import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.entities.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpringAlloyBlockProjectile extends AbstractArrow {
    private float explosionPower = 5.0f;
    private boolean causesFire = false;

    public SpringAlloyBlockProjectile(Level pLevl) {
        super(ModEntities.SPRING_ALLOY_BLOCK_PROJECTILE.get(),pLevl);
    }

    public SpringAlloyBlockProjectile(Level pLevl, LivingEntity livingEntity) {
        super(ModEntities.SPRING_ALLOY_BLOCK_PROJECTILE.get(), pLevl);
    }

    public SpringAlloyBlockProjectile(EntityType<SpringAlloyBlockProjectile> springProjectileEntityType, Level level) {
        super(springProjectileEntityType, level);
        this.setBaseDamage(20);
    }

    @Override
    protected boolean tryPickup(Player player) {
        if (!player.isCreative()) {
            if (!level().isClientSide) {
                ItemStack spring = new ItemStack(ModBlocks.SPRING_ALLOY_BLOCK.get());
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
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (!this.level().isClientSide) {
            // Создаем взрыв
            Vec3 hitPos = hitResult.getLocation();
            if(this.getDeltaMovement().length() > 0.25){
                this.level().explode(
                        this,
                        hitPos.x,
                        hitPos.y,
                        hitPos.z,
                        explosionPower,
                        causesFire,
                        Level.ExplosionInteraction.MOB
                );
                this.discard();
            }
        }
        super.onHitBlock(hitResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (!this.level().isClientSide) {
            if(this.getDeltaMovement().length() > 0.25){
                Vec3 hitPos = hitResult.getLocation();
                this.level().explode(
                        this,
                        hitPos.x,
                        hitPos.y,
                        hitPos.z,
                        explosionPower,
                        causesFire,
                        Level.ExplosionInteraction.MOB
                );
                this.discard();
            }
        }
        super.onHitEntity(hitResult);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModBlocks.SPRING_ALLOY_BLOCK.get().asItem());
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ModBlocks.SPRING_ALLOY_BLOCK.asStack();
    }
}
