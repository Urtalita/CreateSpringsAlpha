package net.Portality.createsprings.recipe.Welding;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.recipe.CSpringsRecipeSerialiser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class WelderRecipeSerialiser extends CSpringsRecipeSerialiser<WelderRecipe> {

    @Override
    public ItemStack getIcon() {
        return ModBlocks.FRICTION_WELDER.asStack();
    }

    @Override
    public WelderRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
        ItemStack output = readOutput(json.get("result"));
        Ingredient input = Ingredient.fromJson(json.get("first_block"));
        Ingredient input2 = Ingredient.fromJson(json.get("second_block"));

        // Получаем строку из JSON и конвертируем в enum
        if (!json.has("speed")) {
            throw new JsonSyntaxException("Recipe " + recipeId + " requires 'speed' field!");
        }
        String speedStr = json.get("speed").getAsString();
        WelderRecipeSpeed speed = WelderRecipeSpeed.valueOf(speedStr.toUpperCase()); // Обработка регистра

        return new WelderRecipe(recipeId, input, input2, output, speed);
    }

    @Override
    public WelderRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient first = Ingredient.fromNetwork(buf);
        Ingredient second = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();
        // Читаем enum из буфера
        WelderRecipeSpeed speed = WelderRecipeSpeed.valueOf(buf.readUtf().toUpperCase());
        return new WelderRecipe(id, first, second, result, speed);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, WelderRecipe recipe) {
        recipe.firstBlock.toNetwork(buf);
        recipe.secondBlock.toNetwork(buf);
        buf.writeItem(recipe.result);

        buf.writeUtf(recipe.speed.name());
    }
}
