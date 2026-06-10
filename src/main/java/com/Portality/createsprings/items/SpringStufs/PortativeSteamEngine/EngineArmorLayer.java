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

import static com.Portality.createsprings.blocks.advanced.spring.SpringInstance.SPRING_LEN;

public class EngineArmorLayer <T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public EngineArmorLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float yaw, float pitch,
                       float pt, float p_225628_8_, float p_225628_9_, float p_225628_10_) {

        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(entity);
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

        ItemStack engineStack = getEngineStack(entity.getArmorSlots());

        int engineSpeed = PortativeSteamEngineItem.getSpeed(engineStack);
        boolean boost = PortativeSteamEngineItem.getOverdrive(engineStack);

        BlockState renderedState = CSpringsBlocks.SPRING.get().defaultBlockState();
        VertexConsumer vc = buffer.getBuffer(Sheets.cutoutBlockSheet());
        ms.translate(-1 / 2f, 13 / 16f, 12/16f);
        ms.scale(1, -1, -1);

        SuperByteBuffer engine = CachedBuffers.partial(CSpringsPartalModels.PORTATIVE_ENGINE, renderedState);
        SuperByteBuffer shaft = CachedBuffers.partial(CSpringsPartalModels.ENGINE_SHAFT, renderedState);
        SuperByteBuffer mid = CachedBuffers.partial(CSpringsPartalModels.ENGINE_MID, renderedState);
        SuperByteBuffer piston = CachedBuffers.partial(CSpringsPartalModels.ENGINE_PISTON, renderedState);

        if(boost){
            engineSpeed = 100;
        }

        ms.pushPose();

        model.body.translateAndRotate(ms);
        float curRot = engineSpeed * AnimationTickHolder.getRenderTime(entity.level()) % 360;

        engine.center()
                .rotateYDegrees(180)
                .uncenter();

        engine.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.translate(0, 0, -2/16f);

        shaft.rotateAround(Axis.XP.rotationDegrees(curRot), 0, 14/16f , 9/16f);

        shaft.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        float midTranslate = (Mth.sin(AngleHelper.rad(curRot) + Mth.PI/2 + Mth.PI)) / 16f * 2 + 2/16f;

        mid.rotateAround(Axis.XP.rotationDegrees(getMidRotation(curRot)), 0, 8/16f + midTranslate, 9/16f);

        mid.disableDiffuse()
                .translate(0, midTranslate, 0)
                .light(light)
                .renderInto(ms, vc);

        piston.disableDiffuse()
                .translate(0, midTranslate, 0)
                .light(light)
                .renderInto(ms, vc);

        int springs = SpringPoweredCore.getSprings(engineStack);
        var list = engineStack.get(CSpringsDataComponents.STORED_LIST);

        if(list != null){
            if(springs > 0 && !list.isEmpty()){
                ms.translate(0, 0, 2/16f);
                ms.rotateAround(Axis.YP.rotationDegrees(-90f), 0,0,0);
                ms.translate(-1/16f, 6/16f, -8/16f);
                renderSpring(light, ms, list.getFirst(), SPRING_LEN, CSpringsPartalModels.CHAMBER_SPRING_PLATE, CSpringsPartalModels.CHAMBER_SPRING_PIECE, renderedState, vc);

                if(springs == 2 && list.size() <= 2){
                    ms.rotateAround(Axis.YP.rotationDegrees(180f), 0,0,0);
                    ms.translate(-16/16f, 0, 0);
                    renderSpring(light, ms, list.get(1), SPRING_LEN, CSpringsPartalModels.CHAMBER_SPRING_PLATE, CSpringsPartalModels.CHAMBER_SPRING_PIECE, renderedState, vc);
                }
            }
        }

        ms.popPose();
    }

    public ItemStack getEngineStack(Iterable<ItemStack> stacks){
        for(ItemStack stack : stacks){
            if(stack.getItem() == CSpringsItems.PORTATIVE_STEAM_ENGINE.get()){return stack;}
        }
        return null;
    }

    public float getMidRotation(float rot){
        float normalized = (rot - 90) % 360;
        return (float) (-22.5 * Math.cos(Math.toRadians(normalized)));
    }

    public static void renderSpring(int light, PoseStack ms, float stored, int springLen, PartialModel plate, PartialModel piece, BlockState renderedState, VertexConsumer vc){
        float progress = 1 - (stored / ModConfigs.common().SPRING_CAPACITY.get() / 2f);

        SuperByteBuffer plateRenderer = CachedBuffers.partial(plate, renderedState);
        plateRenderer.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        SuperByteBuffer pieceRenderer = CachedBuffers.partial(piece, renderedState);
        for(int i = 0; i < springLen; i++){
            pieceRenderer.disableDiffuse()
                    .light(light)
                    .rotateCenteredDegrees(i * 90, Axis.ZP)
                    .renderInto(ms, vc);

            ms.translate(0, 0, 1/16f * progress * 0.5f);
        }

        ms.translate(0, 0, 1/16f);
        plateRenderer.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.translate(0, 0, -1/16f);

        ms.translate(0, 0, -(springLen)/16f * progress * 0.5f);
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
        EngineArmorLayer<?, ?> layer = new EngineArmorLayer<>(livingRenderer);
        livingRenderer.addLayer((EngineArmorLayer) layer);
    }
}
