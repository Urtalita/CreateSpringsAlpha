package net.Portality.createsprings.Entities.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringProjectileRenderer extends EntityRenderer<SpringProjectile> {
    public static final ResourceLocation TEXTURE = CreateSprings.asResource("textures/entity/projectile/spring_projectile.png");
    private final ItemRenderer itemRenderer;

    public SpringProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SpringProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F
        ));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot())
        ));

        poseStack.scale(2,2,2);

        BakedModel model = this.itemRenderer.getItemModelShaper()
                .getItemModel(ModItems.SPRING_PROJECTILE_ITEM.get()); // Ваш предмет-модель

        this.itemRenderer.render(
                new ItemStack(ModItems.SPRING_PROJECTILE_ITEM.get()),
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                model
        );
        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpringProjectile entity) {
        return TEXTURE;
    }
}
