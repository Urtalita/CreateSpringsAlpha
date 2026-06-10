package com.Portality.createsprings.blocks.simpleCustomBlocks;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BouncyCasing extends CasingBlock {
    public static final MapCodec<BouncyCasing> CODEC = simpleCodec(BouncyCasing::new);
    protected MapCodec<? extends BouncyCasing> codec() {
        return CODEC;
    }

    public BouncyCasing(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.1F, level.damageSources().fall());
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        super.updateEntityAfterFallOn(level, entity);
    }
}
