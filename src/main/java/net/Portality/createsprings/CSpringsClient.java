package net.Portality.createsprings;

import com.simibubi.create.AllParticleTypes;
import net.Portality.createsprings.particles.CSpringsParticles;
import net.minecraftforge.eventbus.api.IEventBus;

public class CSpringsClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CSpringsParticles::registerFactories);
    }
}
