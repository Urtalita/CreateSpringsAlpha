package net.Portality.compat.vs;

import net.Portality.createsprings.CreateSprings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VSCompatibilityLoader {
    public static void load() {
        ValkyrienSkiesMod.getApi().registerAttachment(SpringForceAttachment.class);
        MinecraftForge.EVENT_BUS.addListener(CSpringsVsCompatibility::onSpringSplash);
    }
}
