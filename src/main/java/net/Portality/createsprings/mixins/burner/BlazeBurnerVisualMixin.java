package net.Portality.createsprings.mixins.burner;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.transform.Translate;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.Portality.createsprings.utill.mixins.CshatAccessor;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlazeBurnerVisual.class)
public class BlazeBurnerVisualMixin{

    @Shadow private BlazeBurnerBlock.HeatLevel heatLevel;
    private TransformedInstance cspringsHat;
    private TransformedInstance cspringsHat1;
    private TransformedInstance cspringsHat2;
    private BlazeBurnerBlockEntity blockEntity;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "TAIL",
                    target = "<init>(Lcom/simibubi/create/foundation/render/VisualizationContext;Lcom/simibubi/create/content/contraptions/behaviour/BlazeBurnerBlockEntity;F)V"
            ),
            remap = false
    )

    private void onConstructorHead(
            VisualizationContext ctx,
            BlazeBurnerBlockEntity blockEntity,
            float partialTick,
            CallbackInfo ci
    ) {
        // Код, добавляемый в начало конструктора
        cspringsHat = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.HAT))
                .createInstance();

        cspringsHat1 = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.HAT3))
                .createInstance();

        cspringsHat2 = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.HAT4))
                .createInstance();

        cspringsHat.light(LightTexture.FULL_BRIGHT);
        cspringsHat1.light(LightTexture.FULL_BRIGHT);
        cspringsHat2.light(LightTexture.FULL_BRIGHT);

        cspringsHat.setVisible(false);
        cspringsHat1.setVisible(false);
        cspringsHat2.setVisible(false);

        this.blockEntity = blockEntity;
    }

    @Inject(
            method = "beginFrame",
            at = @At("TAIL"),
            remap = false
    )
    private void onAnimateTail(DynamicVisual.Context ctx, CallbackInfo ci) {

        if(blockEntity instanceof CshatAccessor){
            float horizontalAngle = AngleHelper.rad(((BlazeBurnerBlockEntityAccessor) blockEntity).getHeadAngle().getValue());
            boolean cshat = ((CshatAccessor) blockEntity).getCshat();
            CompoundTag tag = ((CshatAccessor) blockEntity).getCshatStack().getOrCreateTag();

            cspringsHat.setIdentityTransform()
                    .translate(blockEntity.getBlockPos())
                    .translateY(0.75F)
                    .translate(Translate.CENTER)
                    .rotateY(horizontalAngle)
                    .translateBack(Translate.CENTER)
                    .setChanged();

            cspringsHat1.setIdentityTransform()
                    .translate(blockEntity.getBlockPos())
                    .translateY(0.75F)
                    .translate(Translate.CENTER)
                    .rotateY(horizontalAngle)
                    .translateBack(Translate.CENTER)
                    .color(tag.getInt("red"), tag.getInt("green"), tag.getInt("blue"))
                    .setChanged();

            cspringsHat2.setIdentityTransform()
                    .translate(blockEntity.getBlockPos())
                    .translateY(0.75F)
                    .translate(Translate.CENTER)
                    .rotateY(horizontalAngle)
                    .translateBack(Translate.CENTER)
                    .color(tag.getInt("red1"), tag.getInt("green1"), tag.getInt("blue1"))
                    .setChanged();

            cspringsHat.setVisible(cshat);
            cspringsHat1.setVisible(cshat);
            cspringsHat2.setVisible(cshat);
        }
    }

    @Inject(
            method = "_delete",
            at = @At("TAIL"),
            remap = false
    )
    private void onDelete(CallbackInfo ci) {
        cspringsHat.delete();
        cspringsHat1.delete();
        cspringsHat2.delete();
    }
}
