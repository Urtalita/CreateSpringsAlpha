package net.Portality.createsprings.ponders;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.Portality.createsprings.blocks.ModBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CSpringsPonders {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(ModBlocks.SPRING)
                .addStoryBoard("spring", CSpringsScenes::spring, AllCreatePonderTags.KINETIC_RELAYS);
    }
}
