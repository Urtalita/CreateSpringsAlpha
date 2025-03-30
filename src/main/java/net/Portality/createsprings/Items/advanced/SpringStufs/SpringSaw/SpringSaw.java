package net.Portality.createsprings.Items.advanced.SpringStufs.SpringSaw;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringDrill.SpringDrillRenderer;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSpeedSys;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SpringSaw extends AxeItem implements CustomArmPoseItem {

    private static final Tier IRON_TIER = Tiers.IRON;
    private final SpringPoweredCore core;
    private final SpringSpeedSys SpeedSys;

    public SpringSaw(Properties properties) {
        super(IRON_TIER, 1, -2.8F, properties
                .durability(-1)
                .rarity(Rarity.UNCOMMON));
        SpeedSys = new SpringSpeedSys();
        this.core = new SpringPoweredCore(2);;
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
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        SpeedSys.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringSawRenderer()));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if(!stack.getOrCreateTag().contains("contains")){
            CompoundTag tag = stack.getOrCreateTag();
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(AllBlocks.MECHANICAL_SAW.asItem());

            CompoundTag contains = tag.getCompound("contains");

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
            }
            tag.put("contains", contains);
        }
        SpeedSys.appendHoverText(stack, level, tooltip, flag);
        core.appendHoverText(stack, level, tooltip, flag);
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
    public int getUseDuration(ItemStack stack) {
        return 0; // Убираем длительность использования
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

        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)){
            return true;
        }
        if (core.addStackedLogick(AllBlocks.MECHANICAL_SAW.asItem(), stack1, stack2, action, player)){
            core.switchToolInHand(player, slot, ModItems.SPRING_BASE.get(), stack1);
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
        if(core.overrideStackedOnOther(stack, slot, action, player)){
            return true;
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }
}
