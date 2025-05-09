package net.Portality.createsprings.recipe.NbtShapelessRecipe;

import com.google.gson.JsonObject;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.Items.advanced.hat.HatItem;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class NbtHatShapelessRecipe extends ShapelessRecipe {
    public NbtHatShapelessRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(id, group, CraftingBookCategory.MISC,result, ingredients);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(container, registryAccess);
        boolean firstdye = true;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof DyeItem){
                if(firstdye){
                    getDyeARGB((DyeItem) stack.getItem(), result, true);
                    firstdye = false;
                }
                getDyeARGB((DyeItem) stack.getItem(), result, false);
            }
        }
        return result;
    }

    public static void getDyeARGB(DyeItem dyeItem, ItemStack stack, boolean mode) {
        DyeColor color = dyeItem.getDyeColor();

        int r = color.getFireworkColor() >> 16 & 0xFF;
        int g = color.getFireworkColor() >> 8 & 0xFF;
        int b = color.getFireworkColor() & 0xFF;

        CompoundTag tag = stack.getOrCreateTag();
        if(mode){
            tag.putInt("red", r);
            tag.putInt("green", g);
            tag.putInt("blue", b);
        } else {
            tag.putInt("red1", r);
            tag.putInt("green1", g);
            tag.putInt("blue1", b);
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends ShapelessRecipe.Serializer {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe recipe = super.fromJson(recipeId, json);
            return new NbtHatShapelessRecipe(
                    recipeId,
                    recipe.getGroup(),
                    recipe.getResultItem(null),
                    recipe.getIngredients()
            );
        }
    }
}
