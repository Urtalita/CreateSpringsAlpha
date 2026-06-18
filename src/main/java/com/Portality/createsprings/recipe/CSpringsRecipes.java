package com.Portality.createsprings.recipe;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.recipe.Casting.CastingRecipe;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CSpringsRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateSprings.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CreateSprings.MODID);

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
        return RECIPE_TYPES.register(id, () -> new RecipeType<>() {
            public String toString() {
                return id;
            }
        });
    }

    public static final Supplier<RecipeType<WelderRecipe>> WELDER_TYPE = register("welding");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WelderRecipe>> WELDING =
            SERIALIZERS.register("welding", () -> new WelderRecipe.Serializer<>(WelderRecipe::new));

    public static final Supplier<RecipeType<CastingRecipe>> CASTING_TYPE = register("casting");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CastingRecipe>> CASTING =
            SERIALIZERS.register("casting", () -> new CastingRecipe.Serializer<>(CastingRecipe::new));

    public static void register(IEventBus event) {

        SERIALIZERS.register(event);
        RECIPE_TYPES.register(event);
    }
}
