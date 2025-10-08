package net.Portality.createsprings.blocks.advanced.Spring;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;

public interface ISpringBE {
    void onBlockExploded(BlockPos pos, Explosion explosion);
}
