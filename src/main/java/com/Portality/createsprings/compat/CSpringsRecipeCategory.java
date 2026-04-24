package com.Portality.createsprings.compat;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Recipe;

public abstract class CSpringsRecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {
    public CSpringsRecipeCategory(Info<T> info) {
        super(info);
    }
}
