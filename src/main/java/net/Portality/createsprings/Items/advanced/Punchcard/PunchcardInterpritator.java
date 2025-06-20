package net.Portality.createsprings.Items.advanced.Punchcard;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.SpringLauncher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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

            float stored = getAllStoredSum(getAllStored(2, tag));
            double speed = tag.getDouble("Speed");

            if (stored > 5000 && speed < 5500){
                speed += 250;
                stored -= 2000;
                if(speed > 5000) speed = 5000;
            }

            float[] allsu = getAllStored(2, tag);
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
            float[] allSu = getAllStored(2, tag);

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
            SpringLauncher.

            info.nextAction();
            return null;
        };
    }
}