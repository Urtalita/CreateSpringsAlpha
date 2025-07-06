package net.Portality.createsprings.Items.advanced.Punchcard;

import com.simibubi.create.AllItems;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.*;

public enum PunchcardExecutor {
    SPRING_BASE(ModItems.SPRING_BASE.get()),
    SPRING_LAUNCHER(ModItems.SPRING_LAUNCHER.get()),
    EXPLOSION_CHAMBER(ModItems.EXPLOSION_CHAMBER.get()),
    POTATO_CANON(AllItems.POTATO_CANNON.get()),
    BACKTANK(AllItems.COPPER_BACKTANK.get()),
    NETHERITE_BACKTANK(AllItems.NETHERITE_BACKTANK.get()),
    EXTENDRO_GRIP(AllItems.EXTENDO_GRIP.get()),
    ;

    public Item item;
    public String nameOfExecutor;

    private PunchcardExecutor(Item item){
        this.item = item;
        this.nameOfExecutor = getExecutorName(item);
    }

    private String getExecutorName(Item item){
        return item.getName(new ItemStack(item)).getString();
    }

    public static PunchcardExecutor getFromItem(Item item){
        PunchcardExecutor[] executors = PunchcardExecutor.values();
        for(int i = 0; i < executors.length; i++){
            if(item == executors[i].item){
                return executors[i];
            }
        }
        return null;
    }
}
