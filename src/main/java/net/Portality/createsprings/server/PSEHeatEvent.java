package net.Portality.createsprings.server;

import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.entity.player.Player;

public class PSEHeatEvent extends Event {
    public Player getPlayer() {
        return player;
    }

    public int getMode() {
        return mode;
    }

    private final Player player;
    private final int mode;

    public PSEHeatEvent(Player player, int mode) {
        this.player = player;
        this.mode = mode;
    }
}
