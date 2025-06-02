package net.Portality.createsprings.Items.advanced.SpringStufs;

import com.simibubi.create.AllBlocks;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.Items.ModItems;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.world.item.*;

import static net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer.getSpeed;
import static net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore.*;

public class SpringSpeedSys {

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        double speed = getSpeed(tag);

        tooltip.add(Component.translatable("tooltip.springstuf.speed").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.valueOf( (int) (speed * 2 * 2.56))).withStyle(getSpeedColor(speed, level))));
    }

    private ChatFormatting getSpeedColor(Double speed, Level level){
        if(speed == 0){return ChatFormatting.GRAY;}
        if(speed < 10){return ChatFormatting.GREEN;}
        if(speed < 40){return ChatFormatting.AQUA;}
        if (speed < 50){return ChatFormatting.LIGHT_PURPLE;}
        if (speed < 100){return ChatFormatting.RED;}

        return (level.getGameTime() % 40 < 21) ? ChatFormatting.DARK_RED : ChatFormatting.RED;
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        double speedMultiplier = 1 + stack.getOrCreateTag().getDouble("Speed")/300f;
        return (float) (speedMultiplier * Config.speed_coef);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        float stored;
        double speed = tag.getDouble("Speed");

        if(tag.getInt("Springs_rn") == 0){return InteractionResultHolder.fail(stack);}

        stored = getAllStoredSum(getAllStored(2, tag));

        Item hook = getItemFromContains(stack, Blocks.TRIPWIRE_HOOK.asItem());
        if(hook != null){
            speed += stored / 8 / 2;
            stored = 0;

            if(speed > 25000){
                placeLava(level, player, stack, hand);
                speed = 25000;
            }

            tag.putBoolean("splash", true);
            tag.putInt("shiftTick", AnimationTickHolder.getTicks() % Config.spring_splash_duration);

            player.playSound(SoundEvents.ITEM_BREAK, 0.5F, 1.0F);
        } else {
            if (stored > 5000 && speed < 5500){
                speed += 250;
                stored -= 2000;
                if(speed > 5000) speed = 5000;
            }
        }

        tag.putDouble("Speed", speed);

        float[] allsu = getAllStored(2, tag);
        putAllStored(spreadSu(allsu, stored), tag);
        return InteractionResultHolder.pass(stack);
    }

    public static void placeLava(Level level, Player player, ItemStack stack, InteractionHand hand){
        switchTagInHandByHand(player, hand, ModItems.SPRING_BASE.get(), stack, 2);
        player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.5F, 1.0F);

        Vec3 viewVec = player.getViewVector(1);
        viewVec = viewVec.scale(2);
        viewVec = viewVec.add(0, 1, 0);
        Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
        viewVec = viewVec.add(playerPos);

        level.setBlock(BlockPos.containing(viewVec), Blocks.ANDESITE.defaultBlockState(), 3);
    }

    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        CompoundTag tag = stack.getOrCreateTag();
        double speed = tag.getDouble("Speed");

        if(speed > 0) {
            if(AnimationTickHolder.getTicks(level) % 40 == 0) {
                tag.putDouble("LastSpeed", speed);
                speed = Math.max(speed - 40, 0);
                tag.putDouble("Speed", speed);
            }

            if(stack.getOrCreateTag().getBoolean("splash")){
                long phase = (AnimationTickHolder.getTicks(level) - stack.getOrCreateTag().getInt("shiftTick")) % Config.spring_splash_duration + 1;
                if(Config.spring_splash_duration == phase){
                    stack.getOrCreateTag().putBoolean("splash", false);
                }
            }
        }
    }
}
