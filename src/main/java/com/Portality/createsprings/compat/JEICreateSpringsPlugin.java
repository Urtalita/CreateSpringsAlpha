package com.Portality.createsprings.compat;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.compat.Welding.WelderCategory;
import com.Portality.createsprings.compat.casting.CastingCategory;
import com.Portality.createsprings.recipe.Casting.CastingRecipe;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.Portality.createsprings.blocks.CSpringsBlocks.ANDESITE_MOLD;
import static com.Portality.createsprings.blocks.CSpringsBlocks.FRICTION_WELDER;
import static com.Portality.createsprings.recipe.CSpringsRecipes.CASTING_TYPE;
import static com.Portality.createsprings.recipe.CSpringsRecipes.WELDER_TYPE;

@JeiPlugin
public class JEICreateSpringsPlugin implements IModPlugin {
    private static final ResourceLocation ID = CreateSprings.asResource("jei_plugin");

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    public IIngredientManager ingredientManager;

    public static CreateRecipeCategory<CastingRecipe> CASTING_CATEGORY;
    public static CreateRecipeCategory<WelderRecipe> WELDER_CATEGORY;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        CASTING_CATEGORY = builder(CastingRecipe.class)
                .addTypedRecipes(CASTING_TYPE::get)
                .catalyst(ANDESITE_MOLD::get)
                .itemIcon(ANDESITE_MOLD.get())
                .emptyBackground(177, 70)
                .build(CreateSprings.asResource("casting"), CastingCategory::new);

        WELDER_CATEGORY = builder(WelderRecipe.class)
                .addTypedRecipes(WELDER_TYPE::get)
                .catalyst(FRICTION_WELDER::get)
                .itemIcon(FRICTION_WELDER.get())
                .emptyBackground(177, 77)
                .build(CreateSprings.asResource("welding"), WelderCategory::new);

        registration.addRecipeCategories(CASTING_CATEGORY, WELDER_CATEGORY);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        CASTING_CATEGORY.registerCatalysts(registration);
        WELDER_CATEGORY.registerCatalysts(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        CASTING_CATEGORY.registerRecipes(registration);
        WELDER_CATEGORY.registerRecipes(registration);
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private static class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            return build(CreateSprings.asResource(name), factory);
        }
    }
}
