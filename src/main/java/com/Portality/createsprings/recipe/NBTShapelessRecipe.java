package com.Portality.createsprings.recipe;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.advanced.Spring.SpringItem;
import com.Portality.createsprings.items.advanced.hat.ColorComponent;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.box.PackageItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.http.conn.util.InetAddressUtils;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class NBTShapelessRecipe extends ShapelessRecipe {

    public NBTShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(CSpringsItems.SUS_PACKAGE);
        List<ItemStack> ingredients = input.items();
        boolean firstdye = true;

        for (ItemStack ingredient : ingredients) {
            float stored = SpringItem.getStoredSu(ingredient);
            if (stored != 0) {
                result.set(CSpringsDataComponents.STORED_SINGLE, stored);
            }

            if (ingredient.getItem() instanceof DyeItem){
                result = new ItemStack(CSpringsItems.HAT.get());
                if(firstdye){
                    getDyeARGB((DyeItem) ingredient.getItem(), result, true);
                    firstdye = false;
                }
                getDyeARGB((DyeItem) ingredient.getItem(), result, false);
            }
        }

        return result;
    }

    public static void getDyeARGB(DyeItem dyeItem, ItemStack stack, boolean mode) {
        DyeColor color = dyeItem.getDyeColor();

        int r = color.getFireworkColor() >> 16 & 0xFF;
        int g = color.getFireworkColor() >> 8 & 0xFF;
        int b = color.getFireworkColor() & 0xFF;

        stack.set(CSpringsDataComponents.COLOUR, new ColorComponent(new Color(r, g, b)));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends ShapelessRecipe.Serializer {
        public static final Serializer INSTANCE = new Serializer();
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec((p_340779_) -> {
            return p_340779_.group(Codec.STRING.optionalFieldOf("group", "").forGetter((p_301127_) -> {
                return p_301127_.getGroup();
            }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((p_301133_) -> {
                return p_301133_.category();
            }), ItemStack.STRICT_CODEC.fieldOf("result").forGetter((p_301142_) -> {
                return p_301142_.getResultItem(null);
            }), Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((p_301021_) -> {
                Ingredient[] aingredient = (Ingredient[])p_301021_.toArray((x$0) -> {
                    return new Ingredient[x$0];
                });
                if (aingredient.length == 0) {
                    return DataResult.error(() -> {
                        return "No ingredients for shapeless recipe";
                    });
                } else {
                    return aingredient.length > ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth() ? DataResult.error(() -> {
                        return "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth());
                    }) : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }
            }, DataResult::success).forGetter((p_300975_) -> {
                return p_300975_.getIngredients();
            })).apply(p_340779_, NBTShapelessRecipe::new);
        });

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<ShapelessRecipe> codec() {
            return CODEC;
        }

        private static ShapelessRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            CraftingBookCategory craftingbookcategory = (CraftingBookCategory)buffer.readEnum(CraftingBookCategory.class);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll((p_319735_) -> {
                return (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            });
            ItemStack itemstack = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
            return new NBTShapelessRecipe(s, craftingbookcategory, itemstack, nonnulllist);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ShapelessRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            buffer.writeVarInt(recipe.getIngredients().size());
            Iterator var2 = recipe.getIngredients().iterator();

            while(var2.hasNext()) {
                Ingredient ingredient = (Ingredient)var2.next();
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(null));
        }
    }
}