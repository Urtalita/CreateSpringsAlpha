package com.Portality.createsprings;

import com.Portality.createsprings.client.particles.CSpringsParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = CreateSprings.ID, dist = Dist.CLIENT)
public class CSpringsClient {
    public CSpringsClient(IEventBus modEventBus) {
        onCtorClient(modEventBus);
    }

    public static void onCtorClient(IEventBus modEventBus) {
        modEventBus.addListener(CSpringsParticles::registerFactories);
    }
}
