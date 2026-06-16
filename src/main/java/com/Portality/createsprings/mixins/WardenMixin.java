package com.Portality.createsprings.mixins;

import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(SonicBoom.class)
public class WardenMixin {
    @Inject(method = "start*", at = @At("HEAD"))
    private void onBoom(ServerLevel level, Warden warden, long gameTime, CallbackInfo ci) {

        Vec3 startPos = warden.getEyePosition();
        Optional<LivingEntity> target = warden.getEntityAngryAt();
        if(target.isEmpty()) return;
        Vec3 endPos = target.get().getBoundingBox().getCenter();

        Vec3 dif = endPos.subtract(startPos);
        Vec3 scaledDif = dif.scale(2);
        Vec3 finalEndPos = startPos.add(scaledDif);

        double distance = startPos.distanceTo(finalEndPos);
        double step = 0.5;

        Set<BlockPos> traversedBlocks = new HashSet<>();

        for (double d = 0; d <= distance; d += step) {
            Vec3 point = startPos.lerp(finalEndPos, d / distance);
            BlockPos pos = BlockPos.containing(point);

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        traversedBlocks.add(pos.offset(x, y, z).immutable());
                    }
                }
            }
        }

        for (BlockPos pos : traversedBlocks) {
            if(warden.level().getBlockEntity(pos) instanceof SpringBlockEntity be){
                be.stored = be.capacity;
                be.sendData();
            }
        }
    }
}
