package net.Portality.createsprings.datagen;

import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;

public class CSpringsStandardRecipeGen extends BaseRecipeProvider {

    final List<GeneratedRecipe> all = new ArrayList<>();



    public CSpringsStandardRecipeGen(PackOutput output) {
        super(output, CreateSprings.MODID);
    }
}
