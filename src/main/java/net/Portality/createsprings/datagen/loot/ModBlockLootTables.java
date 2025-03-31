package net.Portality.createsprings.datagen.loot;

import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Consumer;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.ANDESITE_MOLD.get());
        dropSelf(ModBlocks.SPRING.get());
        dropSelf(ModBlocks.SPRING_ALLOY_BLOCK.get());
        dropSelf(ModBlocks.FRICTION_WELDER.get());
        dropSelf(ModBlocks.OBSIDIAN_PLATE.get());
        dropSelf(ModBlocks.OBSIDIAN_SLAB.get());
        dropSelf(ModBlocks.UNFINISHED_SPRING.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
