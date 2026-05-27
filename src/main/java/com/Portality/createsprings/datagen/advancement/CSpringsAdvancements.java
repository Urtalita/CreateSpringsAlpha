package com.Portality.createsprings.datagen.advancement;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.items.CSpringsItems;
import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
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

import static com.Portality.createsprings.datagen.advancement.CreateSpringsAdvancement.TaskType.SECRET;
import static com.Portality.createsprings.datagen.advancement.CreateSpringsAdvancement.TaskType.SILENT;

public class CSpringsAdvancements implements DataProvider {
    public static final List<CreateSpringsAdvancement> ENTRIES = new ArrayList<>();
    public static final CreateSpringsAdvancement START = null,

    ROOT = create("root", b -> b.icon(CSpringsBlocks.SPRING)
            .title("Welcome to Create Springs")
            .description("springs")
            .awardedForFree()
            .special(SILENT)
    ),

    SPRING_ALLOY = create("spring_alloy", b -> b.icon(CSpringsItems.SPRING_ALLOY)
            .title("Butter")
            .description("forge butter")
            .whenIconCollected()
            .after(ROOT)
    ),

    OBSIDIAN_PLATE = create("obsidian_plate", b -> b.icon(CSpringsBlocks.OBSIDIAN_PLATE)
            .title("The Sturdiest plate")
            .description("make an obsidian plate")
            .whenIconCollected()
            .after(ROOT)
    ),

    SPRING = create("spring", b -> b.icon(CSpringsBlocks.SPRING)
            .title("The Bweum")
            .description("finish spring")
            .whenIconCollected()
            .after(SPRING_ALLOY)
    ),

    SPRING_AGE = create("spring_age", b -> b.icon(CSpringsBlocks.SPRING_ALLOY_CASING)
            .title("The Spring Age")
            .description("Use Spring Alloy to create a casing for 1 recipe and decoration")
            .whenIconCollected()
            .after(SPRING_ALLOY)
    ),

    PSE = create("pse", b -> b.icon(CSpringsItems.PORTATIVE_STEAM_ENGINE)
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

    DASH = create("pse_dash", b -> b.icon(CSpringsItems.PORTATIVE_STEAM_ENGINE)
            .title("Double jump")
            .description("use steam dash while using steam dash")
            .after(PSE)
            .special(SECRET)
    ),

    LARGE_SPRING = create("large_spring", b -> b.icon(CSpringsBlocks.LARGE_SPRING_COIL)
            .title("large spring")
            .description("assemble large spring")
            .after(SPRING)
    ),

    PROGRAMMER = create("programmer", b -> b.icon(CSpringsItems.PUNCHCARD)
            .title("Programmer")
            .description("program your first punchcard")
            .after(SPRING_ALLOY)
    ),

    PEA_SHOOTER = create("pea_shooter", b -> b.icon(CSpringsItems.PUNCHCARD)
            .title("Pea shooter!")
            .description("automate potato cannon")
            .after(PROGRAMMER)
            .special(SECRET)
    ),



    SPRING_TRAP = create("spring_trap", b -> b.icon(CSpringsItems.SUS_PACKAGE.get())
            .title("Spring trap")
            .description("place sus package")
            .after(SPRING)
    ),

    /*


    CASH_BACK = create("cash_back", b -> b.icon(ModBlocks.SPRING.asItem())
            .title("Cash back")
            .description("get hit by your own spring")
            .after(SPRING_TRAP)
            .special(SECRET)
    ),

     */
    END = null;

    //

    private static CreateSpringsAdvancement create(String id, UnaryOperator<CreateSpringsAdvancement.Builder> b) {
        return new CreateSpringsAdvancement(id, b);
    }

    //gen

    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public CSpringsAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(provider -> {
            PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancement");
            List<CompletableFuture<?>> futures = new ArrayList<>();

            Set<ResourceLocation> set = Sets.newHashSet();
            Consumer<AdvancementHolder> consumer = (advancement) -> {
                ResourceLocation id = advancement.id();
                if (!set.add(id))
                    throw new IllegalStateException("Duplicate advancement " + id);
                Path path = pathProvider.json(id);
                LOGGER.info("Saving advancement {}", id);
                futures.add(DataProvider.saveStable(cache, provider, Advancement.CODEC, advancement.value(), path));
            };

            for (CreateSpringsAdvancement advancement : ENTRIES)
                advancement.save(consumer, provider);

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
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
