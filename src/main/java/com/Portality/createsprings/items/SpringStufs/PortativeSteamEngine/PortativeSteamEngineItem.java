package com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.client.CSpringsKeybindings;
import com.Portality.createsprings.client.menus.PortativeEngine.PortativeSteamEngineMenu;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.datagen.advancement.CSpringsAdvancements;
import com.Portality.createsprings.entities.damage.CSpringsDamageSources;
import com.Portality.createsprings.items.CSpringsArmorMaterials;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.ISpringPoweredTool;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedSys;
import com.Portality.createsprings.items.advanced.Punchcard.ExecutorInfo;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardExecutor;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator;
import com.Portality.createsprings.items.advanced.Spring.SpringItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.PSEHeatEvent;
import com.Portality.createsprings.utill.Helpers.ParticleHelper;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;


public class PortativeSteamEngineItem extends BaseArmorItem implements MenuProvider, ISpringPoweredTool {
    public static final EquipmentSlot SLOT = EquipmentSlot.CHEST;

    private final SpringPoweredCore core;
    private final int SPRINGS = 2;

    public PortativeSteamEngineItem(Properties properties) {
        super(CSpringsArmorMaterials.GEAR, Type.CHESTPLATE, properties, Create.asResource("copper_diving"));

        Supplier<Item>[] allowedModifficators = new Supplier[]{
                () -> CSpringsItems.PUNCHCARD.get()
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

    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        return context.getPlayer() == null ? InteractionResult.PASS : this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            openScreen(serverPlayer, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    public void openScreen(ServerPlayer serverPlayer, ItemStack stack){
        serverPlayer.openMenu(this, buf -> {
            ItemStack.STREAM_CODEC.encode(buf, stack);
        });
    }


    @OnlyIn(Dist.CLIENT)
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return PortativeSteamEngineMenu.create(id, inv, heldItem);
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        super.overrideStackedOnOther(stack, slot, action, player);
        if(core.overrideStackedOnOther(stack, slot, action, player)){
            return true;
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess access) {
        int boosted = getOverdriveProgress(stack1);
        if(boosted < 99){
            super.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access);
        }

        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)) {
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        ItemStack lastFuel = getBurnStack(stack);

        int water = (int) (getWater(stack) / 1000f * 20f);
        int fuel = (int) (getFuel(stack) / 1000f * 20f);
        if(water > 20 || fuel > 20){return;}
        int remainingwater = 20 - water;
        int remainingfuel = 20 - fuel;
        if(water < 0 || fuel < 0){return;}
        tooltip.add(Component.literal("|".repeat(water)).withStyle(ChatFormatting.BLUE)
                .append(Component.literal("|".repeat(remainingwater)).withStyle(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("|".repeat(fuel)).withStyle(ChatFormatting.RED)
                .append(Component.literal("|".repeat(remainingfuel)).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" + " + lastFuel.getCount()).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return core.getTooltipImage(p_150902_);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if(!(entity instanceof Player player)) return;
        if(!(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem)){return;}
        onArmor(stack, level, player);
    }

    public void onArmor(ItemStack stack, Level level, Player player) {
        boolean boost = getOverdrive(stack);
        int boosted = getOverdriveProgress(stack);

        handleDash(stack, boosted, player, level);

        if(level.getGameTime() % 5 != 0){
            return;
        }

        if(level.getGameTime() % 10 == 0){
            PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(stack, level, player, PunchcardExecutor.PSE, CSpringsItems.PORTATIVE_STEAM_ENGINE.get()));
        }

        int speed = getSpeed(stack);
        if(boost){speed = 150;}

        int fuel = getFuel(stack);
        int water = getWater(stack);

        int mode = getMode(stack);

        if(boost && mode == 0){
            stack.set(CSpringsDataComponents.OVERDRIVE, false);
        }

        if(mode > 0){
            if(fuel <= 0){
                ItemStack lastFuel = getBurnStack(stack);

                lastFuel.shrink(1);
                setBurnStack(stack, lastFuel);
                fuel += lastFuel.getBurnTime(null);
                stack.set(CSpringsDataComponents.PSE_FUEL, fuel);

                if(player.containerMenu instanceof PortativeSteamEngineMenu menu){
                    menu.shrink();
                }
            }
            stack.set(CSpringsDataComponents.ENGINE_SPEED, getMode(stack));
        }

        if(fuel >= mode / 15f * ModConfigs.common().PSE_FUEL_USAGE.get()){
            fuel -= (int) (mode / 15f * ModConfigs.common().PSE_FUEL_USAGE.get());
        } else if (fuel == 0){
            stack.set(CSpringsDataComponents.ENGINE_SPEED, 0);
            stack.set(CSpringsDataComponents.OVERDRIVE, false);
        } else {
            fuel = 0;
        }

        if(speed != 0){
            if(level.getGameTime() % (200 / (speed / 15)) == 0){
                if(boost){
                    if(level.isClientSide()){
                        spawnParticles(level, player);
                        spawnParticles(level, player);
                    }
                }
                AllSoundEvents.STEAM.playOnServer(level, BlockPos.containing(player.position()).above(), 0.1f, 1f);
                if(level.isClientSide()){
                    spawnParticles(level, player);
                }
            }

            water -= 5;
            if(water <= 1){
                if(ParseInv(player)){
                    water = 1000;
                }
            }
        }

        stack.set(CSpringsDataComponents.PSE_FUEL, fuel);
        stack.set(CSpringsDataComponents.PSE_WATER, water);

        if(boosted > 0){
            if(boost){
                if(boosted > 100){
                    if(level.isClientSide()){
                        spawnParticles(level, player);
                    }
                    AllSoundEvents.SCHEMATICANNON_LAUNCH_BLOCK.playOnServer(level, BlockPos.containing(player.position()).above(), 0.5f, 1f);
                    spawnItems(level, player, AllItems.COPPER_NUGGET.asItem());
                }
                stack.set(CSpringsDataComponents.OVERDRIVE_PROGRESS, boosted+1);
            } else {
                stack.set(CSpringsDataComponents.OVERDRIVE_PROGRESS, boosted-1);
            }

            if(20 - (int) (boosted / 100f * 20) < 0){
                if(level.getGameTime() % 10 > 5){
                    player.displayClientMessage(Component.literal("|".repeat(20)).withStyle(ChatFormatting.RED), true);
                } else {
                    player.displayClientMessage(Component.literal("|".repeat(20)).withStyle(ChatFormatting.DARK_RED), true);
                }
                return;
            }

            displayAboveHotbar(stack, player, boosted, boost);
        }

        int actual = getSpeed(stack) / 15;

        if(boost){
            chargeTanks(stack, level, player, actual); speedUp(stack, level, player, actual);
            charge(stack, level, player, actual);
            chargeTanks(stack, level, player, actual);
            heating(stack, level, player, actual);
        }

        if(mode > 0){
            heating(stack, level, player, actual);
            charge(stack, level, player, actual);
        }

        switch (mode / 15){
            case 4: chargeTanks(stack, level, player, actual);
            case 5: chargeTanks(stack, level, player, actual); speedUp(stack, level, player, actual);
            case 6: chargeTanks(stack, level, player, actual); speedUp(stack, level, player, actual);
        }
    }

    public void handleDash(ItemStack stack, int boosted, Player player, Level level){
        if(boosted > 110){
            player.addDeltaMovement(new Vec3(0, 0.5f, 0));
            if(level.getGameTime() % 2 == 0){
                AllSoundEvents.STEAM.playOnServer(level, BlockPos.containing(player.position()).above(), 0.1f, 1f);
            }
            player.setDeltaMovement(0, 0.5f, 0);
            Vec3 offset = new Vec3(0, -0.5f, 0);
            Vec3 v = offset.scale(0.5).add(player.position().add(0, 0, 0));
            Vec3 m = offset;
            level.addParticle(new SteamJetParticleData(1.0F), v.x, v.y, v.z, m.x, m.y, m.z);

            if(boosted > 115){

                DamageSource damageSource = CSpringsDamageSources.pse(level);
                player.hurt(damageSource, 6f + level.random.nextInt(-2, 2));
                level.playSound(null, BlockPos.containing(player.position()),
                        SoundEvents.GENERIC_EXPLODE.value(),
                        SoundSource.NEUTRAL, 1F, 1F);
                ParticleHelper.SpawnAtPlayer(player, ParticleTypes.EXPLOSION_EMITTER, level);

                player.setItemSlot(EquipmentSlot.CHEST, CSpringsItems.BROKEN_PSE.asStack());
                CSpringsAdvancements.EXPLOSION.awardTo(player);
                return;
            }
        }
        int dash = getDashTicks(stack);

        if(dash > 0){
            player.fallDistance = 0;
            if(dash > 39){
                stack.set(CSpringsDataComponents.OVERDRIVE_PROGRESS, boosted - 3);
                AllSoundEvents.STEAM.playOnServer(level, BlockPos.containing(player.position()).above(), 0.1f, 1f);
                if (level.isClientSide()) {
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                }
            }
            stack.set(CSpringsDataComponents.DASH_TICKS, dash - 1);
        }
    }

    public void displayAboveHotbar(ItemStack stack, Player player, int boosted, boolean isBoosted){

        if(!player.level().isClientSide()){return;}
        if (FMLEnvironment.dist.isClient()) {
            MutableComponent boost, boostComponent, indicator, left, desc, SD;
            boostComponent = Component.literal("|".repeat((int) (boosted / 100f * 20))).withStyle(ChatFormatting.RED);

            boost = Component.translatable(CreateSprings.MODID + ".pse.boost");
            desc = Component.translatable(CreateSprings.MODID + ".pse.dash");
            Component K = CSpringsKeybindings.INSTANCE.PSEDashKey.getKey().getDisplayName();

            if(boosted >= 70){
                indicator = Component.literal("|").withStyle(ChatFormatting.DARK_RED);
                SD = Component.literal(K.getString()).withStyle(ChatFormatting.DARK_RED);
            } else {
                indicator = Component.literal("|").withStyle(ChatFormatting.DARK_GRAY);
                SD = Component.literal(K.getString()).withStyle(ChatFormatting.DARK_GRAY);
            }

            SD = Component.literal("[").withStyle(ChatFormatting.GRAY).append(SD).append(Component.literal("]").withStyle(ChatFormatting.GRAY));
            left = Component.literal("|".repeat(20 - (int) (boosted / 100f * 20))).withStyle(ChatFormatting.GRAY);

            if(boosted <= 100){
                if(isBoosted){
                    player.displayClientMessage(boost.append(boostComponent).append(indicator).append(left)
                            .append(Component.literal(" ").append(desc).append(SD)), true);
                } else {
                    player.displayClientMessage(boostComponent.append(indicator).append(left), true);
                }
                return;
            }
            player.displayClientMessage(Component.literal("|".repeat(20)).withStyle(ChatFormatting.RED), true);
        }
    }

    private void heating(ItemStack stack, Level level, Player player, int mode) {
        if(player.getTicksFrozen() != 0){
            player.setTicksFrozen(0);
        }

        NeoForge.EVENT_BUS.post(new PSEHeatEvent(player, mode));
    }

    private void chargeTanks(ItemStack stack, Level level, Player player, int mode) {

        for (ItemStack item : player.getInventory().items) {
            if(item.getItem() == AllItems.COPPER_BACKTANK.get() || item.getItem() == AllItems.NETHERITE_BACKTANK.get()){
                if(item.getOrDefault(AllDataComponents.BACKTANK_AIR, 0) <= 950){
                    item.set(AllDataComponents.BACKTANK_AIR, (int) (item.getOrDefault(AllDataComponents.BACKTANK_AIR, 0) + 2f / 6f * mode));
                }
            }
        }
    }

    private void speedUp(ItemStack stack, Level level, Player player, int mode){
        int speed = getSpeed(stack);
        if(speed == 0){return;}

        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);

        float toolSpeed = (float) SpringSpeedSys.getSpeed(stack);

        if(handStack.getItem() instanceof ISpringPoweredTool){
            if(toolSpeed < SpringSpeedSys.MAX_REGULAR_SPEED){
                handStack.set(CSpringsDataComponents.TOOL_LAST_SPEED, toolSpeed);
                toolSpeed += mode * 15 / 2f;
                if(toolSpeed > SpringSpeedSys.MAX_REGULAR_SPEED) toolSpeed = SpringSpeedSys.MAX_REGULAR_SPEED;
                handStack.set(CSpringsDataComponents.TOOL_SPEED, toolSpeed);
            }
        }
    }

    private void charge(ItemStack stack, Level level, Player player, int mode){

        float Stored = SpringPoweredCore.getStoredSum(stack);
        int speed = getSpeed(stack);
        int springs = SpringPoweredCore.getSprings(stack);

        if(speed == 0){return;}

        Stored += speed * 3 * 2;

        SpringPoweredCore.putAllStored(SpringPoweredCore.spreadSu(SpringPoweredCore.getAllStored(stack), Stored), stack);

        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(handStack.getItem() instanceof SpringItem){
            float charge = SpringItem.getStoredSu(handStack);
            SpringItem.SetSu(handStack, charge + speed * 2);
        }

        if(handStack.getItem() instanceof ISpringPoweredTool){
            if(SpringPoweredCore.getSprings(handStack) > 0){
                float[] charge = SpringPoweredCore.getAllStored(handStack);
                charge = SpringPoweredCore.spreadSu(charge, SpringPoweredCore.getAllStoredSum(charge) + speed * 2);
                SpringPoweredCore.putAllStored(charge, handStack);
            }
        }
    }

    public static void steamDash(Player player, Level level){
        if(level.isClientSide()){return;}

        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(player);
        if(item != null){
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            int boosted = getOverdriveProgress(stack);

            if(boosted > 70){

                if(boosted > 100){return;}

                Vec3 view = player.getViewVector(0.5f);
                player.setDeltaMovement(new Vec3(view.x * 3, view.y / 2 * 3, view.z * 3));
                player.hurtMarked = true;
                player.setRemainingFireTicks(15);

                if(getDashTicks(stack) > 0){
                    CSpringsAdvancements.DASH.awardTo(player);
                }

                stack.set(CSpringsDataComponents.DASH_TICKS, 60);
                level.playSound(null, player.getOnPos().above(2),
                        SoundEvents.FIREWORK_ROCKET_LAUNCH,
                        SoundSource.NEUTRAL, 0.5F, 1.2F);
            }
        }
    }

    public static void spawnItems(Level level, LivingEntity entity, Item items){
        Random random = new Random();
        Vec3 eyePosition = entity.getEyePosition();

        ItemStack stack = new ItemStack(items);
        ItemEntity item = new ItemEntity(level, eyePosition.x, eyePosition.y, eyePosition.z, stack);

        double speed = 0.15 * 2;

        double motionX = (random.nextDouble() - 0.5) * 2 * speed;
        double motionY = random.nextDouble() * speed + 0.1;
        double motionZ = (random.nextDouble() - 0.5) * 2 * speed;

        item.setDeltaMovement(motionX, motionY, motionZ);
        item.setPickUpDelay(20);
        item.lifespan = 60 * 20 * 5;

        level.addFreshEntity(item);
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnParticles(Level level, Player player) {
        Direction facing = player.getDirection().getOpposite();
        Vec3 offset = VecHelper.rotate((new Vec3(0.0, 0.0, 1.0)).add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1.0F).multiply(1.0, 1.0, 0.0).normalize().scale(0.5)), (double)AngleHelper.verticalAngle(facing), Direction.Axis.X);
        offset = VecHelper.rotate(offset, (double)AngleHelper.horizontalAngle(facing), Direction.Axis.Y);
        Vec3 v = offset.scale(0.5).add(player.position().add(0, 1.5f, 0));
        Vec3 m = offset.subtract(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.75));
        level.addParticle(new SteamJetParticleData(1.0F), v.x, v.y, v.z, m.x, m.y, m.z);
    }

