package net.Portality.createsprings.recipe.NbtShapelessRecipe;

import com.google.gson.JsonObject;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class NbtAwareShapelessRecipe extends ShapelessRecipe {
    public NbtAwareShapelessRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(id, group, CraftingBookCategory.MISC,result, ingredients);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(container, registryAccess);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.hasTag()) {
                if(stack.getItem() == ModBlocks.SPRING.asItem()){
                    CompoundTag tag = result.getOrCreateTag();
                    tag.putFloat("Stored", SpringItem.getStoredSu(stack));
                    break;
                }
            }
        }
        return result;
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
            return new NbtAwareShapelessRecipe(
                    recipeId,
                    recipe.getGroup(),
                    recipe.getResultItem(null),
                    recipe.getIngredients()
            );
        }
    }
}
