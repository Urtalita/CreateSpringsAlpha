package net.Portality.createsprings.Items.advanced.Punchcard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExecutorInfo {
    private ItemStack stack;
    private Level level;
    private Player player;
    private int slotIndex;
    private int selectedIndex;
    PunchcardExecutor executor;

    public ExecutorInfo(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex, PunchcardExecutor executor){
        this.stack = stack;
        this.level = level;
        this.player = player;
        this.slotIndex = slotIndex;
        this.selectedIndex = selectedIndex;
        this.executor = executor;
    }

    public CompoundTag getTag(){
        return stack.getOrCreateTag();
    }

    public int getSelectedIndex() {
        return selectedIndex;
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
        CompoundTag tag = this.getTag();
        CompoundTag punchcard = tag.getCompound("punchcard");
        int curAction = punchcard.getInt("curAction");
        int maxAction = punchcard.getInt("maxActions");

        curAction += 1;
        if(curAction >= maxAction){curAction = 0;}

        punchcard.putInt("curAction", curAction);
        punchcard.putInt("maxActions", maxAction);
        tag.put("punchcard", punchcard);
    }
}
