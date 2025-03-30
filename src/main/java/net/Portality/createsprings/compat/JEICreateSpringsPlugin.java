package net.Portality.createsprings.compat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.recipe.ModRecipes;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static net.Portality.createsprings.blocks.ModBlocks.FRICTION_WELDER;
import static net.Portality.createsprings.recipe.ModRecipes.WELDER_TYPE;

@JeiPlugin
public class JEICreateSpringsPlugin implements IModPlugin {
    private static final ResourceLocation ID = CreateSprings.asResource("jei_plugin");
    public static final mezz.jei.api.recipe.RecipeType<WelderRecipe> WELDER_JEI_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(CreateSprings.asResource("welding"), WelderRecipe.class);

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }

    public IIngredientManager ingredientManager;
    final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        ALL.clear();

        ALL.add(builder(WelderRecipe.class)
                .addTypedRecipes(WELDER_TYPE::get)
                .itemIcon(FRICTION_WELDER.get())
                .emptyBackground(177, 77)
                .build("welding", WelderCategory::new));

        ALL.forEach(registration::addRecipeCategories);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemStack welderItem = new ItemStack(FRICTION_WELDER.get().asItem());
        // Используем WELDER_JEI_TYPE вместо WELDER_TYPE.get()
        registration.addRecipeCatalyst(welderItem, WELDER_JEI_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();
        ALL.forEach(c -> c.registerRecipes(registration));
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> {
        private final Class<? extends T> recipeClass;
        private Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }

        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        public CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = () -> Collections.emptyList();
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(CreateSprings.asResource(name), recipeClass),
                    Component.translatable(CreateSprings.MODID + ".recipe." + name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            return category;
        }
    }
}
