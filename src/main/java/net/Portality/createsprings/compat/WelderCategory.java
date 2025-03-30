package net.Portality.createsprings.compat;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WelderCategory extends CreateRecipeCategory<WelderRecipe> implements IRecipeCategory<WelderRecipe> {

    private final AnimatedWelder welder = new AnimatedWelder();
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateSprings.MODID, "friction_welder");

    public WelderCategory(Info<WelderRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WelderRecipe recipe, IFocusGroup focuses) {
        // Первый ингредиент
        builder
                .addSlot(RecipeIngredientRole.INPUT, 30, 60)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        // Второй ингредиент
        builder
                .addSlot(RecipeIngredientRole.INPUT, 50, 60)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(1));

        // Результат
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 122, 60)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(WelderRecipe recipe, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY) {

        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
        AllGuiTextures.JEI_ARROW.render(graphics, 75, 64);
        welder.draw(graphics, getBackground().getWidth() / 2 - 17, 22);

        Component speedText = Component.translatable(
                "createsprings.recipe.speed",
                Component.translatable("createsprings.speed." + recipe.speed.name().toLowerCase())
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                speedText,
                0,  // X-координата
                0,  // Y-координата (настройте по необходимости)
                0x444444, // Цвет текста
                false // Не использовать тень
        );
    }
}
