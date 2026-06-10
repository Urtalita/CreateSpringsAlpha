package com.Portality.createsprings.client.ponders;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CSpringsPonders {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {

        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(CSpringsBlocks.SPRING)
                .addStoryBoard("spring", CSpringsScenes.SpringPonders::spring, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("springsplash", CSpringsScenes.SpringPonders::springSplash, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("explosion", CSpringsScenes.SpringPonders::explosions, AllCreatePonderTags.KINETIC_RELAYS)
        ;

        HELPER.forComponents(CSpringsBlocks.FRICTION_WELDER)
                .addStoryBoard("welding", CSpringsScenes::welding, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);

        HELPER.forComponents(CSpringsBlocks.SPRING_CATAPULT)
                .addStoryBoard("catapult", CSpringsScenes.SpringCatapultPonders::catapult, AllCreatePonderTags.HIGH_LOGISTICS)
                .addStoryBoard("catapult_second_target", CSpringsScenes.SpringCatapultPonders::catapultSecondTarget, AllCreatePonderTags.HIGH_LOGISTICS);

        HELPER.forComponents(CSpringsBlocks.LARGE_SPRING_COIL)
                .addStoryBoard("large_spring", CSpringsScenes.LargeSpringPonders::largeSpring, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("large_spring_speed", CSpringsScenes.LargeSpringPonders::largeSpringSpeed, AllCreatePonderTags.KINETIC_RELAYS);


        HELPER.forComponents(CSpringsBlocks.KINETIC_INTERFACE)
                .addStoryBoard("kinetic_interface", CSpringsScenes::PSKI, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);

        HELPER.forComponents(CSpringsBlocks.ANALOG_TOGGLE_LATCH)
                .addStoryBoard("analog_latch", CSpringsScenes::AnalogLatch, AllCreatePonderTags.REDSTONE);
    }
}
