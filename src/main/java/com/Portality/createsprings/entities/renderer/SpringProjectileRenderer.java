package com.Portality.createsprings.entities.renderer;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.entities.Projectile.SpringProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

public class SpringProjectileRenderer extends EntityRenderer<SpringProjectile> {
    public static final ResourceLocation TEXTURE = CreateSprings.asResource("textures/entity/projectile/spring_projectile.png");
    private final ItemRenderer itemRenderer;

    public SpringProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SpringProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F
        ));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot())
        ));

        poseStack.scale(2,2,2);

        PartialModel model = CSpringsPartalModels.SPRING_PROJECTILE; // Ваш предмет-модель
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());

        if (model == null)
            return;
        SuperByteBuffer sbb = CachedBuffers.partial(model, Blocks.AIR.defaultBlockState());
        sbb.translate(-.5, -0.5f, -.5)
                //.rotateCentered(-AngleHelper.rad(yaw + 90), Direction.UP)
                .light(packedLight)
                .nudge(entity.getId());
        sbb.renderInto(poseStack, buffer.getBuffer(RenderType.solid()));

        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpringProjectile entity) {
        return TEXTURE;
    }
}
