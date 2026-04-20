package com.Portality.createsprings.items.SpringStufs.SpringLauncher;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.items.SpringStufs.SpringBase.SpringBaseRenderer;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.utill.CSpringsDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static com.Portality.createsprings.blocks.advanced.spring.SpringInstance.SPRING_LEN;
import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.getAllStored;
import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.getAllStoredSum;

public class SpringLauncherRenderer extends CustomRenderedItemModelRenderer {

    protected final PartialModel LAUNCHER_SPRING_UNCHARGED = PartialModel.of(CreateSprings.asResource("item/spring_launcher/spring_uncharged"));;
    protected final PartialModel LAUNCHER_SPRING_CHARGED = PartialModel.of(CreateSprings.asResource("item/spring_launcher/spring_charged"));;
    protected final PartialModel LAUNCHER_AMMO = PartialModel.of(CreateSprings.asResource("item/spring_launcher/spring_ammo"));;
    protected final PartialModel LAUNCHER_SPYGLASS = PartialModel.of(CreateSprings.asResource("item/spring_launcher/spyglass"));;
    protected final PartialModel LAUNCHER_BLOCK_AMMO = PartialModel.of(CreateSprings.asResource("item/spring_launcher/spring_alloy_block_ammo"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        CompoundTag contains = SpringPoweredCore.getContent(stack);

        int Springs_rn = SpringPoweredCore.getSprings(stack);
        float Stored = getAllStoredSum(getAllStored(stack));

        renderer.render(model.getOriginalModel(), light);

        if (contains.getBoolean(SpringLauncher.Spyglass)){
            renderer.render(LAUNCHER_SPYGLASS.get(), light);
        }

        var list = stack.get(CSpringsDataComponents.STORED_LIST);
        if (Springs_rn > 0){
            if (Stored == 0){
                renderer.render(LAUNCHER_SPRING_UNCHARGED.get(), light);
            } else {
                ms.translate(0, 4/16f, 8/16f);
                ms.rotateAround(Axis.XN.rotationDegrees(180), 0, 0, 0);
                SpringBaseRenderer.renderSmallSpring(renderer, light, ms, list.get(0), SPRING_LEN);
                ms.rotateAround(Axis.XN.rotationDegrees(-180), 0, 0, 0);
                ms.translate(0, -4/16f, -8/16f);

                if(contains.getBoolean(SpringLauncher.BlockAmmo)){
                    ms.translate(0, 0, 2/16f);
                    renderer.render(LAUNCHER_BLOCK_AMMO.get(), light);
                } else if (Springs_rn == 2){
                    renderer.render(LAUNCHER_AMMO.get(), light);
                }
            }
        } else {
            if(contains.getBoolean(SpringLauncher.BlockAmmo)){
                ms.translate(0, 0, 2/16f);
                renderer.render(LAUNCHER_BLOCK_AMMO.get(), light);
            }
        }
    }
}
