package net.Portality.createsprings.recipe.Welding;

import net.Portality.createsprings.recipe.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class WelderRecipe implements Recipe<RecipeWrapper> {
    private final ResourceLocation id;
    public final Ingredient firstBlock;
    public final Ingredient secondBlock;
    public final ItemStack result;
    public final WelderRecipeSpeed speed;

    public WelderRecipe(ResourceLocation id, Ingredient firstBlock, Ingredient secondBlock, ItemStack result, WelderRecipeSpeed speed) {
        this.id = id;
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;
        this.result = result;
        this.speed = speed;
    }

    // Кастомный метод проверки блоков
    public boolean matches(BlockState state1, BlockState state2) {
        boolean matchOrder1 = firstBlock.test(state1.getBlock().asItem().getDefaultInstance())
                && secondBlock.test(state2.getBlock().asItem().getDefaultInstance());

        boolean matchOrder2 = firstBlock.test(state2.getBlock().asItem().getDefaultInstance())
                && secondBlock.test(state1.getBlock().asItem().getDefaultInstance());

        return matchOrder1 || matchOrder2;
    }

    public int getSpeed() {
        return speed.getSpeedValue(); // Для совместимости со старым кодом
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, firstBlock, secondBlock);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.WELDER_TYPE.get(); // Используем зарегистрированный тип
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WELDING.get(); // Правильная ссылка
    }

    @Override
    public boolean matches(RecipeWrapper p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeWrapper p_44001_, RegistryAccess p_267165_) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}