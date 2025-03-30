package net.Portality.createsprings.recipe;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.compat.WelderCategory;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.Portality.createsprings.recipe.Welding.WelderRecipeSerialiser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateSprings.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CreateSprings.MODID);

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
        return RECIPE_TYPES.register(id, () -> new RecipeType<>() {
            public String toString() {
                return id;
            }
        });
    }

    public static final Supplier<RecipeType<WelderRecipe>> WELDER_TYPE = register("welding");
    public static final RegistryObject<RecipeSerializer<WelderRecipe>> WELDING =
            SERIALIZERS.register("welding", () -> new WelderRecipeSerialiser());


    public static void register(IEventBus event) {

        SERIALIZERS.register(event);
        RECIPE_TYPES.register(event);
    }
}
