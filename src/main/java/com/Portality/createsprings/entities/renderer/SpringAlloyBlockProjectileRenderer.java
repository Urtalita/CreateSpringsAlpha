package com.Portality.createsprings.entities.renderer;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.entities.Projectile.SpringAlloyBlockProjectile;
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

public class SpringAlloyBlockProjectileRenderer extends EntityRenderer<SpringAlloyBlockProjectile> {
    public static final ResourceLocation TEXTURE = CreateSprings.asResource("textures/entity/projectile/spring_projectile.png");
    protected final PartialModel LAUNCHER_BLOCK_AMMO = PartialModel.of(CreateSprings.asResource("item/spring_launcher/spring_alloy_block_ammo"));
    private final ItemRenderer itemRenderer;

    public SpringAlloyBlockProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SpringAlloyBlockProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F
        ));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot())
        ));

        poseStack.scale(2,2,2);

        PartialModel model = LAUNCHER_BLOCK_AMMO; // Ваш предмет-модель
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());

        if (model == null)
            return;
        SuperByteBuffer sbb = CachedBuffers.partial(model, Blocks.AIR.defaultBlockState());
        sbb.translate(-.5, -0.5f, -.5)
                //.rotateCentered(-AngleHelper.rad(yaw + 90), Direction.UP)
                .light(packedLight)
                .nudge(entity.getId());
        sbb.renderInto(poseStack, buffer.getBuffer(RenderType.solid()));

        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpringAlloyBlockProjectile entity) {
        return TEXTURE;
    }
}

