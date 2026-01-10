package net.Portality.compat.coldsweat;

import net.Portality.createsprings.server.PSEHeatEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ColdSweatCreateSpringsPlugin {
    @SubscribeEvent
    public static void onPSEHeat(PSEHeatEvent event){
        Player player = event.getPlayer();
        int mode = event.getMode();

        player.addEffect(new MobEffectInstance(com.momosoftworks.coldsweat.core.init.EffectInit.WARMTH.get()
                , 20, mode * 2, true, false, true));
    }
}