    private boolean ParseInv(Player player){
        for (ItemStack item : player.getInventory().items) {
            if(item.getItem() == Items.WATER_BUCKET){
                return true;
            }
        }

        return false;
    }

    @Override
    public SpringPoweredCore getCore() {
        return core;
    }

    public static int getOverdriveProgress(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.OVERDRIVE_PROGRESS, 0);}
    public static int getFuel(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.PSE_FUEL, 0);}
    public static int getWater(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.PSE_WATER, 0);}
    public static int getDashTicks(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.DASH_TICKS, 0);}
    public static int getSpeed(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.ENGINE_SPEED, 0);}
    public static int getTargetSpeed(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.TARGET_SPEED, 0);}
    public static int getMode(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.ENGINE_MODE, 0);}
    public static boolean getOverdrive(ItemStack stack){return stack.getOrDefault(CSpringsDataComponents.OVERDRIVE, false);}


    public static ItemStack getBurnStack(ItemStack stack) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        if (contents.equals(ItemContainerContents.EMPTY) || contents.getSlots() == 0) {
            return ItemStack.EMPTY;
        }

        return contents.copyOne();
    }

    public static void setBurnStack(ItemStack stack, ItemStack burnStack) {
        if (burnStack.isEmpty()) {
            stack.remove(DataComponents.CONTAINER);
        } else {
            ItemContainerContents contents = ItemContainerContents.fromItems(List.of(burnStack));
            stack.set(DataComponents.CONTAINER, contents);
        }
    }

    public static void removeBurnStack(ItemStack stack){
        stack.remove(DataComponents.CONTAINER);
    }
}
