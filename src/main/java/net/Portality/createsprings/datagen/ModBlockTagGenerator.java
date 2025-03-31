package net.Portality.createsprings.datagen;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateSprings.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SPRING.get(),
                        ModBlocks.SPRING_ALLOY_BLOCK.get(),
                        ModBlocks.FRICTION_WELDER.get(),
                        ModBlocks.ANDESITE_MOLD.get(),
                        ModBlocks.OBSIDIAN_PLATE.get(),
                        ModBlocks.UNFINISHED_SPRING.get(),
                        ModBlocks.OBSIDIAN_SLAB.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.ANDESITE_MOLD.get(),
                        ModBlocks.FRICTION_WELDER.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SPRING.get(),
                        ModBlocks.SPRING_ALLOY_BLOCK.get(),
                        ModBlocks.UNFINISHED_SPRING.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.OBSIDIAN_PLATE.get(),
                        ModBlocks.OBSIDIAN_SLAB.get());
    }
}
