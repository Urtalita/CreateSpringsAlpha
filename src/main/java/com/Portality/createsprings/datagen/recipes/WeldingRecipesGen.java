package com.Portality.createsprings.datagen.recipes;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.items.CSpringsItems;
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

public class WeldingRecipesGen extends ProcessingRecipeGen<WelderRecipeParams, WelderRecipe, WelderRecipe.Builder<WelderRecipe>> {
    public WeldingRecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateSprings.MODID);
    }

    GeneratedRecipe ANCIENT_DEBRIS = create("ancient_debris_welding", b -> b
                    .require(Items.ANCIENT_DEBRIS)
                    .require(Items.ANCIENT_DEBRIS)
                    .output(Items.NETHERITE_SCRAP, 2)
                    .speed(WelderRecipeSpeed.SLOW)
    );

    GeneratedRecipe CHARCOAL = create("charcoal", b -> b
            .require(ItemTags.LOGS_THAT_BURN)
            .require(ItemTags.LOGS_THAT_BURN)
            .output(Items.CHARCOAL, 3)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe FRAMED = create("framed_glass", b -> b
            .require(Blocks.GLASS)
            .require(Blocks.GLASS)
            .output(AllPaletteBlocks.FRAMED_GLASS, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe IRON = create("melting_iron", b -> b
            .require(Blocks.RAW_IRON_BLOCK)
            .require(Blocks.RAW_IRON_BLOCK)
            .output(Blocks.IRON_BLOCK, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe GOLD = create("melting_gold", b -> b
            .require(Blocks.RAW_GOLD_BLOCK)
            .require(Blocks.RAW_GOLD_BLOCK)
            .output(Blocks.GOLD_BLOCK, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe SSTONE = create("smooth_stone", b -> b
            .require(Blocks.COBBLESTONE)
            .require(Blocks.COBBLESTONE)
            .output(Blocks.SMOOTH_STONE, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe ALLOY = create("andesite_alloy", b -> b
            .require(AllPaletteStoneTypes.CRIMSITE.materialTag)
            .require(AllPaletteStoneTypes.ANDESITE.materialTag)
            .output(AllItems.ANDESITE_ALLOY, 1)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe ALLOY2 = create("andesite_alloy2", b -> b
            .require(AllPaletteStoneTypes.DIORITE.materialTag)
            .require(AllPaletteStoneTypes.CRIMSITE.materialTag)
            .output(AllItems.ANDESITE_ALLOY, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe ALLOY3 = create("andesite_alloy3", b -> b
            .require(AllPaletteStoneTypes.CRIMSITE.materialTag)
            .require(AllPaletteStoneTypes.GRANITE.materialTag)
            .output(AllItems.ANDESITE_ALLOY, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe ALLOY4 = create("andesite_alloy4", b -> b
            .require(Blocks.IRON_BARS)
            .require(AllPaletteStoneTypes.GRANITE.materialTag)
            .output(AllItems.ANDESITE_ALLOY, 4)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe BELT = create("belt", b -> b
            .doubleIngredient(Blocks.DRIED_KELP_BLOCK)
            .output(AllBlocks.BELT, 4)
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe BRASS = create("brass", b -> b
            .require(AllPaletteStoneTypes.ASURINE.materialTag)
            .require(AllPaletteStoneTypes.VERIDIUM.materialTag)
            .output(AllItems.BRASS_NUGGET, 5)
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe BRASS_BLOCK = create("brass_block", b -> b
            .require(Blocks.COPPER_BLOCK)
            .require(CommonMetal.ZINC.storageBlocks.items())
            .output(AllBlocks.BRASS_BLOCK, 2)
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe BONE_MEAl = create("bone_meal", b -> b
            .require(AllPaletteStoneTypes.DIORITE.materialTag)
            .require(AllPaletteStoneTypes.DIORITE.materialTag)
            .output(Items.BONE_MEAL, 1)
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe ANDESITE_MOLD = create("andesite_mold", b -> b
            .require(AllBlocks.SHAFT)
            .require(AllBlocks.ANDESITE_CASING)
            .output(CSpringsBlocks.ANDESITE_MOLD, 1)
            .speed(WelderRecipeSpeed.SLOW)
    );

    GeneratedRecipe COPPER = create("copper", b -> b
            .require(AllPaletteStoneTypes.GRANITE.materialTag)
            .require(Blocks.SMOOTH_STONE)
            .output(AllPaletteStoneTypes.VERIDIUM.baseBlock.get(), 1)
            .speed(WelderRecipeSpeed.SLOW)
    );

    GeneratedRecipe POLISHED_QUARTZ = create("polished_quartz", b -> b
            .doubleIngredient(AllBlocks.ROSE_QUARTZ_BLOCK)
            .output(ResourceLocation.fromNamespaceAndPath(Create.ID,"polished_rose_quartz"))
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe SPRING_ALLOY = create("spring_alloy", b -> b
            .require(CommonMetal.BRASS.storageBlocks.items())
            .require(Blocks.SLIME_BLOCK)
            .output(CSpringsItems.SPRING_ALLOY, 8)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe SPRING_ALLOY2 = create("spring_alloy2", b -> b
            .require(CommonMetal.BRASS.storageBlocks.items())
            .require(Blocks.HONEY_BLOCK)
            .output(CSpringsItems.SPRING_ALLOY, 4)
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe BASIN = create("basin", b -> b
            .doubleIngredient(AllBlocks.BASIN)
            .output(AllBlocks.ANDESITE_ALLOY_BLOCK.get())
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe BURNER = create("burner", b -> b
            .doubleIngredient(AllItems.EMPTY_BLAZE_BURNER)
            .output(Items.IRON_INGOT, 6)
            .speed(WelderRecipeSpeed.NORMAL)
    );

    GeneratedRecipe CAULDRON = create("cauldron", b -> b
            .doubleIngredient(Blocks.CAULDRON)
            .output(Blocks.IRON_BLOCK)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe CHUTE = create("chute", b -> b
            .doubleIngredient(AllBlocks.CHUTE)
            .output(Items.IRON_NUGGET, 24)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe FLUID_TANK = create("fluid_tank", b -> b
            .doubleIngredient(AllBlocks.FLUID_TANK)
            .output(AllItems.COPPER_NUGGET, 27)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe INDUSTRIAL_IRON = create("industrial_iron", b -> b
            .doubleIngredient(AllBlocks.INDUSTRIAL_IRON_BLOCK)
            .output(Items.IRON_INGOT)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe INDUSTRIAL_IRON2 = create("industrial_iron2", b -> b
            .doubleIngredient(AllBlocks.WEATHERED_IRON_BLOCK)
            .output(Items.IRON_INGOT)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe INDUSTRIAL_IRON3 = create("industrial_iron3", b -> b
            .require(AllBlocks.WEATHERED_IRON_BLOCK)
            .require(AllBlocks.INDUSTRIAL_IRON_BLOCK)
            .output(Items.IRON_INGOT)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe INDUSTRIAL_IRON4 = create("industrial_iron4", b -> b
            .doubleIngredient(CSpringsBlocks.WEATHERED_IRON)
            .output(Items.IRON_INGOT)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe ITEM_VAULT = create("item_vault", b -> b
            .doubleIngredient(AllBlocks.ITEM_VAULT)
            .output(Items.IRON_NUGGET, 27)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe LAMP = create("lamp", b -> b
            .doubleIngredient(AllBlocks.ROSE_QUARTZ_LAMP)
            .output(AllBlocks.ROSE_QUARTZ_BLOCK.get(), 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe LIGHTNING_ROD = create("lightning_rod", b -> b
            .doubleIngredient(Blocks.LIGHTNING_ROD)
            .output(AllItems.COPPER_NUGGET, 21)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe OBSIDIAN_PLATE = create("obsidian_plate", b -> b
            .doubleIngredient(CSpringsBlocks.OBSIDIAN_SLAB)
            .output(CSpringsBlocks.OBSIDIAN_PLATE, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe PIPE = create("pipe", b -> b
            .doubleIngredient(AllBlocks.FLUID_PIPE)
            .output(Items.COPPER_INGOT)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe RLAMP = create("rlamp", b -> b
            .doubleIngredient(Blocks.REDSTONE_LAMP)
            .output(Items.GLOWSTONE, 2)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe VAULT = create("vault", b -> b
            .doubleIngredient(AllBlocks.ITEM_VAULT)
            .output(Items.IRON_INGOT, 4)
            .speed(WelderRecipeSpeed.FAST)
    );

    GeneratedRecipe INDUSTRIAL_SPRING_ALLOY = create("industrial_spring_alloy", b -> b
            .doubleIngredient(CSpringsBlocks.INDUSTRIAL_SPRING_ALLOY)
            .output(CSpringsItems.SPRING_ALLOY_NUGGET, 4)
            .speed(WelderRecipeSpeed.FAST)
    );

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        // Возвращаем статический TYPE_INFO из вашего класса рецепта
        return WelderRecipe.TYPE_INFO;
    }

    @Override
    protected WelderRecipe.Builder<WelderRecipe> getBuilder(ResourceLocation id) {
        // Используем конструктор вашего билдера
        return new WelderRecipe.Builder<>(WelderRecipe::new, id);
    }
}
