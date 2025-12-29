package net.Portality.compat.coldsweat;


import net.Portality.createsprings.CreateSprings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ColdSweatCreateSpringsPlugin {


    @SubscribeEvent
    public static void onModifiersRegister(com.momosoftworks.coldsweat.api.event.core.registry.TempModifierRegisterEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(CreateSprings.MODID, "portative_steam_engine"), () -> new PSETempModifier());
    }

    @SubscribeEvent
    public static void onEntitySpawn(com.momosoftworks.coldsweat.api.event.core.init.GatherDefaultTempModifiersEvent event)
    {
        // Add the TempModifier to every player's WORLD trait
        if (event.getEntity() instanceof Player player && event.getTrait() == com.momosoftworks.coldsweat.api.util.Temperature.Trait.WORLD)
        {
            com.momosoftworks.coldsweat.api.util.Temperature.addModifier(
                    player,        // The player entity
                    new PSETempModifier().tickRate(5), // A new instance of your modifier
                    com.momosoftworks.coldsweat.api.util.Temperature.Trait.CORE,  // The temperature trait to affect (e.g., CORE, WORLD, BASE)
                    com.momosoftworks.coldsweat.api.util.Placement.Duplicates.ALLOW // The policy for handling duplicate modifiers
            );
        }
    }
}
