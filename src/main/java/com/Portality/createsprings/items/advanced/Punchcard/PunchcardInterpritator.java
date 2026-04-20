package com.Portality.createsprings.items.advanced.Punchcard;

import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.items.ModItems;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.SpringStufs.SpringSpeedSys;
import com.Portality.createsprings.utill.CSpringsDataComponents;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
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

import java.util.HashMap;
import java.util.function.Function;

import static com.Portality.createsprings.items.SpringStufs.SpringPoweredCore.*;
import static com.Portality.createsprings.items.advanced.Spring.SpringItem.getStoredSu;

public class PunchcardInterpritator {
    public static HashMap<String, Function<ExecutorInfo, Void>> allPunchcardActions = new HashMap<>();

    public static void registerActions(){
        for(PunchcardFunction function : PunchcardFunction.values()){
            allPunchcardActions.put(function.getFunctionName(), function.getFunc());
        }
    }

    public static void DoPunchcardLogic(ExecutorInfo info){
        if(!info.getStack().has(CSpringsDataComponents.PUNCHCARD)){return;}
        CompoundTag punchcard = info.getStack().get(CSpringsDataComponents.PUNCHCARD);

        int curAction = punchcard.getInt("curAction");
        String actionKey = PunchcardAction.getAllFromString(punchcard.getString(String.valueOf(curAction))).getName();

        Function<ExecutorInfo, Void> action = allPunchcardActions.get(actionKey);
        if(action == null){return;}

        action.apply(info);
    }

    public static String getParam(ItemStack stack){
        CompoundTag punchcard = stack.get(CSpringsDataComponents.PUNCHCARD);
        int curAction = punchcard.getInt("curAction");
        return PunchcardAction.getAllFromString(punchcard.getString(String.valueOf(curAction))).getParameter();
    }

    public static void setParam(String param, ItemStack stack){
        CompoundTag punchcard = stack.get(CSpringsDataComponents.PUNCHCARD);
        int curAction = punchcard.getInt("curAction");
        PunchcardAction action = PunchcardAction.getAllFromString(punchcard.getString(String.valueOf(curAction)));
        action.parameter = param;

        punchcard.putString(String.valueOf(curAction), PunchcardAction.putPunchcardActionInString(action));
    }

