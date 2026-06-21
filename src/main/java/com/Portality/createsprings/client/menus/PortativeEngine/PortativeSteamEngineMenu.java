package com.Portality.createsprings.client.menus.PortativeEngine;

import com.Portality.createsprings.client.CSpringsGuiTextures;
import com.Portality.createsprings.client.menus.CSpringsMenus;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.packets.PSEClientUpdate;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class PortativeSteamEngineMenu extends MenuBase<ItemStack> {

    private Slot slot;

    public PortativeSteamEngineMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }
    public PortativeSteamEngineMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static PortativeSteamEngineMenu create(int id, Inventory inv, ItemStack stack) {
        return new PortativeSteamEngineMenu(CSpringsMenus.PSE.get(), id, inv, stack);
    }

    public void shrink(){
        if(slot.getItem().getCount() <= 0){return;}

        ItemStack stack = slot.getItem();
        stack.shrink(1);
        slot.set(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected ItemStack createOnClient(RegistryFriendlyByteBuf extraData) {
        return ItemStack.STREAM_CODEC.decode(extraData);
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

            updateBurnStack(this.slots.get(0).getItem());
        }

        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        if(pStack.getBurnTime(null) <= 0){
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
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack burnStack = stack;
            ItemStack PSE = player.getItemBySlot(EquipmentSlot.CHEST);
            if(!(PSE.getItem() instanceof PortativeSteamEngineItem)){PSE = player.getItemInHand(InteractionHand.MAIN_HAND);}
            if(!(PSE.getItem() instanceof PortativeSteamEngineItem)){PSE = player.getItemInHand(InteractionHand.OFF_HAND);}

            if (!burnStack.isEmpty()) {
                PortativeSteamEngineItem.setBurnStack(PSE, burnStack);
            } else {
                PortativeSteamEngineItem.removeBurnStack(PSE);
            }

            CustomData customData = PSE.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag nbtData = customData.copyTag();
            PacketDistributor.sendToPlayer(serverPlayer, new PSEClientUpdate(nbtData));
        }
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        updateBurnStack(slot.getItem());
    }

    @Override
    protected void addSlots() {

        int invX = (CSpringsGuiTextures.PORTATIVE_STEAM_BG.getWidth() - AllGuiTextures.PLAYER_INVENTORY.getWidth()) / 2 + 8;
        int invY = CSpringsGuiTextures.PORTATIVE_STEAM_BG.getHeight() - 51 + 18;

        ItemStackHandler inv = new ItemStackHandler(1);
        inv.setStackInSlot(0, PortativeSteamEngineItem.getBurnStack(contentHolder));

        slot = new SlotItemHandler(inv, 0, 16, 176);
        this.addSlot(slot);

        this.addPlayerSlots(invX, invY);
    }

    @Override
    protected void saveData(ItemStack stack) {

    }
}
