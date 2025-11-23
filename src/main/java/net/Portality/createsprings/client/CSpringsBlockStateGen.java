package net.Portality.createsprings.client;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.level.block.Block;

public class CSpringsBlockStateGen {
    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> slab(
            String path) {
        return (c, p) -> p.simpleBlock(c.get(), p.models().slab(c.getName(), p.modLoc("block/" + path), p.modLoc("block/" + path), p.modLoc("block/" + path)));
    }
}
