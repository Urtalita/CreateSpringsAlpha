package net.Portality.createsprings.compat.casting;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.compat.Welding.AnimatedWelder;
import net.Portality.createsprings.fluid.CSpringsFluids;
import net.Portality.createsprings.recipe.Casting.CastingRecipe;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Callable;

@ParametersAreNonnullByDefault
public class CastingCategory extends CreateRecipeCategory<CastingRecipe> implements IRecipeCategory<CastingRecipe> {
    private final CastingAnimation casting = new CastingAnimation();

    public CastingCategory(Info<CastingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CastingRecipe recipe, IFocusGroup iFocusGroup) {
        builder.
                addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(Ingredient.of(ModBlocks.ANDESITE_MOLD.get().asItem()));

        addFluidSlot(builder, 27, 32, new FluidStack(CSpringsFluids.SPRING_ALLOY.get(),  500));

        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 132, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(getResultItem(recipe));
    }

    @Override
    public void draw(CastingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);
        casting.draw(graphics, getBackground().getWidth() / 2 - 13, 22);
    }
}
