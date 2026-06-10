package com.Portality.createsprings.items;

import com.Portality.createsprings.entities.BouncyItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BouncyBlockItem extends BlockItem {

    public BouncyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(net.minecraft.world.level.Level level, Entity location, ItemStack stack) {
        if (location instanceof BouncyItemEntity) {
            return null;
        }

        BouncyItemEntity customEntity = new BouncyItemEntity(
                level,
                location.getX(),
                location.getY(),
                location.getZ(),
                stack
        );

        customEntity.setDeltaMovement(location.getDeltaMovement());

        customEntity.setYRot(location.getYRot());
        customEntity.setXRot(location.getXRot());
        customEntity.setPickUpDelay(40);

        customEntity.hasImpulse = true;
        return customEntity;
    }
}
