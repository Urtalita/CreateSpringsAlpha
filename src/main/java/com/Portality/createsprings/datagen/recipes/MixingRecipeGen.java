package com.Portality.createsprings.datagen.recipes;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.server.fluid.CSpringsFluids;
import com.Portality.createsprings.items.CSpringsItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.CompletableFuture;

import static com.Portality.createsprings.items.CSpringsItems.*;
import static com.Portality.createsprings.blocks.CSpringsBlocks.*;


public class MixingRecipeGen extends com.simibubi.create.api.data.recipe.MixingRecipeGen {
    public MixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateSprings.MODID);
    }

    GeneratedRecipe
    SPRING_ALLOY = create("spring_alloy", (b) -> b
            .require(AllItems.BRASS_INGOT)
            .require(Items.SLIME_BALL)
            .require(Items.GOLD_NUGGET)
            .output(CSpringsItems.SPRING_ALLOY)
    ),

    SPRING_ALLOY_BLOCK_FLUID = create("spring_alloy_block_fluid", (b) -> b
            .require(SPRING_ALLOY_BLOCK)
            .requiresHeat(HeatCondition.HEATED)
            .output(new FluidStack(CSpringsFluids.SPRING_ALLOY.get().getSource(), 1000))
    ),

    SPRING_ALLOY_COIL_FLUID = create("spring_alloy_coil_fluid", (b) -> b
            .require(LARGE_SPRING_COIL)
            .requiresHeat(HeatCondition.HEATED)
            .output(new FluidStack(CSpringsFluids.SPRING_ALLOY.get().getSource(), 500))
            ),

    SPRING_ALLOY_FLUID = create("spring_alloy_fluid", (b) -> b
            .require(CSpringsItems.SPRING_ALLOY)
            .requiresHeat(HeatCondition.HEATED)
            .output(new FluidStack(CSpringsFluids.SPRING_ALLOY.get().getSource(), 100))
            ),

    SPRING_ALLOY_NUGGET_FLUID = create("spring_alloy_nugget_fluid", (b) -> b
            .require(CSpringsItems.SPRING_ALLOY_NUGGET)
            .requiresHeat(HeatCondition.NONE)
            .output(new FluidStack(CSpringsFluids.SPRING_ALLOY.get().getSource(), 10))
            ),

    SPRING_ALLOY_SHEET_FLUID = create("spring_sheet_fluid", (b) -> b
            .require(SPRING_ALLOY_SHEET)
            .requiresHeat(HeatCondition.HEATED)
            .output(new FluidStack(CSpringsFluids.SPRING_ALLOY.get().getSource(), 100))
    )
    ;

}
