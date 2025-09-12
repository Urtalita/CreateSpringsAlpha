package net.Portality.createsprings.blocks.advanced.AndesiteMold;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class MoldBlockEntity extends SmartBlockEntity {
    public ItemStack heldStack = ItemStack.EMPTY;

    public boolean filled = true;
    private int delay = 20;
    private int lastdelay = 20;

    public MoldBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        heldStack = new ItemStack(ModBlocks.LARGE_SPRING_COIL.get().asItem());
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
                        ModBlocks.ANDESITE_MOLD.get().defaultBlockState().setValue(FACING, getBlockState().getValue(FACING)), 3);
            }
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heldStack = ItemStack.of(tag.getCompound("heldStack"));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("heldStack", heldStack.serializeNBT());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> new IItemHandler() {
                private final int OUTPUT_SLOT = 0;
                private final int SLOT_COUNT = 1;

                @Override
                public int getSlots() {
                    return SLOT_COUNT;
                }

                @Override
                public @NotNull ItemStack getStackInSlot(int slot) {
                    if (slot == OUTPUT_SLOT) {
                        return heldStack;
                    }
                    return ItemStack.EMPTY;
                }

                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                    if (!(stack.getItem() == ModBlocks.LARGE_SPRING_COIL.get().asItem())) {
                        return stack;
                    }

                    ItemStack result = ItemStack.EMPTY;
                    return result;
                }

                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                    if (slot != OUTPUT_SLOT || amount <= 0 || heldStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    ItemStack extracted = heldStack.copyWithCount(1);
                    filled = false;

                    return extracted;
                }

                @Override
                public int getSlotLimit(int slot) {
                    return 1;
                }

                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    return slot == OUTPUT_SLOT && stack.getItem() == ModBlocks.LARGE_SPRING_COIL.get().asItem();
                }
            }).cast();
        }
        return super.getCapability(cap, side);
    }

}
