package com.Portality.createsprings.server;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

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
