package com.Portality.createsprings.client;

import com.Portality.createsprings.CreateSprings;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CSpringsSpriteShifts {
    public static final CTSpriteShiftEntry SPRING_ALLOY_CASING = omni("spring_alloy_casing");
    public static final CTSpriteShiftEntry CUT_SPRING_ALLOY = omni("cut_spring_alloy");
    public static final CTSpriteShiftEntry WEATHERED_IRON = omni("weathered_iron"), WEATHERED_IRON_SIDE = omni("weathered_iron_side");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, CreateSprings.asResource("block/" + blockTextureName),
                CreateSprings.asResource("block/" + connectedTextureName + "_connected"));
    }
}
