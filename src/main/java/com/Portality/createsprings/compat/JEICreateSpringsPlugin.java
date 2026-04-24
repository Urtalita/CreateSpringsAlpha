package com.Portality.createsprings.compat;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.compat.Welding.WelderCategory;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.Portality.createsprings.blocks.ModBlocks.FRICTION_WELDER;
import static com.Portality.createsprings.recipe.ModRecipes.WELDER_TYPE;
import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

@JeiPlugin
public class JEICreateSpringsPlugin implements IModPlugin {
    private static final ResourceLocation ID = CreateSprings.asResource("jei_plugin");

    public static final mezz.jei.api.recipe.RecipeType<WelderRecipe> WELDER_JEI_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(CreateSprings.asResource("welding"), WelderRecipe.class);

    /*
    public static final mezz.jei.api.recipe.RecipeType<CastingRecipe> CASTING_JEI_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(CreateSprings.asResource("casting"), CastingRecipe.class);

     */

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    public IIngredientManager ingredientManager;
    static final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        ALL.clear();

        ALL.add(builder(WelderRecipe.class)
                .addTypedRecipes(WELDER_TYPE::get)
                .catalyst(FRICTION_WELDER::get)
                .itemIcon(FRICTION_WELDER.get())
                .emptyBackground(177, 77)
                .build("welding", WelderCategory::new));

        /*
        ALL.add(builder(CastingRecipe.class)
                .addTypedRecipes(CASTING_TYPE::get)
                .itemIcon(ANDESITE_MOLD.get())
                .emptyBackground(177, 70)
                .build("casting", CastingCategory::new));

         */

        ALL.forEach(registration::addRecipeCategories);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ALL.forEach(c -> c.registerCatalysts(registration));
        //registration.addRecipeCatalyst(new ItemStack(CAItems.DIAMOND_GRIT_SANDPAPER.get()), new ResourceLocation(Create.ID, "deploying"));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();
        ALL.forEach(c -> c.registerRecipes(registration));
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private static class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            ALL.add(category);
            return category;
        }

        @Override
        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            return build(CreateSprings.asResource(name), factory);
        }
    }
}
