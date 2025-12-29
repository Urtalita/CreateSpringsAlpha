package net.Portality.createsprings.compat;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.config.ModConfigs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VSCompatibilityManager {

    public static void checkAndLoadCompatibility() {
        if (ModList.get().isLoaded("valkyrienskies")) {
            try {
                Class<?> loaderClass = Class.forName("vs.VSCompatibilityLoader");
                loaderClass.getMethod("load").invoke(null);
                if(ModConfigs.common().VS_COMPATIBILITY.get()){

                }
            } catch (Exception e) {
                return;
            }
        }
    }
}
