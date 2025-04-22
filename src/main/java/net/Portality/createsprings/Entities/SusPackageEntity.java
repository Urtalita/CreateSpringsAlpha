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

public class SusPackageEntity extends PackageEntity {

    public SusPackageEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public SusPackageEntity(Level worldIn, double x, double y, double z) {
        this(AllEntityTypes.PACKAGE.get(), worldIn);
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
}
