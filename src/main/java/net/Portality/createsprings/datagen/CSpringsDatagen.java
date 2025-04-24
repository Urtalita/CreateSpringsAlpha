package net.Portality.createsprings.datagen;

import com.simibubi.create.foundation.data.DamageTypeTagGen;
import com.simibubi.create.infrastructure.data.GeneratedEntriesProvider;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.damage.CSpringsEntriesProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

public class CSpringsDatagen {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existing = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        CSpringsEntriesProvider generatedEntriesProvider = new CSpringsEntriesProvider(output, lookupProvider);
        lookupProvider = generatedEntriesProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), generatedEntriesProvider);
    }
}
