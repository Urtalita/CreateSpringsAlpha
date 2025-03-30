package net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpringLauncherRenderer extends CustomRenderedItemModelRenderer {

    protected final PartialModel LAUNCHER_SPRING_UNCHARGED = PartialModel.of(CreateSprings.asResource("item/launcher/spring_uncharged"));;
    protected final PartialModel LAUNCHER_SPRING_CHARGED = PartialModel.of(CreateSprings.asResource("item/launcher/spring_charged"));;
    protected final PartialModel LAUNCHER_AMMO = PartialModel.of(CreateSprings.asResource("item/launcher/spring_ammo"));;
    protected final PartialModel LAUNCHER_SPYGLASS = PartialModel.of(CreateSprings.asResource("item/launcher/spyglass"));;
    protected final PartialModel LAUNCHER_BLOCK_AMMO = PartialModel.of(CreateSprings.asResource("item/launcher/spring_alloy_block_ammo"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");

        int Springs_rn = tag.getInt("Springs_rn");
        float Stored = tag.getFloat("Stored");

        renderer.render(model.getOriginalModel(), light);

        if (contains.getBoolean(SpringLauncher.Spyglass)){
            renderer.render(LAUNCHER_SPYGLASS.get(), light);
        }

        if (Springs_rn > 0){
            if (Stored == 0){
                renderer.render(LAUNCHER_SPRING_UNCHARGED.get(), light);
            } else {
                renderer.render(LAUNCHER_SPRING_CHARGED.get(), light);

                if(contains.getBoolean(SpringLauncher.BlockAmmo)){
                    renderer.render(LAUNCHER_BLOCK_AMMO.get(), light);
                } else if (Springs_rn == 2){
                    renderer.render(LAUNCHER_AMMO.get(), light);
                }
            }
        } else {
            if(contains.getBoolean(SpringLauncher.BlockAmmo)){
                renderer.render(LAUNCHER_BLOCK_AMMO.get(), light);
            }
        }
    }
}
