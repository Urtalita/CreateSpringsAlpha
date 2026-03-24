package net.Portality.createsprings;

import net.Portality.createsprings.client.particles.CSpringsParticles;
import net.minecraftforge.eventbus.api.IEventBus;

public class CSpringsClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CSpringsParticles::registerFactories);
    }
}
