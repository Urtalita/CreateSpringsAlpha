package net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine;

import com.simibubi.create.*;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.damage.CSpringsDamageSources;
import net.Portality.createsprings.Items.CspringsArmorMaterials;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Punchcard.ExecutorInfo;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardExecutor;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardInterpritator;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.Items.advanced.SpringStufs.ISpringPoweredTool;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSpeedSys;
import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.datagen.CSpringsAdvancements;
import net.Portality.createsprings.menus.PortativeEngine.PortativeSteamEngineMenu;
import net.Portality.createsprings.utill.Helpers.ParticleHelper;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;


public class PortativeSteamEngineItem extends ArmorItem implements MenuProvider, ISpringPoweredTool {
    public static final EquipmentSlot SLOT = EquipmentSlot.CHEST;

    private final SpringPoweredCore core;
    private final int SPRINGS = 2;

    public PortativeSteamEngineItem(Properties properties) {
        super(CspringsArmorMaterials.HAT, Type.CHESTPLATE, properties);

        Item[] allowedModifficators = new Item[]{
            ModItems.PUNCHCARD.get()
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
        if(player.isShiftKeyDown()){
            if (!level.isClientSide && player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                openScreen(serverPlayer, stack);
            }
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }
        return super.use(level, player, hand);
    }

    public void openScreen(ServerPlayer serverPlayer, ItemStack stack){
        NetworkHooks.openScreen(serverPlayer, this, (buf) -> {
            buf.writeItem(stack);
        });
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return PortativeSteamEngineMenu.create(id, inv, heldItem);
    }

    @Override
    public Component getDisplayName() {
        return this.getDescription();
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
        int boosted = stack1.getOrCreateTag().getInt("boosted");
        if(boosted < 99){
            super.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access);
        }

        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)) {
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        ItemStack lastFuel = ItemStack.of(stack.getOrCreateTag().getCompound("burnStack"));

        int water = (int) (tag.getInt("water") / 1000f * 20f);
        int fuel = (int) (tag.getInt("fuel") / 1000f * 20f);
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
    public Optional<TooltipComponent> getTooltipImage(ItemStack p_150902_) {
        return core.getTooltipImage(p_150902_);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if(!(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem)){return;}
        onArmor(stack, level, player);
    }

    public void onArmor(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        boolean boost = tag.getBoolean("boost");

        if(boost) {
            int boosted = tag.getInt("boosted");

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
                            SoundEvents.GENERIC_EXPLODE,
                            SoundSource.NEUTRAL, 1F, 1F);
                    ParticleHelper.SpawnAtPlayer(player, ParticleTypes.EXPLOSION_EMITTER, level);

                    player.setItemSlot(EquipmentSlot.CHEST, ModItems.BROKEN_PSE.asStack());
                    CSpringsAdvancements.EXPLOSION.awardTo(player);
                    return;
                }
            }
            int dash = tag.getInt("DashTicks");

            if(dash > 0){
                player.fallDistance = 0;
                if(dash > 39){
                    tag.putInt("boosted", boosted - 3);
                    AllSoundEvents.STEAM.playOnServer(level, BlockPos.containing(player.position()).above(), 0.1f, 1f);
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                }
                tag.putInt("DashTicks", dash - 1);
            }
        }

        if(level.getGameTime() % 5 != 0){
            return;
        }

        if(level.getGameTime() % 10 == 0){
            PunchcardInterpritator.DoPunchcardLogic(new ExecutorInfo(stack, level, player, PunchcardExecutor.PSE, ModItems.PORTATIVE_STEAM_ENGINE.get()));
        }

        int speed = tag.getInt("engineSpeed");
        if(boost){speed = 150;}

        int fuel = tag.getInt("fuel");
        int water = tag.getInt("water");

        int mode = (int) tag.getFloat("mode");

        if(boost && mode == 0){
            tag.putBoolean("boost", false);
        }

        if(mode > 0){
            if(fuel <= 0){
                ItemStack lastFuel = ItemStack.of(stack.getOrCreateTag().getCompound("burnStack"));

                lastFuel.shrink(1);
                stack.getOrCreateTag().put("burnStack", lastFuel.serializeNBT());
                fuel += ForgeHooks.getBurnTime(lastFuel, null);
                tag.putInt("fuel", fuel);

                if(player.containerMenu instanceof PortativeSteamEngineMenu menu){
                    menu.shrink();
                }
            }
            tag.putFloat("engineSpeed", tag.getFloat("mode"));
        }

        if(fuel >= mode / 15f * ModConfigs.common().PSE_FUEL_USAGE.get()){
            fuel -= (int) (mode / 15f * ModConfigs.common().PSE_FUEL_USAGE.get());
        } else if (fuel == 0){
            tag.putFloat("engineSpeed", 0);
            tag.putBoolean("boost", false);
        } else {
            fuel = 0;
        }

        if(speed != 0){
            if(level.getGameTime() % (200 / (speed / 15)) == 0){
                if(boost){
                    spawnParticles(level, player);
                    spawnParticles(level, player);
                }
                AllSoundEvents.STEAM.playOnServer(level, BlockPos.containing(player.position()).above(), 0.1f, 1f);
                spawnParticles(level, player);
            }

            water -= 5;
            if(water <= 1){
                if(ParseInv(player)){
                    water = 1000;
                }
            }
        }

        tag.putInt("fuel", fuel);
        tag.putInt("water", water);
        int boosted = tag.getInt("boosted");

        if(boosted > 0){
            if(boost){
                if(boosted > 100){
                    spawnParticles(level, player);
                    AllSoundEvents.SCHEMATICANNON_LAUNCH_BLOCK.playOnServer(level, BlockPos.containing(player.position()).above(), 0.5f, 1f);
                    spawnItems(level, player, AllItems.COPPER_NUGGET.asItem());
                }
                tag.putInt("boosted", boosted + 1);
            } else {
                tag.putInt("boosted", boosted - 1);
            }

            if(20 - (int) (boosted / 100f * 20) < 0){
                if(level.getGameTime() % 10 > 5){
                    player.displayClientMessage(Component.literal("|".repeat(20)).withStyle(ChatFormatting.RED), true);
                } else {
                    player.displayClientMessage(Component.literal("|".repeat(20)).withStyle(ChatFormatting.DARK_RED), true);
                }
                return;
            }

            MutableComponent boostComponent, indicator, left;
            boostComponent = Component.literal("|".repeat((int) (boosted / 100f * 20))).withStyle(ChatFormatting.RED);
            if(boosted >= 70){
                indicator = Component.literal("|").withStyle(ChatFormatting.DARK_RED);
            } else {
                indicator = Component.literal("|").withStyle(ChatFormatting.DARK_GRAY);
            }
            left = Component.literal("|".repeat(20 - (int) (boosted / 100f * 20))).withStyle(ChatFormatting.GRAY);

            if(boosted <= 100 && level.isClientSide()){
                player.displayClientMessage(boostComponent.append(indicator).append(left), true);
            } else if(level.isClientSide()){
                player.displayClientMessage(Component.literal("|".repeat(20)).withStyle(ChatFormatting.RED), true);
            }
        }

        int actual = mode / 15;

        if(boost){
            chargeTanks(stack, level, player, actual); speedUp(stack, level, player, actual);
            charge(stack, level, player, actual);
            chargeTanks(stack, level, player, actual);
        }

        if(mode > 0){
            charge(stack, level, player, actual);
        }

        switch (mode / 15){
            case 4: chargeTanks(stack, level, player, actual);
            case 5: chargeTanks(stack, level, player, actual); speedUp(stack, level, player, actual);
            case 6: chargeTanks(stack, level, player, actual); speedUp(stack, level, player, actual);
        }
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }


    private void chargeTanks(ItemStack stack, Level level, Player player, int mode) {
        for (ItemStack item : player.getInventory().items) {
            if(item.getItem() == AllItems.COPPER_BACKTANK.get() || item.getItem() == AllItems.NETHERITE_BACKTANK.get()){
                if(item.getOrCreateTag().getFloat("Air") <= 950){
                    item.getOrCreateTag().putFloat("Air",item.getOrCreateTag().getFloat("Air") + 2f / 6f * mode);
                }
            }
        }
    }

    private void speedUp(ItemStack stack, Level level, Player player, int mode){
        CompoundTag tag = stack.getOrCreateTag();

        int speed = tag.getInt("engineSpeed");
        if(speed == 0){return;}

        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);

        float toolSpeed = handStack.getOrCreateTag().getFloat("Speed");

        if(handStack.getItem() instanceof ISpringPoweredTool){
            if(toolSpeed < SpringSpeedSys.MAX_REGULAR_SPEED){
                handStack.getOrCreateTag().putFloat("LastSpeed", toolSpeed);
                toolSpeed += mode * 15 / 2f;
                if(toolSpeed > SpringSpeedSys.MAX_REGULAR_SPEED) toolSpeed = SpringSpeedSys.MAX_REGULAR_SPEED;
                handStack.getOrCreateTag().putFloat("Speed", toolSpeed);
            }
        }
    }

    private void charge(ItemStack stack, Level level, Player player, int mode){
        CompoundTag tag = stack.getOrCreateTag();
        float Stored0 = tag.getFloat("Stored0");
        float Stored1 = tag.getFloat("Stored1");
        int speed = tag.getInt("engineSpeed");
        int springs = tag.getInt("Springs_rn");

        if(speed == 0){return;}

        Stored0 += speed * 3;
        Stored1 += speed * 3;
        if(Stored0 > ModConfigs.common().SPRING_CAPACITY.get()){
            Stored0 = ModConfigs.common().SPRING_CAPACITY.get();
        }
        if(Stored1 > ModConfigs.common().SPRING_CAPACITY.get()){
            Stored1 = ModConfigs.common().SPRING_CAPACITY.get();
        }

        if(springs > 0){
            tag.putFloat("Stored0", Stored0);
            if(springs == 2){
                tag.putFloat("Stored1", Stored1);
            }
        }

        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(handStack.getItem() instanceof SpringItem){
            float charge = SpringItem.getStoredSu(handStack);
            SpringItem.SetSu(handStack, charge + speed * 2);
        }

        for(Item tool : CreateSprings.SPRING_TOOLS){
            if(tool == handStack.getItem()){
                if(handStack.getOrCreateTag().getInt("Springs_rn") > 0){
                    float[] charge = SpringPoweredCore.getAllStored(handStack.getOrCreateTag());
                    charge[0] += speed;
                    charge[1] += speed;
                    SpringPoweredCore.putAllStored(charge, handStack.getOrCreateTag());
                }
            }
        }
    }

    public static void steamDash(Player player, Level level){
        if(level.isClientSide()){return;}

        PortativeSteamEngineItem item = PortativeSteamEngineItem.getWornBy(Minecraft.getInstance().player);
        if(item != null){
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            CompoundTag tag = stack.getOrCreateTag();
            int boosted = tag.getInt("boosted");

            if(boosted > 70){

                if(boosted > 100){return;}

                Vec3 view = player.getViewVector(0.5f);
                player.setDeltaMovement(new Vec3(view.x * 3, view.y / 2 * 3, view.z * 3));
                player.hurtMarked = true;
                player.setSecondsOnFire(1);

                if(tag.getInt("DashTicks") > 0){
                    CSpringsAdvancements.DASH.awardTo(player);
                }

                tag.putInt("DashTicks", 60);
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
    /*

    private void startEngine(CompoundTag tag){
        int fuel = tag.getInt("fuel");
        int water = tag.getInt("water");

        if(fuel == 0 || water == 0){
            tag.putFloat("engineSpeed", 0);
            return;
        }

        tag.putInt("fuel", 500);
        tag.putInt("water", 500);
        tag.putFloat("engineSpeed", tag.getFloat("targetSpeed"));
    }

     */
}
