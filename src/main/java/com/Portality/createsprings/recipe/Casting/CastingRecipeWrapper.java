package com.Portality.createsprings.recipe.Casting;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class CastingRecipeWrapper implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return new ItemStack(CSpringsBlocks.LARGE_SPRING_COIL);
    }

    @Override
    public int size() {
        return 1;
    }
}
