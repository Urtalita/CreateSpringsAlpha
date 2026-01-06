package net.Portality.createsprings.menus.PortativeEngine;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.client.CSpringsGuiTextures;
import net.Portality.createsprings.client.CSpringsMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PortativeSteamEngineMenu extends MenuBase<ItemStack> {

    private Slot slot;

    public PortativeSteamEngineMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }
    public PortativeSteamEngineMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static PortativeSteamEngineMenu create(int id, Inventory inv, ItemStack stack) {
        return new PortativeSteamEngineMenu((MenuType) CSpringsMenus.PSE.get(), id, inv, stack);
    }

    public void shrink(){
        if(slot.getItem().getCount() <= 0){return;}

        ItemStack stack = slot.getItem();
        stack.shrink(1);
        slot.set(stack);
    }

    @Override
    protected ItemStack createOnClient(FriendlyByteBuf friendlyByteBuf) {
        Player player = Minecraft.getInstance().player;
        InteractionHand hand = player.getUsedItemHand();
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof PortativeSteamEngineItem) {
            return stack;
        } else {
            ItemStack stack1 = player.getItemBySlot(EquipmentSlot.CHEST);
            return stack1;
        }
    }

    @Override
    protected void initAndReadInventory(ItemStack contentHolder) {

    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(pIndex);

        if (clickedSlot != null && clickedSlot.hasItem()) {
            ItemStack slotStack = clickedSlot.getItem();
            itemstack = slotStack.copy();

            // Если кликнули по burnStack слоту (индекс 0)
            if (pIndex == 0) {
                // Перемещаем из burnStack в инвентарь игрока
                if (!moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                clickedSlot.onQuickCraft(slotStack, itemstack);
            }
            // Если кликнули по инвентарю игрока
            else {
                // Пытаемся переместить в burnStack слот
                if (moveItemStackTo(slotStack, 0, 1, false)) {
                    // Успешно переместили в burnStack
                }
                else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                clickedSlot.set(ItemStack.EMPTY);
            } else {
                clickedSlot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onTake(pPlayer, slotStack);

            // Обновляем burnStack после любого перемещения, которое могло затронуть burnStack слот
            updateBurnStack(this.slots.get(0).getItem());
        }

        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        if(ForgeHooks.getBurnTime(pStack, null) <= 0){
            return false;
        }
        return super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
    }

    @Override
    public void clicked(int index, int flags, ClickType type, Player player) {
        super.clicked(index, flags, type, player);

        if(slot != null){
            updateBurnStack(slot.getItem());
        }
    }

    private void updateBurnStack(ItemStack stack){
        player.getItemBySlot(EquipmentSlot.CHEST).getOrCreateTag().put("burnStack", stack.serializeNBT());
    }

    @Override
    protected void addSlots() {

        int invX = (CSpringsGuiTextures.PORTATIVE_STEAM_BG.getWidth() - AllGuiTextures.PLAYER_INVENTORY.getWidth()) / 2 + 8;
        int invY = CSpringsGuiTextures.PORTATIVE_STEAM_BG.getHeight() - 51 + 18;

        ItemStackHandler inv = new ItemStackHandler(1);
        inv.setStackInSlot(0, ItemStack.of(contentHolder.getOrCreateTag().getCompound("burnStack")));

        slot = new SlotItemHandler(inv, 0, 16, 176);
        this.addSlot(slot);

        this.addPlayerSlots(invX, invY);
    }

    @Override
    protected void saveData(ItemStack stack) {

    }
}
