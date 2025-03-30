package net.Portality.createsprings.recipe.Welding;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WelderContext implements Container {
    @Override public int getContainerSize() { return 0; }
    @Override public boolean isEmpty() { return true; }
    @Override public ItemStack getItem(int slot) { return ItemStack.EMPTY; }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }
}
