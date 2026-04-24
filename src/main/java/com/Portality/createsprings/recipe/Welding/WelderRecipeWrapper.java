package com.Portality.createsprings.recipe.Welding;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public record WelderRecipeWrapper(ItemStack first, ItemStack second) implements RecipeInput {
    public static WelderRecipeWrapper fromStates(BlockState craftState1, BlockState craftState2) {
        return new WelderRecipeWrapper(new ItemStack(craftState1.getBlock().asItem()), new ItemStack(craftState2.getBlock().asItem()));
    }

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> first;
            case 1 -> second;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
