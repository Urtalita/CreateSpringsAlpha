package com.Portality.createsprings.items.advanced.hat;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public class HatModel extends BakedModelWrapper<BakedModel> {

    public HatModel(BakedModel template) {
        super(template);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraItemDisplayContext, PoseStack mat, boolean leftHanded) {
        if (cameraItemDisplayContext == ItemDisplayContext.HEAD)
            return CSpringsPartalModels.HAT.get()
                    .applyTransform(cameraItemDisplayContext, mat, leftHanded);
        return super.applyTransform(cameraItemDisplayContext, mat, leftHanded);
    }
}
