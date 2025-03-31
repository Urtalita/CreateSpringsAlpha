package net.Portality.createsprings.Items.advanced.SpringStufs;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpringSpeedSys {

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        double speed = tag.getDouble("Speed");

        tooltip.add(Component.literal("speed: ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.valueOf( (int) (speed / 50 * 2.56))).withStyle(ChatFormatting.GRAY)));
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = 1F;
        double speedMultiplier = 1 + getSpeed(stack)/300f;
        return baseSpeed * (float)speedMultiplier;
    }

    public double getSpeed(ItemStack stack){
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getDouble("Speed") : 0;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        float Stored = 0;
        // Получаем текущую скорость из NBT
        double speed = tag.getDouble("Speed");
        Stored = tag.getFloat("Stored");

        if (Stored > 5000){
            speed += 250;
            Stored -= 2000;
        }

        // Добавляем ограничение максимальной скорости
        if(speed > 5000) speed = 5000;

        tag.putDouble("Speed", speed);
        tag.putFloat("Stored", Stored);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if(level.isClientSide() || slotIndex != selectedIndex) return;

        CompoundTag tag = stack.getOrCreateTag();
        double speed = tag.getDouble("Speed");

        if(speed > 0) {
            if(level.getGameTime() % 40 == 0) {
                speed = Math.max(speed - 40, 0);
                tag.putDouble("Speed", speed);
            }
        }
    }
}
