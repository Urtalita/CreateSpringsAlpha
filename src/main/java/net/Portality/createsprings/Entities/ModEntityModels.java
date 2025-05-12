package net.Portality.createsprings.Entities;

import net.Portality.createsprings.CreateSprings;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModEntityModels {
    public static final ModelLayerLocation EMPTY_ARMOR = new ModelLayerLocation(
            new ResourceLocation(CreateSprings.MODID, "hat"),
            "main"
    );
}
