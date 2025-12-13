package net.Portality.createsprings.Items.advanced.SpringStufs.ExplosionСhamber;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public enum ExplosionChamberFuel {
    GUNPOWDER(Items.GUNPOWDER, 12000),
    TNT(Blocks.TNT.asItem(), 75000),
    END_CRYSTAL(Items.END_CRYSTAL, 160000),
    STAR(Items.FIREWORK_STAR, 20000),
    RESPAWN_ANCHOR(Blocks.RESPAWN_ANCHOR.asItem(), 160000),
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
