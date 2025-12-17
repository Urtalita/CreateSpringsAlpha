package net.Portality.createsprings.datagen.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.recipe.ModRecipes;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.Portality.createsprings.recipe.Welding.WelderRecipeSpeed;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

public class WeldingRecipeGen extends ProcessingRecipeGen {
    /*
    GeneratedRecipe
        SPRING_ALLOY = doRecipe(Blocks.SLIME_BLOCK, AllBlocks.BRASS_BLOCK.get(), ModItems.SPRING_ALLOY.get(), 7, WelderRecipeSpeed.SLOW)
    ;

    public GeneratedRecipe doRecipe(Block first, Block second, Item result, int count, WelderRecipeSpeed speed){
        String name = result.getDescriptionId() + "_from_" + first.getDescriptionId() + "_and_" + second.getDescriptionId();
        return create(name, b -> b.)
    }

     */

    public WeldingRecipeGen(PackOutput generator) {
        super(generator, CreateSprings.MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return (IRecipeTypeInfo) ModRecipes.WELDER_TYPE;
    }
}
