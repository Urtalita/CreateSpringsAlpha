package net.Portality.createsprings.ponders;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.Portality.createsprings.blocks.ModBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class CSpringsPonders {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {

        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(ModBlocks.UNFINISHED_SPRING)
                        .addStoryBoard("su_sec", CSpringsScenes.SpringPonders::suSec, AllCreatePonderTags.KINETIC_SOURCES);

        HELPER.forComponents(ModBlocks.SPRING)
                .addStoryBoard("spring", CSpringsScenes.SpringPonders::spring, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("hardness", CSpringsScenes.SpringPonders::hardness, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("springsplash", CSpringsScenes.SpringPonders::springSplash, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("explosion", CSpringsScenes.SpringPonders::explosions, AllCreatePonderTags.KINETIC_RELAYS);

        HELPER.forComponents(ModBlocks.FRICTION_WELDER)
                .addStoryBoard("welding", CSpringsScenes::welding, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);

        HELPER.forComponents(ModBlocks.SPRING_CATAPULT)
                .addStoryBoard("catapult", CSpringsScenes.SpringCatapultPonders::catapult, AllCreatePonderTags.HIGH_LOGISTICS)
                .addStoryBoard("catapult_second_target", CSpringsScenes.SpringCatapultPonders::catapultSecondTarget, AllCreatePonderTags.HIGH_LOGISTICS);

        HELPER.forComponents(ModBlocks.LARGE_SPRING_COIL)
                .addStoryBoard("large_spring", CSpringsScenes.LargeSpringPonders::largeSpring, AllCreatePonderTags.KINETIC_RELAYS)
                .addStoryBoard("large_spring_speed", CSpringsScenes.LargeSpringPonders::largeSpringSpeed, AllCreatePonderTags.KINETIC_RELAYS);

        HELPER.forComponents(ModBlocks.KINETIC_INTERFACE)
                .addStoryBoard("kinetic_interface", CSpringsScenes::PSKI, AllCreatePonderTags.CONTRAPTION_ASSEMBLY);
    }
}
