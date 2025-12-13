package net.Portality.createsprings.sounds;

import com.google.gson.JsonObject;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.RegisterEvent;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CSpringsSounds {
    public static final Map<ResourceLocation, AllSoundEvents.SoundEntry> ALL = new HashMap<>();

    public static final AllSoundEvents.SoundEntry
            BWEUM = create("standart_bweum").noSubtitle()
                    .category(SoundSource.PLAYERS)
                    .build(),

            BWEUM_SHOOT = create("shooting_bweum1").noSubtitle()
                    .addVariant("shooting_bweum2")
                    .category(SoundSource.PLAYERS)
                    .build(),

            PUNCHCARD = create("punchcard").noSubtitle()
                    .addVariant("punchcard")
                    .category(SoundSource.PLAYERS)
                    .build()
    ;

    private static AllSoundEvents.SoundEntryBuilder create(String name) {
        return create(CreateSprings.asResource(name));
    }

    public static AllSoundEvents.SoundEntryBuilder create(ResourceLocation id) {
        return new AllSoundEvents.SoundEntryBuilder(id);
    }

    public static void prepare() {
        for (AllSoundEvents.SoundEntry entry : ALL.values())
            entry.prepare();
    }

    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> {
            for (AllSoundEvents.SoundEntry entry : ALL.values())
                entry.register(helper);
        });
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (AllSoundEvents.SoundEntry entry : ALL.values())
            if (entry.hasSubtitle())
                consumer.accept(entry.getSubtitleKey(), entry.getSubtitle());
    }

    public static SoundEntryProvider provider(DataGenerator generator) {
        return new SoundEntryProvider(generator);
    }

    public static class SoundEntryProvider implements DataProvider {

        private PackOutput output;

        public SoundEntryProvider(DataGenerator generator) {
            output = generator.getPackOutput();
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            return generate(output.getOutputFolder(), cache);
        }

        @Override
        public String getName() {
            return "CreateSprings Custom Sounds";
        }

        public CompletableFuture<?> generate(Path path, CachedOutput cache) {
            path = path.resolve("assets/createsprings");
            JsonObject json = new JsonObject();
            ALL.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        entry.getValue()
                                .write(json);
                    });
            return DataProvider.saveStable(cache, json, path.resolve("sounds.json"));
        }

    }
}
