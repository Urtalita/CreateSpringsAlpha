package net.Portality.createsprings.compat;

import net.Portality.createsprings.CreateSprings;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColdSweatCompatibilityManager {

    public static void checkAndLoadCompatibility() {
        if (ModList.get().isLoaded("cold_sweat")) {
            try {
                // Этот класс находится в coldsweat_compat source set
                // Он будет загружен только если Cold Sweat присутствует
                Class<?> loaderClass = Class.forName("net.Portality.compat.coldsweat.ColdSweatCompatibilityLoader");
                loaderClass.getMethod("load").invoke(null);

            } catch (Exception e) {
                return;
            }
        }
    }
}
