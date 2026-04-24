package com.Portality.createsprings.recipe.Welding;

import com.Portality.createsprings.recipe.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class WelderRecipe extends ProcessingRecipe<WelderRecipeWrapper, WelderRecipeParams> {
    public static final IRecipeTypeInfo TYPE_INFO = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath("createsprings", "welding");
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            // Убедись, что берешь WELDING
            return (T) ModRecipes.WELDING.get();
        }

        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            // Убедись, что берешь WELDER_TYPE
            return (RecipeType<R>) ModRecipes.WELDER_TYPE.get();
        }
    };

    private WelderRecipeSpeed speed;

    public WelderRecipe(WelderRecipeParams params) {
        super(TYPE_INFO, params);
        speed = params.getSpeed();
    }

    public ItemStack getResult() {
        return results.getFirst().getStack();
    }

    public WelderRecipeSpeed getSpeed() {
        return speed;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.WELDER_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WELDING.get();
    }

    @Override
    public boolean matches(WelderRecipeWrapper input, Level world) {
        if (ingredients.size() < 2) return false;

        ItemStack stack1 = input.getItem(0);
        ItemStack stack2 = input.getItem(1);

        // Используем стандартную проверку ингредиентов (поддерживает теги!)
        Ingredient first = ingredients.get(0);
        Ingredient second = ingredients.get(1);

        boolean matchOrder1 = first.test(stack1) && second.test(stack2);
        boolean matchOrder2 = first.test(stack2) && second.test(stack1);

        return matchOrder1 || matchOrder2;
    }
    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @FunctionalInterface
    public interface Factory<R extends WelderRecipe> extends ProcessingRecipe.Factory<WelderRecipeParams, R> {
        R create(WelderRecipeParams params);
    }

    public static class Builder<R extends WelderRecipe> extends ProcessingRecipeBuilder<WelderRecipeParams, R, Builder<R>> {
        public Builder(WelderRecipe.Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        public Builder<R> speed(WelderRecipeSpeed speed) {
            this.params.speed = speed;
            return self();
        }

        public Builder<R> doubleIngredient(ItemLike item) {
            return self().require(item).require(item);
        }

        @Override
        protected WelderRecipeParams createParams() {
            return new WelderRecipeParams();
        }

        @Override
        public WelderRecipe.Builder<R> self() {
            return this;
        }
    }

    public static class Serializer<R extends WelderRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ProcessingRecipe.Factory<WelderRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, WelderRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, WelderRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }
    }
}