package net.Portality.createsprings.blocks.displaySource;

import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.DeathCounterDisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CSpringsDisplaySources {
    private static final CreateRegistrate REGISTRATE = CreateSprings.registrate();

    public static final RegistryEntry<SpringDisplaySource> CHARGE = simple("spring", SpringDisplaySource::new);
    public static final RegistryEntry<LargeSpringDisplaySource> LARGE_CHARGE = simple("large_spring", LargeSpringDisplaySource::new);

    public static final Map<String, RegistryEntry<? extends DisplaySource>> LEGACY_NAMES = Util.make(() -> {
        Map<String, RegistryEntry<? extends DisplaySource>> map = new HashMap<>();

        map.put("spring_display_source", CHARGE);
        map.put("large_spring_display_source", LARGE_CHARGE);

        return map;
    });

    private static <T extends DisplaySource> RegistryEntry<T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displaySource(name, supplier).register();
    }

    public static void register() {
    }
}
