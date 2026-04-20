package com.Portality.createsprings.items.SpringStufs.SpringSaw;

import com.Portality.createsprings.items.ModItems;
import com.Portality.createsprings.items.SpringStufs.ISpringPoweredTool;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedSys;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpringSaw extends AxeItem implements CustomArmPoseItem, ISpringPoweredTool {

    private static final Tier IRON_TIER = Tiers.IRON;
    private final SpringPoweredCore core;

    public SpringSaw(Properties properties) {
        super(IRON_TIER, properties
                .durability(-1).component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                .rarity(Rarity.UNCOMMON));
        Supplier<Item>[] allowedModifficators = new Supplier[]{
                () -> ModItems.PUNCHCARD.get(),
                () -> Items.TRIPWIRE_HOOK,
        };

        this.core = new SpringPoweredCore(2, allowedModifficators);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if(!isCorrectToolForDrops(stack, state))
            return 1.0F;

        return SpringSpeedSys.getDestroySpeed(stack, state);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return SpringSpeedSys.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        SpringSpeedSys.onInventoryTick(stack, level, entity, slotId, isSelected);
    }


    @Override
    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    public void initializeClient(Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringSawRenderer()));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        core.checkAndAddModifier(stack, AllBlocks.MECHANICAL_SAW.asItem());
        SpringSpeedSys.appendHoverText(stack, tooltip, flag);
        core.appendHoverText(stack, tooltip, flag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return SpringPoweredCore.getTooltipImage(p_150902_);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 0;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return false; // Отключаем сброс анимации ломания блоков
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true; // Отключаем анимацию взмаха рукой
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess access) {
        core.checkAndAddModifier(stack1, AllBlocks.MECHANICAL_SAW.asItem());
        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)){
            return true;
        }
        if (core.addStackedLogic(AllBlocks.MECHANICAL_SAW.asItem(), stack1, stack2, action, player)){
            //core.switchTagInHand(player, slot, ModItems.SPRING_BASE.get(), stack1);
            player.playSound(SoundEvents.ANVIL_BREAK, 0.5F, 1.0F);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public HumanoidModel.ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        if (!player.swinging) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return null;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        core.checkAndAddModifier(stack, AllBlocks.MECHANICAL_SAW.asItem());
        //if(core.overrideStackedOnOther(stack, slot, action, player)){
        //    return true;
        //}
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public SpringPoweredCore getCore() {
        return core;
    }

    @Override
    public boolean hasSpeedSystem() {
        return true;
    }
}
