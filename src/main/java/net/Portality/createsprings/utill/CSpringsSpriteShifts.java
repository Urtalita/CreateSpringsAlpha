package net.Portality.createsprings.utill;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.Portality.createsprings.CreateSprings;

public class CSpringsSpriteShifts {
    public static final CTSpriteShiftEntry SPRING_ALLOY_CASING = omni("spring_alloy_casing");

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