    public static Function<ExecutorInfo, Void> useSpringBase(){
        return (info) -> {
            ItemStack stack = info.getStack();
            String param = getParam(stack);

            float stored = getAllStoredSum(getAllStored(stack));
            double speed = SpringSpeedSys.getRealSpeed(stack);

            if (stored > SpringSpeedSys.MAX_REGULAR_SPEED && speed < 5500){
                speed += 250;
                stored -= 2000;
                if(speed > SpringSpeedSys.MAX_REGULAR_SPEED) speed = SpringSpeedSys.MAX_REGULAR_SPEED;
            }

            float[] allsu = getAllStored(stack);
            putAllStored(spreadSu(allsu, stored), stack);
            stack.set(CSpringsDataComponents.TOOL_SPEED, (float) speed);

            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitForSpeedIncreased(){
        return (info) -> {
            float param = (float) Integer.parseInt(getParam(info.getStack())) / 256 * 5000;
            float speed = (float) SpringSpeedSys.getRealSpeed(info.getStack());

            if(param >= speed){
                info.nextAction();
            }

            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitForSpeedDecreased(){
        return (info) -> {
            float param = (float) Integer.parseInt(getParam(info.getStack())) / 256 * 5000;
            float speed = (float) SpringSpeedSys.getRealSpeed(info.getStack());

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
            int Springs_rn = SpringPoweredCore.getSprings(info.getStack());
            float[] allSu = getAllStored(info.getStack());

            if (Springs_rn > 0){
                float springSu;

                springSu = allSu[Springs_rn-1];
                allSu[Springs_rn-1] = 0;

                info.getPlayer().getInventory().add(putSuInSpring(springSu));

                Springs_rn--;
                info.getStack().set(CSpringsDataComponents.SPRING_AMOUNT, Springs_rn);
                putAllStored(allSu, info.getStack());
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> sendMessage() {
        return (info) -> {
            if(info.isSelected()){return null;}

            info.getPlayer().displayClientMessage(Component.literal(getParam(info.getStack())), true);
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitTicks() {
        return (info) -> {
            int param = Integer.parseInt(getParam(info.getStack()));
            if(param <= 10){
                info.nextAction();
                return null;
            }
            setParam(String.valueOf(param - 10), info.getStack());
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> waitForSlotSelected() {
        return (info) -> {
            if(info.isSelected()){
                info.nextAction();
            }
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> shootFromSpringLauncher() {
        return (info) -> {
            if(info.isSelected()){
                return null;
            }
            //if(info.getItem() instanceof SpringLauncher launcher){
            //    launcher.releaseUsing(info.getStack(), info.getLevel(), info.getPlayer(), 0);
            //    info.nextAction();
            //}
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> explodeChamber() {
        return (info) -> {
            //if(info.getItem() instanceof ChamberItem chamberItem){
            //    chamberItem.use(info.getLevel(), info.getPlayer(), InteractionHand.MAIN_HAND);
            //    info.nextAction();
            //}
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> toggleBoost(){
        return (info) -> {
            /*if(info.getPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
                ItemStack stack = info.getStack();
                int boosted = stack.getOrCreateTag().getInt("boosted");
                if(boosted < 99){
                    stack.getOrCreateTag().putBoolean("boost", !stack.getOrCreateTag().getBoolean("boost"));
                    if(boosted <= 0){
                        stack.getOrCreateTag().putInt("boosted", 1);
                    }
                }
            }

             */
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> steamDash(){
        return (info) -> {
            /*if(info.getPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PortativeSteamEngineItem){
                PortativeSteamEngineItem.steamDash(info.getPlayer(), info.getLevel());
            }

             */
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> shootFromCannon(){
        return (info) -> {
            if(!info.getPlayer().getCooldowns().isOnCooldown(AllItems.POTATO_CANNON.asItem())){
                if (info.isSelected()){
                    info.getItem().use(info.getLevel(), info.getPlayer(), InteractionHand.MAIN_HAND);
                }
            }
            info.nextAction();
            return null;
        };
    }

    public static Function<ExecutorInfo, Void> tripleShot(){
        return (info) -> {
            if (info.isSelected()){
                if(!info.getPlayer().getCooldowns().isOnCooldown(AllItems.POTATO_CANNON.asItem())){
                    for(int i = 0; i < 3; i++){
                        info.getItem().use(info.getLevel(), info.getPlayer(), InteractionHand.MAIN_HAND);
                        info.getPlayer().setXRot(info.getPlayer().getXRot() - 5);
                        ShootableGadgetItemMethods.applyCooldown(info.getPlayer(), info.getStack(), InteractionHand.MAIN_HAND, s -> s.getItem() instanceof PotatoCannonItem, 60);
                    }
                    if(info.getPlayer() instanceof ServerPlayer serverPlayer){
                        //CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new RotatePlayerPacket(serverPlayer.getXRot()));
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
                float air = 0;
                if(info.getStack().has(AllDataComponents.BACKTANK_AIR)) air = info.getStack().get(AllDataComponents.BACKTANK_AIR);
                if(air > 25){
                    if(info.getPlayer().getDeltaMovement().y < 2){
                        info.getPlayer().addDeltaMovement(new Vec3(0, 0.8f, 0));
                    }
                    if(info.getPlayer() instanceof ServerPlayer serverPlayer){
                        //CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new AirDashPlayerPacket());
                        AllSoundEvents.STEAM.playOnServer(info.getPlayer().level(), BlockPos.containing(info.getPlayer().position()).above(), 0.8f, 1f);
                    }
                    air -= 25;
                    info.getStack().set(AllDataComponents.BACKTANK_AIR, (int) air);
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
                        //CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new GrabPacket(newSpeed));
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
            if(info.isSelected()){
                Player player = info.getPlayer();
                ItemStack found = null;
                for(ItemStack slot : player.getInventory().items){
                    if(slot.getItem() != ModBlocks.SPRING.asItem()){continue;}
                    float stored = getStoredSu(slot);
                    if(stored < 5000){continue;}
                    found = slot;
                }

                if(found == null){return null;}
                int Springs_rn = SpringPoweredCore.getSprings(info.getStack());
                //int springsMaxCount = (info.getItem() == ModItems.EXPLOSION_CHAMBER.get()) ? 1 : 2;
                int springsMaxCount = 2;
                float[] allSu = getAllStored(info.getStack());

                /*
                if (springsMaxCount != Springs_rn && !tag.getBoolean("block") && exceptions(tag)){
                    allSu[Springs_rn] = getStoredSu(found);

                    Springs_rn++;

                    tag.putInt("Springs_rn", Springs_rn);
                    putAllStored(allSu, tag);

                    found.shrink(1);
                }

                 */
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
                            //CSpringsPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PushOffPacket());
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