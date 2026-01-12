package net.Portality.createsprings.Entities.Projectile;

import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UnfinishedSpringProjectile extends AbstractArrow {
    protected UnfinishedSpringProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModBlocks.UNFINISHED_SPRING.get().asItem());
    }
}
