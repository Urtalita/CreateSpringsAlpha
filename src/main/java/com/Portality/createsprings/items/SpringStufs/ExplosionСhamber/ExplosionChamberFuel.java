package com.Portality.createsprings.items.SpringStufs.ExplosionСhamber;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public enum ExplosionChamberFuel {
    GUNPOWDER(Items.GUNPOWDER, 120000),
    TNT(Blocks.TNT.asItem(), 750000),
    END_CRYSTAL(Items.END_CRYSTAL, 1600000),
    STAR(Items.FIREWORK_STAR, 200000),
    RESPAWN_ANCHOR(Blocks.RESPAWN_ANCHOR.asItem(), 1600000),
    ;

    public Item item;
    public int addedSuSec;

    ExplosionChamberFuel(Item item, int addedSuSec) {
        this.item = item;
        this.addedSuSec = addedSuSec;
    }

    public static int getByItem(Item item){
        for (ExplosionChamberFuel fuel : ExplosionChamberFuel.values()){
            if(fuel.item == item){
                return fuel.addedSuSec;
            }
        }
        return 0;
    }
}
