package net.Portality.createsprings.ponders;

import com.simibubi.create.foundation.ponder.PonderWorldBlockEntityFix;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.Portality.createsprings.CreateSprings;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CSpringsPonderPlugin implements PonderPlugin {

    
    @Override
    public String getModId() {
        return CreateSprings.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CSpringsPonders.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        AllCreatePonderTags.register(helper);
    }
}
