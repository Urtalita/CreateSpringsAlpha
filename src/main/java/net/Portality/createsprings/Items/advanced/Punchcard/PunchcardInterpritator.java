package net.Portality.createsprings.Items.advanced.Punchcard;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.ExplosionСhamber.ChamberItem;
import net.Portality.createsprings.Items.advanced.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.SpringLauncher;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSpeedSys;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.server.*;
import net.Portality.createsprings.server.packets.AirDashPlayerPacket;
import net.Portality.createsprings.server.packets.GrabPacket;
import net.Portality.createsprings.server.packets.PushOffPacket;
import net.Portality.createsprings.server.packets.RotatePlayerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.function.Function;

import static net.Portality.createsprings.Items.advanced.Spring.SpringItem.getStoredSu;
import static net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore.*;

public class PunchcardInterpritator {
    public static HashMap<String, Function<ExecutorInfo, Void>> allPunchcardActions = new HashMap<>();

    public static void registerActions(){
        for(PunchcardFunction function : PunchcardFunction.values()){
            allPunchcardActions.put(function.getFunctionName(), function.getFunc());
        }
    }

    public static void DoPunchcardLogic(ExecutorInfo info){
        CompoundTag tag = info.getTag();
        if(!checkItemInContains(tag, ModItems.PUNCHCARD.get())){return;}
        CompoundTag punchcard = tag.getCompound("punchcard");

        int curAction = punchcard.getInt("curAction");
        String actionKey = PunchcardAction.getAllFromString(punchcard.getString(String.valueOf(curAction))).getName();

        Function<ExecutorInfo, Void> action = allPunchcardActions.get(actionKey);
        if(action == null){return;}

        action.apply(info);
    }

    public static String getParam(CompoundTag tag){
        CompoundTag punchcard = tag.getCompound("punchcard");
        int curAction = punchcard.getInt("curAction");
        return PunchcardAction.getAllFromString(punchcard.getString(String.valueOf(curAction))).getParameter();
    }

    public static void setParam(String param, CompoundTag tag){
        CompoundTag punchcard = tag.getCompound("punchcard");
        int curAction = punchcard.getInt("curAction");
        PunchcardAction action = PunchcardAction.getAllFromString(punchcard.getString(String.valueOf(curAction)));
        action.parameter = param;

        punchcard.putString(String.valueOf(curAction), PunchcardAction.putPunchcardActionInString(action));
    }

