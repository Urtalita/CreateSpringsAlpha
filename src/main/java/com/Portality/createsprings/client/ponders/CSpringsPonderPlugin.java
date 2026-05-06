package com.Portality.createsprings.client.ponders;

import com.Portality.createsprings.CreateSprings;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
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
}
