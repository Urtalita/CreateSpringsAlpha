package com.Portality.createsprings.client.sounds;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.ClientForgeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class CSpringsSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CreateSprings.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent>
            BWEUM = registerSoundEvent("standart_bweum"),
            BWEUM_SHOOT1 = registerSoundEvent("shooting_bweum1"),
            LARGE_BWEUM = registerSoundEvent("large_bweum"),
            PUNCHCARD = registerSoundEvent("punchcard"),
            BROKEN_PSE = registerSoundEvent("broken_pse");
    ;

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = CreateSprings.asResource(name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void playOnServer(Level level, BlockPos pos, float volume, SoundEvent event){
        level.playSound(null, pos,
                event,
                SoundSource.NEUTRAL, volume, 1F);
    }

    public static void playLargeBweum(Level level, BlockPos pos, float volume){
        level.playSound(null, pos,
                CSpringsSounds.LARGE_BWEUM.get(),
                SoundSource.NEUTRAL, volume, 1F);

        if(level.isClientSide()){
            ClientForgeHandler.start((int) (40 * volume));
        } else {
            List<? extends Player> players = level.players();
            for(Player player : players){
                BlockPos playerPos = player.getOnPos();

                double distance = playerPos.getCenter().distanceTo(pos.getCenter());
                if(distance > 16 && distance < 1000){
                    level.playSound(null, playerPos,
                            CSpringsSounds.LARGE_BWEUM.get(),
                            SoundSource.NEUTRAL, volume / 10f, 1F);
                }
            }
        }
    }

    public static void playBweum(Level level, BlockPos pos, float volume){
        level.playSound(null, pos,
                CSpringsSounds.BWEUM_SHOOT1.get(),
                SoundSource.NEUTRAL, volume, 1F);
    }

    public static void playBweum(Level level, BlockPos pos){
        playBweum(level, pos, 1);
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
