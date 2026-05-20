package com.Portality.createsprings.items.SpringStufs.ExplosionСhamber;

import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.ISpringPoweredTool;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.ExecutorInfo;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardExecutor;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.utill.Helpers.ParticleHelper;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.*;

public class ChamberItem extends Item implements ISpringPoweredTool {
    private final SpringPoweredCore core;
    private final int SPRINGS = 1;

    public ChamberItem(Properties p_41383_) {
        super(p_41383_);
        Supplier<Item>[] allowedModifficators = new Supplier[]{
                () -> CSpringsItems.PUNCHCARD.get(),
                () -> Items.TRIPWIRE_HOOK,
        };

        this.core = new SpringPoweredCore(SPRINGS, allowedModifficators);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        core.appendHoverText(stack, tooltipComponents, tooltipFlag);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return core.getTooltipImage(p_150902_);
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

        for (ExplosionChamberFuel fuel : ExplosionChamberFuel.values()) {
            Item key = fuel.item;

            if(addItemWithCount(key, stack1, stack2)){
                addFuel(stack1, key, true);
                return true;
            }
            if(removeWithCount(key, stack1, stack2, action, player)){
                addFuel(stack1, key, false);
                return true;
            }
        }

        return false;
    }

    private void addFuel(ItemStack stack, Item item, boolean addMode){
        float fuel = getFuel(stack);

        int addFuel = ExplosionChamberFuel.getByItem(item);

        if (addMode){
            fuel += addFuel;
            if (fuel > ModConfigs.common().SPRING_CAPACITY.get()){fuel = ModConfigs.common().SPRING_CAPACITY.get();}
        } else {
            fuel -= addFuel;
            if (fuel < 0){fuel = 0;}
        }

        stack.set(CSpringsDataComponents.CHAMBER_FUEL, fuel);
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
        float stored = getAllStoredSum(getAllStored(stack));

        if(getSprings(stack) < 1){return super.use(level, player, hand);}
        if(getFuel(stack) < 1){return super.use(level, player, hand);}

        float fuel = getFuel(stack);
        stored += getFuel(stack);

        if(SpringPoweredCore.removeOne(stack)){
            stack.set(CSpringsDataComponents.CHAMBER_FUEL, fuel);
        } else {
            stack.set(CSpringsDataComponents.CHAMBER_FUEL, 0f);
        }

        if(stored > ModConfigs.common().SPRING_CAPACITY.get()){stored = ModConfigs.common().SPRING_CAPACITY.get();}

        putAllStored(spreadSu(getAllStored(stack), stored), stack);

        level.playSound(null, player.getOnPos(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.NEUTRAL, 0.5F, 1.2F);


        ParticleHelper.explodeAtPlayer(player, level);

        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(level.getGameTime() % 10 == 0){
            if(entity instanceof Player player){
                PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(stack, level, player, slotId, isSelected, PunchcardExecutor.EXPLOSION_CHAMBER, this));
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public SpringPoweredCore getCore() {
        return core;
    }

    public static float getFuel(ItemStack stack){
        if(stack.has(CSpringsDataComponents.CHAMBER_FUEL)){
            return stack.get(CSpringsDataComponents.CHAMBER_FUEL);
        }
        return 0;
    }
}
