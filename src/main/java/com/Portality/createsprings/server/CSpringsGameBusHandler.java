package com.Portality.createsprings.server;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.items.ModItems;
import com.Portality.createsprings.items.advanced.Spring.SpringItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = CreateSprings.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CSpringsGameBusHandler {
    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack stackLeft = player.getItemInHand(InteractionHand.OFF_HAND);

            if(stack.getItem() == ModBlocks.SPRING.asItem()){
                chargeFromLanding(event, player, stack);
            }
            if(stackLeft.getItem() == ModBlocks.SPRING.asItem()){
                chargeFromLanding(event, player, stackLeft);
            }
        }
    }

    private static void chargeFromLanding(LivingFallEvent event, Player player, ItemStack stack){
        float distance = event.getDistance();
        if(distance < 4){return;}
        float added = distance * 2000;
        float su = SpringItem.getStoredSu(stack);
        if(su + added <= ModConfigs.common().SPRING_CAPACITY.get()){
            event.setDamageMultiplier(0.3f);
            SpringItem.SetSu(stack, added + su);
        } else {
            float overblocks = (su + added - ModConfigs.common().SPRING_CAPACITY.get()) / 4000f;
            event.setDamageMultiplier(overblocks / distance);
            SpringItem.SetSu(stack, ModConfigs.common().SPRING_CAPACITY.get());
        }
    }
}