package net.Portality.createsprings.sounds;

import com.google.gson.JsonObject;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.*;

import static net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS;

public class CSpringsSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CreateSprings.MODID);

    public static final RegistryObject<SoundEvent>
            BWEUM = registerSoundEvent("standart_bweum"),
            BWEUM_SHOOT1 = registerSoundEvent("shooting_bweum1"),
            PUNCHCARD = registerSoundEvent("punchcard");
    ;

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(CreateSprings.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void playOnServer(Level level, BlockPos pos, float volume, SoundEvent event){
        level.playSound(null, pos,
                event,
                SoundSource.NEUTRAL, volume, 1F);
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
