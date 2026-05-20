package com.Portality.createsprings.blocks.advanced.AndesiteMold;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class MoldBlockEntity extends SmartBlockEntity {
    public ItemStack heldStack = ItemStack.EMPTY;

    public boolean filled = true;
    public int delay = 20;
    private int lastdelay = 20;

    private final IItemHandler itemHandler = new MoldItemHandler();

    public MoldBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        heldStack = new ItemStack(CSpringsBlocks.LARGE_SPRING_COIL.get().asItem());
    }

    public float getDelay(float pt){
        int del = (20 - delay) / 20;
        int lastdel = (20 - lastdelay) / 20;
        return Mth.lerp(pt, del, lastdel);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void tick() {
        super.tick();
        if(!filled){
            delay--;
            lastdelay = delay;
            if(delay == -1){
                level.setBlock(worldPosition,
                        CSpringsBlocks.ANDESITE_MOLD.get().defaultBlockState().setValue(FACING, getBlockState().getValue(FACING)), 3);
            }
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("heldStack")) {
            this.heldStack = ItemStack.parse(registries, tag.getCompound("heldStack")).orElse(ItemStack.EMPTY);
        }
        this.filled = tag.getBoolean("filled");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (!heldStack.isEmpty()) {
            tag.put("heldStack", heldStack.save(registries));
        }
        tag.putBoolean("filled", filled);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    private class MoldItemHandler implements IItemHandler {
        @Override
        public int getSlots() { return 1; }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot == 0 ? heldStack : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            // Судя по логике, вставка запрещена, если это форма для выдачи
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || amount <= 0 || heldStack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack extracted = heldStack.copyWithCount(1);
            if (!simulate) {
                heldStack = ItemStack.EMPTY; // Очищаем после извлечения
                filled = false;
                setChanged(); // Не забываем обновлять состояние
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) { return 1; }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false; // Запрещаем ручную вставку
        }
    }
}
