package com.Portality.createsprings.recipe.Welding;

import com.Portality.createsprings.recipe.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class WelderRecipe extends ProcessingRecipe<WelderRecipeWrapper, WelderRecipeParams> {
    public static final IRecipeTypeInfo TYPE_INFO = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return ModRecipes.WELDING.getId();
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) ModRecipes.WELDER_TYPE.get();
        }

        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            return (RecipeType<R>) ModRecipes.WELDER_TYPE.get();
        }
    };

    private ItemStack firstBlock;
    private ItemStack secondBlock;
    private ItemStack result;
    private WelderRecipeSpeed speed;

    public WelderRecipe(WelderRecipeParams params) {
        super(TYPE_INFO, params);
        firstBlock = params.getFirstBlock();
        secondBlock = params.getSecondBlock();
        result = params.getResult();
        speed = params.getSpeed();

    }

    public ItemStack getFirstBlock() {
        return firstBlock;
    }

    public ItemStack getSecondBlock() {
        return secondBlock;
    }

    public ItemStack getResult() {
        return result;
    }

    public WelderRecipeSpeed getSpeed() {
        return speed;
    }

    @Override
    public boolean matches(WelderRecipeWrapper input, Level world) {
        ItemStack item1 = input.first();
        ItemStack item2 = input.second();

        boolean matchOrder1 = this.firstBlock.is(item1.getItem()) && this.secondBlock.is(item2.getItem());
        boolean matchOrder2 = this.firstBlock.is(item2.getItem()) && this.secondBlock.is(item1.getItem());

        return matchOrder1 || matchOrder2;
    }
    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }

    @FunctionalInterface
    public interface Factory<R extends WelderRecipe> extends ProcessingRecipe.Factory<WelderRecipeParams, R> {
        R create(WelderRecipeParams params);
    }

    public static class Builder<R extends WelderRecipe> extends ProcessingRecipeBuilder<WelderRecipeParams, R, Builder<R>> {
        public Builder(WelderRecipe.Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
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