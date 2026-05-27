package com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.Portality.createsprings.blocks.advanced.spring.SpringInstance.SPRING_LEN;

public class EngineBrokenArmorLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public EngineBrokenArmorLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitc) {

        BrokenPSEItem item = BrokenPSEItem.getWornBy(entity);
        if (item == null)
            return;

        if (entity.getPose() == Pose.SLEEPING)
            return;

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model))
            return;


        if(entity.getPose() == Pose.CROUCHING){
            ms.translate(0, 0, 5/16f);
        }

        BlockState renderedState = CSpringsBlocks.SPRING.get().defaultBlockState();
        VertexConsumer vc = buffer.getBuffer(Sheets.cutoutBlockSheet());
        ms.translate(-1 / 2f, 13 / 16f, 12/16f);
        ms.scale(1, -1, -1);

        SuperByteBuffer engine = CachedBuffers.partial(CSpringsPartalModels.BROKEN_PSE_PARTAL, renderedState);
        SuperByteBuffer part = CachedBuffers.partial(CSpringsPartalModels.BROKEN_PSE_MOVING_PART, renderedState);

        Vec3 speed = getInterpolatedClientVelocity(entity, partialTick).scale(40);
        double angleX = Math.clamp(speed.y, -40, 40);

        double horizontalSpeed = getHorizontalInterpolatedVelocityInLookDirection(entity, partialTick) * 40;
        double angleXAdditional = Math.clamp(horizontalSpeed, -20, 20);


        ms.pushPose();

        model.body.translateAndRotate(ms);

        engine.center()
                .rotateYDegrees(180)
                .uncenter();

        engine.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.popPose();



        ms.pushPose();

        model.body.translateAndRotate(ms);

        part.center()
                .rotateYDegrees(180)

                .rotateXDegrees((float) (angleX + angleXAdditional))
                //.rotateYDegrees((float) angleY)
                //.rotateZDegrees((float) angleZ)

                .uncenter();

        part.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.popPose();
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values())
            registerOn(renderer);
        //for (EntityRenderer<?> renderer : renderManager.renderers.values())
        //    registerOn(renderer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        EngineBrokenArmorLayer<?, ?> layer = new EngineBrokenArmorLayer<>(livingRenderer);
        livingRenderer.addLayer((EngineBrokenArmorLayer) layer);
    }

    //a bunch of math

    public Vec3 getInterpolatedClientVelocity(LivingEntity entity, float partialTicks) {
        double currentX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double currentY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double currentZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        double lastX = Mth.lerp(partialTicks - 1.0F, entity.xo, entity.getX());
        double lastY = Mth.lerp(partialTicks - 1.0F, entity.yo, entity.getY());
        double lastZ = Mth.lerp(partialTicks - 1.0F, entity.zo, entity.getZ());

        return new Vec3(currentX - lastX, currentY - lastY, currentZ - lastZ);
    }

    public double getHorizontalInterpolatedVelocityInLookDirection(LivingEntity entity, float partialTicks) {
        // 1. Вычисляем интерполированную скорость за кадр рендера (X и Z)
        double currentX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double currentZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        double lastX = Mth.lerp(partialTicks - 1.0F, entity.xo, entity.getX());
        double lastZ = Mth.lerp(partialTicks - 1.0F, entity.zo, entity.getZ());

        double velocityX = currentX - lastX;
        double velocityZ = currentZ - lastZ;

        // 2. Получаем интерполированное направление взгляда (yaw)
        float interpolatedYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());

        // 3. Создаем нормализованный горизонтальный вектор взгляда (без учета наклона головы pitch)
        // В Minecraft угол 0 градусов смотрит на Юг (Z+), расчет идет через радианы
        float angleRad = interpolatedYaw * (Mth.PI / 180.0F);
        double lookX = -Mth.sin(angleRad);
        double lookZ = Mth.cos(angleRad);

        // 4. Скалярное произведение горизонтальной скорости и вектора взгляда
        // Формула: (V.x * L.x) + (V.z * L.z)
        return (velocityX * lookX) + (velocityZ * lookZ);
    }
}
