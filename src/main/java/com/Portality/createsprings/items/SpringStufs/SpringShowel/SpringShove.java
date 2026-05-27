package com.Portality.createsprings.items.SpringStufs.SpringShowel;

import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.ISpringPoweredTool;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedSys;
import com.simibubi.create.AllItems;
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

public class SpringShove extends ShovelItem implements CustomArmPoseItem, ISpringPoweredTool {
    private static final Tier IRON_TIER = Tiers.IRON;
    private final SpringPoweredCore core;
    private final SpringSpeedSys SpeedSys;

    public SpringShove(Properties properties) {
        super(IRON_TIER, properties
                .durability(-1).component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                .rarity(Rarity.UNCOMMON));
        SpeedSys = new SpringSpeedSys();

        Supplier<Item>[] allowedModifficators = new Supplier[]{
                () -> CSpringsItems.PUNCHCARD.get(),
                () -> Items.TRIPWIRE_HOOK,
        };

        this.core = new SpringPoweredCore(2, allowedModifficators);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if(!isCorrectToolForDrops(stack, state))
            return 1.0F;

        return SpeedSys.getDestroySpeed(stack, state);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return SpeedSys.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        SpeedSys.onInventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringShoveRenderer()));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        core.checkAndAddModifier(stack, AllItems.WHISK.asItem());
        SpeedSys.appendHoverText(stack, tooltipComponents, tooltipFlag);
        core.appendHoverText(stack, tooltipComponents, tooltipFlag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return core.getTooltipImage(p_150902_);
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
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess access) {
        core.checkAndAddModifier(stack1, AllItems.WHISK.asItem());
        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)){
            return true;
        }
        if (core.addStackedLogic(AllItems.WHISK.asItem(), stack1, stack2, action, player)){
            core.switchTagInHand(player, slot, CSpringsItems.SPRING_BASE.get(), stack1);
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
        core.checkAndAddModifier(stack, AllItems.WHISK.asItem());
        if(core.overrideStackedOnOther(stack, slot, action, player)){
            return true;
        }
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