    public static Function<ExecutorInfo, Void> useSpringBase(){
        return (info) -> {
            CompoundTag tag = info.getTag();
            String param = getParam(tag);

            float stored = getAllStoredSum(getAllStored(tag));
            double speed = tag.getDouble("Speed");

            if (stored > SpringSpeedSys.MAX_REGULAR_SPEED && speed < 5500){
                speed += 250;
                stored -= 2000;
                if(speed > SpringSpeedSys.MAX_REGULAR_SPEED) speed = SpringSpeedSys.MAX_REGULAR_SPEED;
            }

            float[] allsu = getAllStored(tag);
            putAllStored(spreadSu(allsu, stored), tag);
            tag.putDouble("Speed", speed);

            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitForSpeedIncreased(){
        return (info) -> {
            CompoundTag tag = info.getTag();
            float param = Integer.parseInt(getParam(tag));
            float speed = (float) tag.getDouble("Speed");

            if(param >= speed){
                info.nextAction();
            }

            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitForSpeedDecreased(){
        return (info) -> {
            CompoundTag tag = info.getTag();
            float param = Integer.parseInt(getParam(tag));
            float speed = (float) tag.getDouble("Speed");

            if(param < speed){
                info.nextAction();
            }

            return null;
        };
    }

    public static Function<ExecutorInfo, Void> end() {
        return (info) -> {
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> detachSpring() {
        return (info) -> {
            CompoundTag tag = info.getTag();
            int Springs_rn = tag.getInt("Springs_rn");
            float[] allSu = getAllStored(tag);

            if (Springs_rn > 0){
                float springSu;

                springSu = allSu[Springs_rn-1];
                allSu[Springs_rn-1] = 0;

                info.getPlayer().getInventory().add(putSuInSpring(springSu));

                Springs_rn--;
                tag.putInt("Springs_rn", Springs_rn);
                putAllStored(allSu, tag);
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> sendMessage() {
        return (info) -> {
            if(info.getSelectedIndex() != info.getSlotIndex()){return null;}
            CompoundTag tag = info.getTag();

            info.getPlayer().displayClientMessage(Component.literal(getParam(tag)), true);
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitTicks() {
        return (info) -> {
            int param = Integer.parseInt(getParam(info.getTag()));
            if(param <= 10){
                info.nextAction();
                return null;
            }
            setParam(String.valueOf(param - 10), info.getTag());
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitForSlotSelected() {
        return (info) -> {
            if(info.getSlotIndex() == info.getSelectedIndex()){
                info.nextAction();
            }
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> shootFromSpringLauncher() {
        return (info) -> {
            if(info.getSlotIndex() != info.getSelectedIndex()){
                return null;
            }
            if(info.getItem() instanceof SpringLauncher launcher){
                launcher.releaseUsing(info.getStack(), info.getLevel(), info.getPlayer(), 0);
                info.nextAction();
            }
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> explodeChamber() {
        return (info) -> {
            if(info.getItem() instanceof ChamberItem chamberItem){
                chamberItem.use(info.getLevel(), info.getPlayer(), InteractionHand.MAIN_HAND);
                info.nextAction();
            }
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> toggleBoost(){
        return (info) -> {
            if(info.getPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
                ItemStack stack = info.getStack();
                int boosted = stack.getOrCreateTag().getInt("boosted");
                if(boosted < 99){
                    stack.getOrCreateTag().putBoolean("boost", !stack.getOrCreateTag().getBoolean("boost"));
                    if(boosted <= 0){
                        stack.getOrCreateTag().putInt("boosted", 1);
                    }
                }
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> steamDash(){
        return (info) -> {
            if(info.getPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
                PortativeSteamEngineItem.steamDash(info.getPlayer(), info.getLevel());
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> shootFromCannon(){
        return (info) -> {
            if(!info.getPlayer().getCooldowns().isOnCooldown(AllItems.POTATO_CANNON.asItem())){
                if (info.getSelectedIndex() == info.getSlotIndex()){
                    info.getItem().use(info.getLevel(), info.getPlayer(), InteractionHand.MAIN_HAND);
                }
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> tripleShot(){
        return (info) -> {
            if (info.getSelectedIndex() == info.getSlotIndex()){
                if(!info.getPlayer().getCooldowns().isOnCooldown(AllItems.POTATO_CANNON.asItem())){
                    for(int i = 0; i < 3; i++){
                        info.getItem().use(info.getLevel(), info.getPlayer(), InteractionHand.MAIN_HAND);
                        info.getPlayer().setXRot(info.getPlayer().getXRot() - 5);
                        ShootableGadgetItemMethods.applyCooldown(info.getPlayer(), info.getStack(), InteractionHand.MAIN_HAND, s -> s.getItem() instanceof PotatoCannonItem, 60);
                    }
                    if(info.getPlayer() instanceof ServerPlayer serverPlayer){
                        CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new RotatePlayerPacket(serverPlayer.getXRot()));
                    }
                }
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> empty(){
        return (info) -> {
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> airDash(){
        return (info) -> {
            if(info.getPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof BacktankItem){
                float air = info.getTag().getFloat("Air");
                if(air > 25){
                    if(info.getPlayer().getDeltaMovement().y < 2){
                        info.getPlayer().addDeltaMovement(new Vec3(0, 0.8f, 0));
                    }
                    if(info.getPlayer() instanceof ServerPlayer serverPlayer){
                        CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new AirDashPlayerPacket());
                        AllSoundEvents.STEAM.playOnServer(info.getPlayer().level(), BlockPos.containing(info.getPlayer().position()).above(), 0.8f, 1f);
                    }
                    air -= 25;
                    info.getTag().putFloat("Air", air);
                }
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> grab() {
        return (info) -> {
            if(!info.getPlayer().getCooldowns().isOnCooldown(AllItems.EXTENDO_GRIP.asItem())){
                if(AllItems.EXTENDO_GRIP.isIn(info.getPlayer().getItemInHand(InteractionHand.MAIN_HAND))){
                    if(info.getPlayer() instanceof ServerPlayer serverPlayer){
                        Vec3 newSpeed = getRaycastVector(info.getPlayer());
                        newSpeed = new Vec3(newSpeed.x, newSpeed.y / 3, newSpeed.z);
                        newSpeed = new Vec3(newSpeed.x % 3, newSpeed.y % 3, newSpeed.z % 3);
                        CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new GrabPacket(newSpeed));
                        serverPlayer.addDeltaMovement(newSpeed);
                        ShootableGadgetItemMethods.applyCooldown(info.getPlayer(), info.getStack(), InteractionHand.MAIN_HAND, s -> s.getItem() instanceof ExtendoGripItem, 10);
                    }
                }
            }
            info.nextAction();
            return null;
        };
    }

    public static Vec3 getRaycastVector(Player player) {
        // Получаем позицию глаз игрока
        Vec3 eyePosition = player.getEyePosition();

        // Получаем направление взгляда
        Vec3 lookVector = player.getViewVector(1.0F);

        // Вычисляем конечную точку рейкаста
        Vec3 endPoint = eyePosition.add(lookVector.x * 8, lookVector.y * 8, lookVector.z * 8);

        // Создаем контекст для рейкаста, игнорируя жидкости
        ClipContext clipContext = new ClipContext(
                eyePosition,
                endPoint,
                ClipContext.Block.OUTLINE, // Проверяем collision-боксы блоков
                ClipContext.Fluid.NONE,    // Игнорируем жидкости
                player
        );

        // Выполняем рейкаст
        HitResult hitResult = player.level().clip(clipContext);

        // Если попали в блок или энтити и это не жидкость
        if (hitResult.getType() == HitResult.Type.BLOCK || hitResult.getType() == HitResult.Type.ENTITY) {
            // Возвращаем разницу между позицией попадания и позицией глаз
            return hitResult.getLocation().subtract(eyePosition);
        }

        // Если блок не найден или попали в жидкость
        return Vec3.ZERO;
    }

    public static Function<ExecutorInfo, Void> findAndReplaceSpring() {
        return (info) -> {
            if(info.getSelectedIndex() == info.getSlotIndex()){
                Player player = info.getPlayer();
                ItemStack found = null;
                for(ItemStack slot : player.getInventory().items){
                    if(slot.getItem() != ModBlocks.SPRING.asItem()){continue;}
                    float stored = getStoredSu(slot);
                    if(stored < 5000){continue;}
                    found = slot;
                }

                if(found == null){return null;}
                CompoundTag tag = info.getStack().getOrCreateTag();
                int Springs_rn = tag.getInt("Springs_rn");
                int springsMaxCount = (info.getItem() == ModItems.EXPLOSION_CHAMBER.get()) ? 1 : 2;
                float[] allSu = getAllStored(tag);

                if (springsMaxCount != Springs_rn && !tag.getBoolean("block") && exceptions(tag)){
                    allSu[Springs_rn] = getStoredSu(found);

                    Springs_rn++;

                    tag.putInt("Springs_rn", Springs_rn);
                    putAllStored(allSu, tag);

                    found.shrink(1);
                }
            }
            info.nextAction();
            return null;
        };
    }


    public static Function<ExecutorInfo, Void> pushOff() {
        return (info) -> {
            if(!info.getPlayer().getCooldowns().isOnCooldown(AllItems.EXTENDO_GRIP.asItem())){
                if(AllItems.EXTENDO_GRIP.isIn(info.getPlayer().getItemInHand(InteractionHand.MAIN_HAND))){
                    if(info.getPlayer() instanceof ServerPlayer serverPlayer){
                        Vec3 check = getRaycastVector(serverPlayer);
                        Vec3 newSpeed = serverPlayer.getViewVector(1).scale(-1);

                        if(check != Vec3.ZERO){
                            CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PushOffPacket());
                            serverPlayer.addDeltaMovement(newSpeed);
                        }

                        ShootableGadgetItemMethods.applyCooldown(info.getPlayer(), info.getStack(), InteractionHand.MAIN_HAND, s -> s.getItem() instanceof ExtendoGripItem, 10);
                    }
                }
            }
            info.nextAction();
            return null;
        };
    }
}