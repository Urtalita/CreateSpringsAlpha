package net.Portality.createsprings.recipe.Casting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.recipe.CSpringsRecipeSerialiser;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.Portality.createsprings.recipe.Welding.WelderRecipeSpeed;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class CastingRecipeSerialiser extends CSpringsRecipeSerialiser<CastingRecipe> {
    @Override
    public ItemStack getIcon() {
        return ModBlocks.FILLED_ANDESITE_MOLD.asStack();
    }

    @Override
    public CastingRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
        ItemStack output = readOutput(json.get("result"));

        return new CastingRecipe(recipeId, output);
    }

    @Override
    public CastingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        ItemStack result = buf.readItem();

        return new CastingRecipe(id, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, CastingRecipe recipe) {
        buf.writeItem(recipe.result);
    }
}
