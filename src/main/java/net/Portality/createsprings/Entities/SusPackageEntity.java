package net.Portality.createsprings.Entities;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.logistics.box.PackageEntity;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class SusPackageEntity extends PackageEntity {

    public SusPackageEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public SusPackageEntity(Level worldIn, double x, double y, double z) {
        this(ModEntities.SUS_PACKAGE.get(), worldIn);
        this.setPos(x, y, z);
        this.refreshDimensions();
    }

    public AbstractArrow createProjectile(Level level, LivingEntity shooter) {
        SpringProjectile projectile = new SpringProjectile(level, shooter);
        projectile.setBaseDamage(3.5); // Установка урона
        return projectile;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        SpringProjectile projectile = (SpringProjectile) createProjectile(level(), this);
        projectile.shootFromRotation(this, 0, 270, 0.0F,  3.0F, 1.0F);
        level().addFreshEntity(projectile);
        return super.hurt(source, amount);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<SusPackageEntity> boxBuilder = (EntityType.Builder<SusPackageEntity>) builder;
        return boxBuilder.setCustomClientFactory(SusPackageEntity::spawn)
                .sized(1, 1);
    }
    public static SusPackageEntity spawn(PlayMessages.SpawnEntity spawnEntity, Level world) {
        SusPackageEntity packageEntity =
                new SusPackageEntity(world, spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ());
        packageEntity.setDeltaMovement(spawnEntity.getVelX(), spawnEntity.getVelY(), spawnEntity.getVelZ());
        packageEntity.clientPosition = packageEntity.position();
        return packageEntity;
    }
}
