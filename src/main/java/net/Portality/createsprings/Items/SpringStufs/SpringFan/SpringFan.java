package net.Portality.createsprings.Items.SpringStufs.SpringFan;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.SpringStufs.ISpringPoweredTool;
import net.Portality.createsprings.Items.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.Items.SpringStufs.SpringSpeedSys;
import net.Portality.createsprings.particles.SimpleAirParticleData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.simibubi.create.content.kinetics.fan.AirCurrent.isPlayerCreativeFlying;

public class SpringFan extends Item implements CustomArmPoseItem, ISpringPoweredTool {

    private final SpringPoweredCore core;
    private final SpringSpeedSys SpeedSys;
    private final int SPRINGS = 2;

    public SpringFan(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
        SpeedSys = new SpringSpeedSys();
        Item[] allowedModifficators = new Item[]{
                ModItems.PUNCHCARD.get(),
                Items.TRIPWIRE_HOOK,
        };

        this.core = new SpringPoweredCore(SPRINGS, allowedModifficators);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return SpeedSys.use(level, player, hand);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        SpeedSys.onInventoryTick(stack, level, player, slotIndex, selectedIndex);


        if(slotIndex != selectedIndex){
            if(slotIndex != 40){ // 40 - index for off hand in inventory
                return;
            }
        }

        final Vec3 vec = player.getViewVector(1);
        double distance = 10;
        double coef = SpringSpeedSys.getSpeedCoef(stack) * 10;
        double launchVelocity = -0.15f * coef;
        if(player.isShiftKeyDown()){launchVelocity *= -1;}
        // Находим сущность в направлении взгляда

        level.addParticle(new AirFlowParticleData(player.getOnPos()), 1, 0, 0, 0, 0, 0);

        ArrayList<EntityHitResult> entityHit = getEntityLookAtInLine(player, distance);

        if (!entityHit.isEmpty()) {
            for(EntityHitResult Hit : entityHit ){
                Entity target = Hit.getEntity();
                double distToSource = Hit.distanceTo(player);

                Vec3 previousMotion = target.getDeltaMovement();
                Vec3 motion = new Vec3(vec.x * launchVelocity, vec.y * launchVelocity / 3, vec.z * launchVelocity);

                if(distToSource > distance / 2){
                    motion.scale((distance - distToSource) / (distance / 2));
                }

                target.setDeltaMovement(previousMotion.add(motion));
            }
        }

        Vec3 playerMotion = new Vec3(vec.x * launchVelocity, vec.y * launchVelocity / 3, vec.z * launchVelocity);

        if(level.isClientSide){
            if(level.getGameTime() % Math.floor(21 - coef * 20) == 0){
                Vec3 start = player.getEyePosition(1.0F);
                start = start.add(playerMotion.scale(-8 * 7 * coef));

                Vec3 particleMotion = playerMotion.scale(7 * coef);
                level.addParticle(new SimpleAirParticleData(), start.x, start.y, start.z, particleMotion.x, particleMotion.y, particleMotion.z);
            }
        }

        if(isPlayerCreativeFlying(player)){return;}
        playerMotion = calibratePushOnElytra(playerMotion, player);
        playerMotion = calibratePushInWater(playerMotion, player);
        player.setDeltaMovement(player.getDeltaMovement().add(playerMotion.scale(-0.25)));
    }

    private Vec3 calibratePushInWater(Vec3 motion, Player player){
        if(!player.isInWater()){return motion;}
        if(player.getDeltaMovement().length() > 1f){return motion.scale(0);}
        return motion.scale(1f);
    }

    private Vec3 calibratePushOnElytra(Vec3 motion, Player player){
        if(!isPlayerFlyingWithIntactElytra(player)){return motion;}
        if(player.getDeltaMovement().length() > 0.5f){return motion.scale(0);}
        return motion.scale(0.5f);
    }

    public boolean isPlayerFlyingWithIntactElytra(Player player) {
        if (player.isFallFlying()) {
            ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
            return chestplate.getItem() == Items.ELYTRA && chestplate.getDamageValue() < chestplate.getMaxDamage() - 1;
        }
        return false;
    }

    public static ArrayList<EntityHitResult> getEntityLookAtInLine(Player player, double maxDistance) {
        Level level = player.level();
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(maxDistance));
        ArrayList<EntityHitResult> output = new ArrayList<EntityHitResult>();

        // Создаем AABB, который охватывает весь путь луча, и немного расширяем его
        AABB area = new AABB(start, end).inflate(2.0D); // Расширяем на 2 блока во все стороны для надежности
        List<Entity> entitiesInArea = level.getEntities(player, area, e -> !e.isSpectator());

        for (Entity entity : entitiesInArea) {
            // Получаем hitbox сущности и дополнительно расширяем его, если нужно
            AABB entityBoundingBox = entity.getBoundingBox().inflate(0.5D); // Расширяем hitbox на 0.5 блока
            // Проверяем пересечение луча и hitbox сущности
            Optional<Vec3> clipResult = entityBoundingBox.clip(start, end);

            if (clipResult.isPresent()) {
                Vec3 hitLoc = clipResult.get();
                output.add(new EntityHitResult(entity, hitLoc));
            }
        }

        return output; // Может быть null, если ничего не найдено
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SpringFanRenderer()));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        core.checkAndAddModifier(stack, AllItems.PROPELLER.asItem());
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
        core.checkAndAddModifier(stack1, AllItems.PROPELLER.asItem());
        if (core.overrideOtherStackedOnMe(stack1, stack2, slot, action, player, access)){
            return true;
        }
        if (core.addStackedLogic(AllItems.PROPELLER.get(), stack1, stack2, action, player)){
            core.switchTagInHand(player, slot, ModItems.SPRING_BASE.get(), stack1);
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
        core.checkAndAddModifier(stack, AllItems.PROPELLER.asItem());
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
