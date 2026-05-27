package com.Portality.createsprings.datagen;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.ponders.CSpringsPonderPlugin;
import com.Portality.createsprings.datagen.advancement.CSpringsAdvancements;
import com.Portality.createsprings.datagen.recipes.CSpringsAssemblyRecipeGen;
import com.Portality.createsprings.datagen.recipes.MixingRecipeGen;
import com.Portality.createsprings.datagen.recipes.WeldingRecipesGen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.data.recipe.CreateSequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CSpringsDatagen {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new WeldingRecipesGen(output, registries));
        generator.addProvider(event.includeServer(), new MixingRecipeGen(output, registries));
        generator.addProvider(event.includeServer(), new CSpringsAssemblyRecipeGen(output, registries));

        generator.addProvider(event.includeServer(), new CSpringsAdvancements(output, registries));
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/createsprings/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    public static void addExtraRegistrateData() {
        CreateSprings.CSPRINGS_REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            provideDefaultLang("tooltips", langConsumer);
            provideDefaultLang("manual", langConsumer);

            CSpringsAdvancements.provideLang(langConsumer);
            providePonderLang(langConsumer);
        });
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        PonderIndex.addPlugin(new CSpringsPonderPlugin());

        PonderIndex.getLangAccess().provideLang(CreateSprings.MODID, consumer);
    }
}
