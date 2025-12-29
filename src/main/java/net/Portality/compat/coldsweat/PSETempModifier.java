package net.Portality.compat.coldsweat;

import com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.core.init.EffectInit;
import net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

import com.momosoftworks.coldsweat.api.temperature.modifier.TempModifier;

public class PSETempModifier extends TempModifier {


    @Override
    protected Function<Double, Double> calculate(LivingEntity livingEntity, Temperature.Trait trait) {

        return ((temp) -> {
            if(livingEntity instanceof Player player){
                if(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){

                    ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                    int mode = stack.getOrCreateTag().getInt("engineSpeed") / 15;

                    if(mode == 0){return temp;}
                    player.addEffect(new MobEffectInstance(EffectInit.WARMTH.get(), 20, mode * 2, true, false, false));

                    return temp;
                }
            }
            return temp;
        });
    }
}
