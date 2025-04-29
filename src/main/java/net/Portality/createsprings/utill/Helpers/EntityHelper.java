package net.Portality.createsprings.utill.Helpers;

import net.minecraft.world.InteractionHand;

public class EntityHelper {
    public static InteractionHand getOppositeHand(InteractionHand hand){
        if(hand == InteractionHand.MAIN_HAND){
            return InteractionHand.OFF_HAND;
        }
        return InteractionHand.OFF_HAND;
    }
}
