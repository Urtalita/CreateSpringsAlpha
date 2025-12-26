package net.Portality.createsprings.compat;

import net.Portality.createsprings.CreateSprings;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VSCompatibilityManager {
    public static void checkAndLoadCompatibility() {
        if (ModList.get().isLoaded("valkyrienskies")) {
            try {
                Class<?> loaderClass = Class.forName("net.Portality.compat.vs.VSCompatibilityLoader");
                loaderClass.getMethod("load").invoke(null);

            } catch (Exception e) {
                return;
            }
        }
    }
}
