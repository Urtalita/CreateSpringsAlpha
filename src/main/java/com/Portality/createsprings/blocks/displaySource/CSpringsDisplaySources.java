package com.Portality.createsprings.blocks.displaySource;

import com.Portality.createsprings.CreateSprings;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CSpringsDisplaySources {
    private static final CreateRegistrate REGISTRATE = CreateSprings.registrate();

    public static final RegistryEntry<DisplaySource, SpringDisplaySource> CHARGE = simple("spring", SpringDisplaySource::new);
    public static final RegistryEntry<DisplaySource, LargeSpringDisplaySource> LARGE_CHARGE = simple("large_spring", LargeSpringDisplaySource::new);

    public static final Map<String, RegistryEntry<DisplaySource, ? extends DisplaySource>> LEGACY_NAMES = Util.make(() -> {
        Map<String, RegistryEntry<DisplaySource, ? extends DisplaySource>> map = new HashMap<>();

        map.put("spring_display_source", CHARGE);
        map.put("large_spring_display_source", LARGE_CHARGE);

        return map;
    });

    private static <T extends DisplaySource> RegistryEntry<DisplaySource, T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displaySource(name, supplier).register();
    }

    public static void register() {
    }
}
