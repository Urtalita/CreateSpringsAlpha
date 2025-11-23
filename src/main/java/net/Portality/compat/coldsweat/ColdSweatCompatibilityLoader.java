package net.Portality.compat.coldsweat;

import net.Portality.createsprings.CreateSprings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColdSweatCompatibilityLoader {

    public static void load() {
        // Этот код выполнится ТОЛЬКО если Cold Sweat установлен
        MinecraftForge.EVENT_BUS.addListener(ColdSweatCreateSpringsPlugin::onEntitySpawn);
        MinecraftForge.EVENT_BUS.addListener(ColdSweatCreateSpringsPlugin::onModifiersRegister);
    }
}
