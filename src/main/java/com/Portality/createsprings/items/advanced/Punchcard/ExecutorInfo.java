package com.Portality.createsprings.items.advanced.Punchcard;

import com.Portality.createsprings.server.CSpringsDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExecutorInfo {
    private ItemStack stack;
    private Level level;
    private Player player;
    private int slotIndex;
    private boolean isSelected;
    private PunchcardExecutor executor;
    private Item item;


    public ExecutorInfo(ItemStack stack, Level level, Player player, int slotIndex, boolean selectedIndex, PunchcardExecutor executor, Item item){
        this.stack = stack;
        this.level = level;
        this.player = player;
        this.slotIndex = slotIndex;
        this.isSelected = selectedIndex;
        this.executor = executor;
        this.item = item;
    }

    public ExecutorInfo(ItemStack stack, Level level, Player player, PunchcardExecutor executor, Item item){
        this.stack = stack;
        this.level = level;
        this.player = player;
        this.slotIndex = 0;
        this.isSelected = true;
        this.executor = executor;
        this.item = item;
    }

    public Item getItem() {return item;}

    public boolean isSelected() {
        return isSelected;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void nextAction(){
        if(stack.has(CSpringsDataComponents.PUNCHCARD)){
            CompoundTag punchcard = stack.get(CSpringsDataComponents.PUNCHCARD);
            int curAction = punchcard.getInt("curAction");
            int maxAction = punchcard.getInt("maxActions");

            curAction += 1;
            if(curAction >= maxAction){curAction = 0;}

            punchcard.putInt("curAction", curAction);
            punchcard.putInt("maxActions", maxAction);
            stack.set(CSpringsDataComponents.PUNCHCARD, punchcard);
        }
    }
}
