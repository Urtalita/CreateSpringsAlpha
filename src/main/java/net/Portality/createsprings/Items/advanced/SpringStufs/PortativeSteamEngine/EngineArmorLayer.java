package net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.BacktankArmorLayer;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.utill.CSpringsPartalModels;
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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class EngineArmorLayer <T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public EngineArmorLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float yaw, float pitch,
                       float pt, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (entity.getPose() == Pose.SLEEPING)
            return;

        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(entity);
        if (item == null)
            return;

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model))
            return;

        BlockState renderedState = ModBlocks.SPRING.get().defaultBlockState();
        SuperByteBuffer engine = CachedBuffers.partial(CSpringsPartalModels.PORTATIVE_ENGINE, renderedState);
        VertexConsumer vc = buffer.getBuffer(Sheets.cutoutBlockSheet());

        ms.pushPose();

        model.body.translateAndRotate(ms);
        ms.translate(-1 / 2f, 13 / 16f, 12/16f);
        ms.scale(1, -1, -1);

        engine.center()
                .rotateYDegrees(180)
                .uncenter();

        engine.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.popPose();
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values())
            registerOn(renderer);
        for (EntityRenderer<?> renderer : renderManager.renderers.values())
            registerOn(renderer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        EngineArmorLayer<?, ?> layer = new EngineArmorLayer<>(livingRenderer);
        livingRenderer.addLayer((EngineArmorLayer) layer);
    }
}
