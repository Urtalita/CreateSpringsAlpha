package net.Portality.createsprings.Entities.renderer;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.packager.PackagerRenderer;
import net.Portality.createsprings.Entities.SusPackageEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SusPackageRenderer extends EntityRenderer<SusPackageEntity> {

    public SusPackageRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(SusPackageEntity p_114482_) {
        return null;
    }
}
