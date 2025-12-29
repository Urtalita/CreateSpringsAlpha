package net.Portality.createsprings.compat;

import net.Portality.createsprings.CreateSprings;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColdSweatCompatibilityManager {


    public static void checkAndLoadCompatibility() {
        if (ModList.get().isLoaded("cold_sweat")) {
            try {
                IModInfo modInfo = ModList.get().getModFileById("cold_sweat").getMods().get(0);
                String version = modInfo.getVersion().toString();
                if (version.startsWith("2.4")) {return;}

                Class<?> loaderClass = Class.forName("net.Portality.compat.coldsweat.ColdSweatCompatibilityLoader");
                loaderClass.getMethod("load").invoke(null);

            } catch (Exception e) {
                return;
            }
        }
    }
}
