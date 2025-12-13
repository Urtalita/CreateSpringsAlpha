package net.Portality.createsprings.Items.advanced.SpringStufs;

import com.simibubi.create.AllBlocks;
import net.Portality.createsprings.Items.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface ISpringPoweredTool {
    SpringPoweredCore getCore();
}
