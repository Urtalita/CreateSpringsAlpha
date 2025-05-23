package net.Portality.createsprings.utill.burner;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlazeBurnerBlockEntity.class)
public interface BlazeBurnerBlockEntityAccessor {
    @Accessor("headAngle")
    LerpedFloat getHeadAngle();
}
