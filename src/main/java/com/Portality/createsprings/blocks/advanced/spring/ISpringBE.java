package com.Portality.createsprings.blocks.advanced.spring;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;

public interface ISpringBE {
    void onBlockExploded(BlockPos pos, Explosion explosion);
}
