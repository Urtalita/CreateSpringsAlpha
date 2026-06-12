package com.Portality.createsprings.recipe.Casting;

import com.Portality.createsprings.recipe.CSpringsRecipes;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import com.Portality.createsprings.recipe.Welding.WelderRecipeParams;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class CastingRecipe extends ProcessingRecipe<CastingRecipeWrapper, CastingRecipeParams> {
    public static final IRecipeTypeInfo TYPE_INFO = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CSpringsRecipes.CASTING.getId();
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
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
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
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY);
    }

    @Override
    public boolean matches(CastingRecipeWrapper castingRecipeWrapper, Level level) {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return results.getFirst().getStack();
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
