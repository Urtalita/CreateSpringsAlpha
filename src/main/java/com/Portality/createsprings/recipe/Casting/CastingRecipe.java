package com.Portality.createsprings.recipe.Casting;

import com.Portality.createsprings.recipe.CSpringsRecipes;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import com.Portality.createsprings.recipe.Welding.WelderRecipeParams;
import com.Portality.createsprings.recipe.Welding.WelderRecipeSpeed;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class CastingRecipe extends ProcessingRecipe<CastingRecipeWrapper, CastingRecipeParams> {
    public static final IRecipeTypeInfo TYPE_INFO = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath("createsprings", "casting");
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) CSpringsRecipes.CASTING.get();
        }

        @Override
        public <V extends RecipeInput, R extends Recipe<V>> RecipeType<R> getType() {
            return (RecipeType<R>) CSpringsRecipes.CASTING_TYPE.get();
        }
    };

    public CastingRecipe(CastingRecipeParams params) {
        super(TYPE_INFO, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }

    @Override
    public RecipeType<?> getType() {
        return CSpringsRecipes.CASTING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CSpringsRecipes.CASTING.get();
    }

    @Override
    public boolean matches(CastingRecipeWrapper castingRecipeWrapper, Level level) {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @FunctionalInterface
    public interface Factory<R extends CastingRecipe> extends ProcessingRecipe.Factory<CastingRecipeParams, R> {
        R create(CastingRecipeParams params);
    }

    public static class Builder<R extends CastingRecipe> extends ProcessingRecipeBuilder<CastingRecipeParams, R, CastingRecipe.Builder<R>> {
        public Builder(CastingRecipe.Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected CastingRecipeParams createParams() {
            return new CastingRecipeParams();
        }

        @Override
        public CastingRecipe.Builder<R> self() {
            return this;
        }
    }

    public static class Serializer<R extends CastingRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ProcessingRecipe.Factory<CastingRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, CastingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, CastingRecipeParams.STREAM_CODEC);
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
