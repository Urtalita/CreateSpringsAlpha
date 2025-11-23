package net.Portality.createsprings.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class Keybindings {
    public static final Keybindings INSTANCE = new Keybindings();

    private Keybindings(){}

    private static final String CATEGORY = "key.categories." + CreateSprings.MODID;

    //Register in main class also

    public final KeyMapping PSEOpenKey = new KeyMapping(
            "key." + CreateSprings.MODID + "pse_open",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_Z, -1),
            CATEGORY
    );

    public final KeyMapping PSEBoostKey = new KeyMapping(
            "key." + CreateSprings.MODID + "pse_boost",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_X, -1),
            CATEGORY
    );

    public final KeyMapping PSEDashKey = new KeyMapping(
            "key." + CreateSprings.MODID + "pse_dash",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_G, -1),
            CATEGORY
    );
}
