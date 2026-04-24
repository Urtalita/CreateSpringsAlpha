package com.Portality.createsprings.items.SpringStufs;

import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.server.CSpringsDataComponents;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.*;

public class SpringSpeedSys {
    public static float MAX_REGULAR_SPEED = 5000;
    public static float MAX_OVERCLOCKED_SPEED = 25000;

    public static void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        double speed = getSpeed(stack);

        tooltip.add(Component.translatable("tooltip.springstuf.speed").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.valueOf( (int) (speed * 2 * 2.56))).withStyle(getSpeedColor(speed))));
    }

    private static ChatFormatting getSpeedColor(Double speed){
        if(speed == 0){return ChatFormatting.GRAY;}
        if(speed < 10){return ChatFormatting.GREEN;}
        if(speed < 40){return ChatFormatting.AQUA;}
        if (speed < 50){return ChatFormatting.LIGHT_PURPLE;}
        if (speed < 100){return ChatFormatting.RED;}

        return (speed % 40 < 21) ? ChatFormatting.DARK_RED : ChatFormatting.RED;
    }

    public static float getDestroySpeed(ItemStack stack, BlockState state) {
        double speedMultiplier = 1 + getRealSpeed(stack)/300f;
        return (float) (speedMultiplier * ModConfigs.common().SPRING_TOOL_SPEED_COEF.get());
    }

    public static InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        float stored;
        float speed = getRealSpeed(stack);

        if(getSprings(stack) == 0){return InteractionResultHolder.fail(stack);}

        stored = getAllStoredSum(getAllStored(stack));

        Item hook = getItemFromContains(stack, Blocks.TRIPWIRE_HOOK.asItem());
        if(hook != null){
            speed += stored / 8 / 2;
            stored = 0;

            if(speed > MAX_OVERCLOCKED_SPEED){
                speed = MAX_OVERCLOCKED_SPEED;
                placeLava(level, player, stack, hand);
                stack.set(CSpringsDataComponents.MODIFIERS, new CompoundTag());

                CompoundTag modifiers = getContent(stack);
                modifiers.remove(BuiltInRegistries.ITEM.getKey(Blocks.TRIPWIRE_HOOK.asItem()).toString());
                stack.set(CSpringsDataComponents.MODIFIERS, modifiers);
            }

            CSpringsSounds.playBweum(level, player.getOnPos());

            if(level.isClientSide()){
                ClientSpringAnimation.start();
            }

            player.playSound(SoundEvents.ITEM_BREAK, 0.5F, 1.0F);
        } else {
            if (stored > MAX_REGULAR_SPEED && speed < 5500){
                speed += 250;
                stored -= 2500;
                if(speed > MAX_REGULAR_SPEED) speed = MAX_REGULAR_SPEED;
            }
        }

        stack.set(CSpringsDataComponents.TOOL_SPEED, speed);

        float[] allsu = getAllStored(stack);
        //putAllPrevStored(allsu, stack);
        putAllStored(spreadSu(allsu, stored), stack);
        return InteractionResultHolder.pass(stack);
    }

    public static void placeLava(Level level, Player player, ItemStack stack, InteractionHand hand){
        //switchTagInHandByHand(player, hand, ModItems.SPRING_BASE.get(), stack, 2);
        player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.5F, 1.0F);

        Vec3 viewVec = player.getViewVector(1);
        viewVec = viewVec.scale(2);
        viewVec = viewVec.add(0, 1, 0);
        Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
        viewVec = viewVec.add(playerPos);

        level.setBlock(BlockPos.containing(viewVec), Blocks.ANDESITE.defaultBlockState(), 3);
    }

    public static void onInventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean isSelected) {
        double speed = getRealSpeed(stack);

        if(speed > 0) {
            if(level.getGameTime() % 40 == 0) {
                if(entity instanceof LivingEntity livingEntity){
                    ItemStack chestStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
                    /*
                    if(chestStack.getItem() instanceof PortativeSteamEngineItem){
                        if (speed < 7500){
                            if(chestStack.getOrCreateTag().getFloat("engineSpeed") < 30){
                                changeSpeed(tag, -40);
                            } else {
                                tag.putDouble("LastSpeed", speed);
                                tag.putDouble("Speed", speed);
                            }
                        } else {
                            changeSpeed(tag, -40);
                        }
                    } else {

                    }
                    */
                    changeSpeed(stack, -40);
                }
            }
        }

        if(level.getGameTime() % 10 == 0){
            //PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(stack, level, player, slotIndex, selectedIndex, PunchcardExecutor.SPRING_BASE, ModItems.SPRING_BASE.get()));
        }
    }

    public static void changeSpeed(ItemStack stack, float add){
        float speed = getRealSpeed(stack);
        stack.set(CSpringsDataComponents.TOOL_LAST_SPEED, speed + add);
        speed = Math.max(speed + add, 0);
        stack.set(CSpringsDataComponents.TOOL_SPEED, speed);
    }

    public static double getSpeedCoef(ItemStack stack){
        double speed = getRealSpeed(stack);
        return (speed / MAX_REGULAR_SPEED) / 10;
    }

    public static double getSpeed(ItemStack stack){
        return Mth.lerp((Minecraft.getInstance().level.getGameTime() + AnimationTickHolder.getPartialTicks() - 1)
                % 40 / 40f ,getLastSpeed(stack) / 100, getRealSpeed(stack) / 100);
    }

    public static float getRealSpeed(ItemStack stack){
        Float f = stack.get(CSpringsDataComponents.TOOL_SPEED);
        if(f != null) return f;
        return 0;
    }

    public static float getLastSpeed(ItemStack stack){
        Float f = stack.get(CSpringsDataComponents.TOOL_LAST_SPEED);
        if(f != null) return f;
        return 0;
    }

}
