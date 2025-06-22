package net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.foundation.item.LayeredArmorItem;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.CspringsArmorMaterials;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Punchcard.ExecutorInfo;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardExecutor;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardInterpritator;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSpeedSys;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;


public class PortativeSteamEngineItem extends ArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.CHEST;

    private final SpringPoweredCore core;
    private final int SPRINGS = 2;

    public PortativeSteamEngineItem(Properties properties) {
        super(CspringsArmorMaterials.HAT, Type.CHESTPLATE, properties);

        Item[] allowedModifficators = new Item[]{

        };

        this.core = new SpringPoweredCore(SPRINGS, allowedModifficators);
    }

    @Nullable
    public static PortativeSteamEngineItem getWornBy(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }
        if (!(livingEntity.getItemBySlot(SLOT).getItem() instanceof PortativeSteamEngineItem item)) {
            return null;
        }
        return item;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if(core.overrideStackedOnOther(stack, slot, action, player)){
            return true;
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)) {
            return true;
        }

        if(AllTags.AllItemTags.BLAZE_BURNER_FUEL_REGULAR.matches(stack2.getItem())){
            int burnTime = ForgeHooks.getBurnTime(stack2, null);

            CompoundTag tag = stack1.getOrCreateTag();
            int fuel = tag.getInt("fuel");
            if(fuel + burnTime < 1000){
                stack2.setCount(0);
                tag.putInt("fuel", fuel + burnTime);
                return true;
            }
        }

        if(core.addStackedLogick(ModItems.PUNCHCARD.get() ,stack1, stack2, action, player)){
            CompoundTag tag = stack1.getOrCreateTag();
            int fuel = tag.getInt("fuel");
            int water = tag.getInt("water");
            tag.putInt("fuel", fuel + 200);
            tag.putInt("water", water + 200);
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        core.appendHoverText(stack, level, tooltip, flag);
        CompoundTag tag = stack.getOrCreateTag();

        int water = (int) (tag.getInt("water") / 1000f * 20f);
        int fuel = (int) (tag.getInt("fuel") / 1000f * 20f);
        if(water > 20 || fuel > 20){return;}
        int remainingwater = 20 - water;
        int remainingfuel = 20 - fuel;
        tooltip.add(Component.literal("|".repeat(water)).withStyle(ChatFormatting.BLUE).append(Component.literal("|".repeat(remainingwater)).withStyle(ChatFormatting.GRAY)));
        tooltip.add(Component.literal("|".repeat(fuel)).withStyle(ChatFormatting.RED).append(Component.literal("|".repeat(remainingfuel)).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return core.getTooltipImage(p_150902_);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);

        if(level.getGameTime() % 5 != 0){
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        float Stored0 = tag.getFloat("Stored0");
        float Stored1 = tag.getFloat("Stored1");
        int speed = tag.getInt("engineSpeed");
        int springs = tag.getInt("Springs_rn");

        startEngine(tag);
        if(speed == 0){return;}

        Stored0 += speed;
        Stored1 += speed;

        if(springs > 0){
            tag.putFloat("Stored0", Stored0);
            if(springs == 2){
                tag.putFloat("Stored1", Stored1);
            }
        }
    }

    private void startEngine(CompoundTag tag){
        int fuel = tag.getInt("fuel");
        int water = tag.getInt("water");

        if(fuel == 0 || water == 0){
            tag.putFloat("engineSpeed", 0);
            return;
        }

        tag.putInt("fuel", fuel - 1);
        tag.putInt("water", water - 1);
        tag.putFloat("engineSpeed", 10);
    }
}
