package com.Portality.createsprings.client;

import com.Portality.createsprings.CreateSprings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public final class CSpringsKeybindings {
    public static final CSpringsKeybindings INSTANCE = new CSpringsKeybindings();

    private CSpringsKeybindings(){}

    private static final String CATEGORY = "key.categories." + CreateSprings.MODID;

    //Register in main class also

    public final KeyMapping PSEOpenKey = new KeyMapping(
            "key." + CreateSprings.MODID + ".pse_open",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_Z, -1),
            CATEGORY
    );

    public final KeyMapping PSEBoostKey = new KeyMapping(
            "key." + CreateSprings.MODID + ".pse_boost",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_X, -1),
            CATEGORY
    );

    public final KeyMapping PSEDashKey = new KeyMapping(
            "key." + CreateSprings.MODID + ".pse_dash",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_G, -1),
            CATEGORY
    );

    public final KeyMapping PSEReleaseKey = new KeyMapping(
            "key." + CreateSprings.MODID + ".pse_release",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_V, -1),
            CATEGORY
    );

    public final KeyMapping ActivatePunchcard = new KeyMapping(
            "key." + CreateSprings.MODID + ".activate_punchcard",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_TAB, -1),
            CATEGORY
    );
}
