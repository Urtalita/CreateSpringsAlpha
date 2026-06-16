package com.Portality.createsprings.datagen.recipes;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.recipe.Casting.CastingRecipe;
import com.Portality.createsprings.recipe.Casting.CastingRecipeParams;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import com.Portality.createsprings.recipe.Welding.WelderRecipeParams;
import com.Portality.createsprings.recipe.Welding.WelderRecipeSpeed;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class CastingRecipesGen extends ProcessingRecipeGen<CastingRecipeParams, CastingRecipe, CastingRecipe.Builder<CastingRecipe>> {
    public CastingRecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateSprings.MODID);
    }

    GeneratedRecipe EMPTY_RECIPE = create("spring_alloy_coil", b -> b
                    .require(CSpringsItems.SPRING_ALLOY)
                    .output(CSpringsBlocks.LARGE_SPRING_COIL)
    );

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CastingRecipe.TYPE_INFO;
    }

    @Override
    protected CastingRecipe.Builder<CastingRecipe> getBuilder(ResourceLocation id) {
        return new CastingRecipe.Builder<>(CastingRecipe::new, id);
    }
}
