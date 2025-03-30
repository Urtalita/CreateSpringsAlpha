package net.Portality.createsprings.menus.Spring;

import net.Portality.createsprings.menus.MenuInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SpringMenu extends AbstractContainerMenu {
    ItemStack stack = ItemStack.EMPTY;
    public float stored = GetStoredSu(stack);

    public SpringMenu(int containerId, Inventory playerInventory, ItemStack SpringItem) {
        super(MenuInit.SPRING_MENU.get(), containerId);
        stack = SpringItem;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        return itemstack;
    }

    public float GetStoredSu(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        float stored = 0;

        CompoundTag BlockEntityTag = tag.getCompound("BlockEntityTag");
        stored = BlockEntityTag.getFloat("Stored");

        return stored;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}