package net.Portality.createsprings.compat;

import net.Portality.createsprings.CreateSprings;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public interface IModCompatibility {
     void load();
}
