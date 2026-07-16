package com.Portality.createsprings.datagen.recipes;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.items.CSpringsItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.DeployingRecipeGen;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.compat.jei.category.animations.AnimatedSpout;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.CompletableFuture;

public class CSpringsAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
    GeneratedRecipe

    PUNCHCARD = create("precision_mechanism", b -> b.require(AllItems.CARDBOARD)
            .addOutput(CSpringsItems.PUNCHCARD, 120)
            .addOutput(AllItems.CARDBOARD, 8)
            .addOutput(Items.MAP, 8)
            .addOutput(Items.PAPER, 5)
            .addOutput(Items.PAINTING, 3)
            .addOutput(AllItems.SAND_PAPER, 3)
            .addOutput(AllItems.ATTRIBUTE_FILTER, 2)
            .addOutput(AllItems.SCHEMATIC, 1)
            .loops(1)
            .transitionTo(CSpringsItems.INCOMPLETE_PUNCHCARD)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.PAPER))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(CSpringsItems.SPRING_ALLOY_NUGGET))
            .addStep(PressingRecipe::new, rb -> rb)
    ),


    PSE = create("pse_repair", b -> b.require(CSpringsItems.BROKEN_PSE)
            .addOutput(CSpringsItems.PORTATIVE_STEAM_ENGINE, 120)
            .addOutput(AllItems.COPPER_SHEET, 15)
            .loops(1)
            .transitionTo(CSpringsItems.BROKEN_PSE)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllBlocks.FLUID_TANK))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.CRAFTER_SLOT_COVER))
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.ANDESITE_ALLOY))
            .addStep(PressingRecipe::new, rb -> rb)
            ),


    OBSIDIAN_PLATE = create("obsidian_plate_craft", b -> b.require(AllItems.STURDY_SHEET)
            .addOutput(new ItemStack(CSpringsBlocks.OBSIDIAN_PLATE, 4), 1)
            .loops(1)
            .transitionTo(CSpringsItems.UNFINISHED_OBSIDIAN_PLATE)
            .addStep(PressingRecipe::new, rb -> rb)
            .addStep(PressingRecipe::new, rb -> rb)
            .addStep(CuttingRecipe::new, rb -> rb)
            )
    ;


    public CSpringsAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateSprings.MODID);
    }
}
