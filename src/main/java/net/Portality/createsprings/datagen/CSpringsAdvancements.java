package net.Portality.createsprings.datagen;

import com.google.common.collect.Sets;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static net.Portality.createsprings.datagen.CreateSpringsAdvancement.TaskType.SILENT;
import static net.Portality.createsprings.datagen.CreateSpringsAdvancement.TaskType.EXPERT;
import static net.Portality.createsprings.datagen.CreateSpringsAdvancement.TaskType.NOISY;
import static net.Portality.createsprings.datagen.CreateSpringsAdvancement.TaskType.SECRET;

public class CSpringsAdvancements implements DataProvider {
    public static final List<CreateSpringsAdvancement> ENTRIES = new ArrayList<>();
    public static final CreateSpringsAdvancement START = null,

    ROOT = create("root", b -> b.icon(ModBlocks.SPRING)
            .title("Welcome to Create Springs")
            .description("springs")
            .awardedForFree()
            .special(SILENT)
    ),

    SPRING_ALLOY = create("spring_alloy", b -> b.icon(ModItems.SPRING_ALLOY)
            .title("Butter")
            .description("forge butter")
            .whenIconCollected()
            .after(ROOT)
    ),

    OBSIDIAN_PLATE = create("obsidian_plate", b -> b.icon(ModBlocks.OBSIDIAN_PLATE)
            .title("The Sturdiest plate")
            .description("make an obsidian plate")
            .whenIconCollected()
            .after(ROOT)
    ),

    SPRING = create("spring", b -> b.icon(ModBlocks.SPRING)
            .title("The Bweum")
            .description("finish spring")
            .whenIconCollected()
            .after(SPRING_ALLOY)
    ),

    SPRING_AGE = create("spring_age", b -> b.icon(ModBlocks.SPRING_ALLOY_CASING)
            .title("The Spring Age")
            .description("Use Spring Alloy to create a casing for 1 recipe and decoration")
            .whenIconCollected()
            .after(SPRING_ALLOY)
    ),

    PSE = create("pse", b -> b.icon(ModItems.PORTATIVE_STEAM_ENGINE)
            .title("Steam to Go")
            .description("Create a PSE")
            .whenIconCollected()
            .after(SPRING_AGE)
    ),

    OVERDRIVE = create("boost", b -> b.icon(Items.BARRIER)
            .title("150% Steam")
            .description("Turn on PSE overdrive")
            .after(PSE)
    ),

    EXPLOSION = create("explosion", b -> b.icon(Items.TNT)
            .title("To much steam")
            .description("explode the PSE")
            .after(OVERDRIVE)
            .special(SECRET)
    ),

    DASH = create("pse_dash", b -> b.icon(ModItems.PORTATIVE_STEAM_ENGINE)
            .title("Not a bug")
            .description("use steam dash while using steam dash")
            .after(PSE)
            .special(SECRET)
    ),


    END = null;

    //

    private static CreateSpringsAdvancement create(String id, UnaryOperator<CreateSpringsAdvancement.Builder> b) {
        return new CreateSpringsAdvancement(id, b);
    }

    //gen

    private final PackOutput output;

    public CSpringsAdvancements(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            ResourceLocation id = advancement.getId();
            if (!set.add(id))
                throw new IllegalStateException("Duplicate advancement " + id);
            Path path = pathProvider.json(id);
            futures.add(DataProvider.saveStable(cache, advancement.deconstruct()
                    .serializeToJson(), path));
        };

        for (CreateSpringsAdvancement advancement : ENTRIES)
            advancement.save(consumer);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "CreateSpring's Advancements";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (CreateSpringsAdvancement advancement : ENTRIES)
            advancement.provideLang(consumer);
    }

    public static void register() {
    }
}
