package com.Portality.createsprings.items.advanced.hat;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.EngineArmorLayer;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.caffeinemc.mods.sodium.mixin.features.render.immediate.matrix_stack.VertexConsumerMixin;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.Portality.createsprings.blocks.advanced.spring.SpringInstance.SPRING_LEN;
import static com.Portality.createsprings.items.advanced.hat.HatItem.readStackFromNBT;

public class HatArmorLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public HatArmorLayer(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }
    public static final float duration = 90;

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float yaw, float pitch,
                       float pt, float p_225628_8_, float p_225628_9_, float p_225628_10_) {

        HatItem item = HatItem.getWornBy(entity);
        if (item == null)
            return;

        if (entity.getPose() == Pose.SLEEPING)
            return;

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model))
            return;


        ItemStack hatStack = getHatStack(entity.getArmorSlots());

        BlockState renderedState = CSpringsBlocks.SPRING.get().defaultBlockState();
        VertexConsumer vc = buffer.getBuffer(Sheets.cutoutBlockSheet());

        SuperByteBuffer hat = CachedBuffers.partial(CSpringsPartalModels.HAT, renderedState);
        SuperByteBuffer hat2 = CachedBuffers.partial(CSpringsPartalModels.HAT2, renderedState);
        SuperByteBuffer goggles = CachedBuffers.partial(AllPartialModels.GOGGLES, renderedState);

        ms.pushPose();

        model.hat.translateAndRotate(ms);

        renderItem(hatStack, ms, vc, light);

        if(HatItem.getAnimation(hatStack)){

            float time = getTime(hatStack);
            float rotation = (float) Math.sin(time / duration * Math.PI) * 90 / 2;
            ms.rotateAround(Axis.ZP.rotationDegrees((rotation)), -8/16f, 0/16f, 0);
            if(time * -1 > (duration - 2)){hatStack.set(CSpringsDataComponents.HAT_ANIMATION, false);}
        }
        if(HatItem.hasGoggles(hatStack)){
            Vec3 offset = new Vec3(-8/16f, -18/16f, -8/16f);
            ms.translate(offset.x, offset.y, offset.z);
            goggles.disableDiffuse()
                    .light(light)
                    .renderInto(ms, vc);
            ms.translate(-offset.x, -offset.y, -offset.z);
        }

        ms.rotateAround(Axis.XP.rotationDegrees(180), 0, 0, 0);
        ms.translate(-8/16f, 6/16f, -8/16f);

        hat.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        hat2.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.popPose();
    }

    private float getTime(ItemStack stack){
        return ((Minecraft.getInstance().level.getGameTime() - HatItem.getAnimationProgress(stack)) % duration + AnimationTickHolder.getPartialTicks() - 1) * -1 * 2;
    }

    private boolean renderItem(ItemStack stack, PoseStack ms, VertexConsumer vc, int light){
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack processedStack = readStackFromNBT(stack);
        BakedModel model1 = itemRenderer.getModel(processedStack, Minecraft.getInstance().level, null, 0);

        if(processedStack.isEmpty()) return false;
        if(!model1.isCustomRenderer()){
            ms.pushPose();

            ms.rotateAround(Axis.XP.rotationDegrees(180), 0, 0, 0);
            ms.translate(-8/16f, 6/16f, -8/16f);

            ms.translate(4/16f, 0, 4/16f);
            if(processedStack.getItem() instanceof BlockItem){
                ms.scale(0.5f, 0.5f, 0.5f);
                ms.translate(4/16f, 4/16f, 4/16f);
            }
            ms.scale(0.5f, 0.5f, 0.5f);

            itemRenderer.renderModelLists(model1, processedStack, light, OverlayTexture.NO_OVERLAY, ms, vc);

            ms.popPose();
        }
        return true;
    }

    public ItemStack getHatStack(Iterable<ItemStack> stacks){
        for(ItemStack stack : stacks){
            if(stack.getItem() == CSpringsItems.HAT.get()){return stack;}
        }
        return null;
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values())
            registerOn(renderer);
        for (EntityRenderer<?> renderer : ((EntityRenderDispatcherAccessor) renderManager).create$getRenderers().values())
            registerOn(renderer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        HatArmorLayer<?, ?> layer = new HatArmorLayer<>(livingRenderer);
        livingRenderer.addLayer((HatArmorLayer) layer);
    }
}
