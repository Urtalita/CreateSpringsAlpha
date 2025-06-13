package net.Portality.createsprings.Items.advanced.SpringStufs.ExplosionСhamber;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.utill.Helpers.ParticleHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore.*;

public class ChamberItem extends Item {
    private final SpringPoweredCore core;
    private final int SPRINGS = 1;

    public ChamberItem(Properties p_41383_) {
        super(p_41383_);
        this.core = new SpringPoweredCore(SPRINGS);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        core.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return core.getTooltipImage(p_150902_);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ChamberItemRenderer()));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)){
            return true;
        }

        HashMap<Item, Integer> chamber_fuel = Config.chamber_fuel;
        for (Map.Entry<Item, Integer> entry : chamber_fuel.entrySet()) {
            Item key = entry.getKey();

            if(addItem(key, stack1, stack2)){
                addFuel(stack1, key, true);
                return true;
            }
            if(removeItem(key, stack1, stack2, action, player)){
                addFuel(stack1, key, false);
                return true;
            }
        }

        return false;
    }

    private void addFuel(ItemStack stack, Item item, boolean addMode){
        CompoundTag tag = stack.getOrCreateTag();
        int fuel = tag.getInt("fuel");

        int addFuel = Config.chamber_fuel.get(item);

        if (addMode){
            fuel += addFuel;
            if (fuel > Config.spring_capacity){fuel = Config.spring_capacity;}
        } else {
            fuel -= addFuel;
            if (fuel < 0){fuel = 0;}
        }

        tag.putInt("fuel", fuel);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if(core.overrideStackedOnOther(stack, slot, action, player)){
            return true;
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        float stored = getAllStoredSum(getAllStored(SPRINGS, tag));

        if(tag.getInt("Springs_rn") < 1){return super.use(level, player, hand);}
        if(tag.getInt("fuel") < 1){return super.use(level, player, hand);}

        stored += tag.getInt("fuel");
        tag.putInt("fuel", 0);

        tag.put("contains", new CompoundTag());

        if(stored > Config.spring_capacity){stored = Config.spring_capacity;}

        putAllStored(spreadSu(getAllStored(SPRINGS, tag), stored), tag);

        level.playSound(null, BlockPos.containing(player.position()),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.NEUTRAL, 0.5F, 1.2F);
        ParticleHelper.explodeAtPlayer(player, level);

        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }
}
