package net.Portality.createsprings.menus.Punchcard;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.Portality.createsprings.menus.MenuInit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PunchcardMenu extends AbstractContainerMenu {
    private final Container container;
    String BlockName = "";
    String PunchcardName;
    ItemStack stack = ItemStack.EMPTY;
    ItemStack block = ItemStack.EMPTY;

    public PunchcardMenu(int containerId, Inventory playerInventory, ItemStack punchcardItem) {
        super(MenuInit.PUNCHCARD_MENU.get(), containerId);
        punchcardItem = punchcardItem.copy();
        this.container = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
            }
        };

        // Загрузка сохраненного блока из NBT
        CompoundTag tag = punchcardItem.getOrCreateTag();
        if (tag.contains("BlockState")) {
            CompoundTag blockStateTag = tag.getCompound("BlockState");
            BlockState savedState = parseBlockState(blockStateTag);

            BlockName = I18n.get(savedState.getBlock().getDescriptionId());
            block = new ItemStack(savedState.getBlock().asItem());
        }

        if (tag.contains("Name"));{
            PunchcardName = punchcardItem.getHoverName().getString();
        }

        stack = punchcardItem;
    }

    public String getPunchcardName(){
        return PunchcardName;
    }

    public String getBlockName() {
        return BlockName;
    }


    private BlockState parseBlockState(CompoundTag tag) {
        try {
            return BlockStateParser.parseForBlock(
                    BuiltInRegistries.BLOCK.asLookup(),
                    tag.getString("Name"),
                    true
            ).blockState();
        } catch (CommandSyntaxException e) {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}