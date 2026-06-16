package com.Portality.createsprings.server;

import com.Portality.createsprings.CreateSprings;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.warden.Warden;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

@EventBusSubscriber(modid = CreateSprings.MODID)
public class GameBusHandler {
    @SubscribeEvent
    public static void onEntityPlaySound(PlayLevelSoundEvent.AtEntity event) {
        int a = 1;
        if (event.getEntity() instanceof Warden warden) {
            if (event.getSound().value().equals(SoundEvents.WARDEN_SONIC_CHARGE)) {
                if (!warden.level().isClientSide()) {

                } else {

                }
            }
        }
    }
}
