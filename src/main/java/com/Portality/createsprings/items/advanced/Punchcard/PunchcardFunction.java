package com.Portality.createsprings.items.advanced.Punchcard;

import com.Portality.createsprings.CreateSprings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public enum PunchcardFunction {
    USE_SPRING_BASE(PunchcardInterpritator.useSpringBase(), false, false, false, "use", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE
    }),

    WAIT_FOR_SPEED_INCREASED(PunchcardInterpritator.waitForSpeedIncreased(), true, true, true,"waitForSpeedIncreased", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
    }),

    WAIT_FOR_SPEED_DECREASED(PunchcardInterpritator.waitForSpeedDecreased(), true,true, true,"waitForSpeedDecreased", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
    }),

    END(PunchcardInterpritator.end(), false, false, false,"end", PunchcardExecutor.values()),

    DETACH_SPRING(PunchcardInterpritator.detachSpring(), false, false, false, "detachSpring", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
            PunchcardExecutor.EXPLOSION_CHAMBER,
            PunchcardExecutor.SPRING_LAUNCHER
    }),

    SEND_MESSAGE(PunchcardInterpritator.sendMessage(), true, false, false,"sendMessage", PunchcardExecutor.values()),

    WAIT_FOR_ACTIVATION(PunchcardInterpritator.empty(), false, false, true,"waitForActivation", PunchcardExecutor.values()),

    WAIT_TICKS(PunchcardInterpritator.waitTicks(), true, true, true,"wait", PunchcardExecutor.values()),

    WAIT_FOR_SLOT_SELECTED(PunchcardInterpritator.waitForSlotSelected(), false, false, true,"waitForSelected", PunchcardExecutor.values()),

    SHOOT_FROM_SPRING_LAUNCHER(PunchcardInterpritator.shootFromSpringLauncher(), false, false, false, "shootFromSpringLauncher", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_LAUNCHER
    }),

    EXPLODE_CHAMBER(PunchcardInterpritator.explodeChamber(), false, false, false, "explodeChamber", new PunchcardExecutor[]{
        PunchcardExecutor.EXPLOSION_CHAMBER
    }),

    TOGGLE_BOOST(PunchcardInterpritator.toggleBoost(), false, false, false, "toggleBoost", new PunchcardExecutor[]{
            PunchcardExecutor.PSE
    }),

    STEAM_DASH(PunchcardInterpritator.steamDash(), false, false, false, "steamDash", new PunchcardExecutor[]{
        PunchcardExecutor.PSE
    }),

    SHOOTING(PunchcardInterpritator.shootFromCannon(), false, false, false, "potatoCannonShoot", new PunchcardExecutor[]{
        PunchcardExecutor.POTATO_CANON
    }),

    TRIPPLE_SHOOTING(PunchcardInterpritator.tripleShot(), false, false, false, true, "potatoCannonTripleShoot", new PunchcardExecutor[]{
        PunchcardExecutor.POTATO_CANON
    }),

    AIR_DASH(PunchcardInterpritator.airDash(), false, false, false, true, "airDash", new PunchcardExecutor[]{
        PunchcardExecutor.NETHERITE_BACKTANK,
        PunchcardExecutor.BACKTANK
    }),

    GRAB(PunchcardInterpritator.grab(), false, false, false, true, "grab", new PunchcardExecutor[]{
            PunchcardExecutor.EXTENDRO_GRIP
    }),

    PUSH_OFF(PunchcardInterpritator.pushOff(), false, false, false, true, "push_off", new PunchcardExecutor[]{
            PunchcardExecutor.EXTENDRO_GRIP
    }),

    REPLACE_SPRING(PunchcardInterpritator.findAndReplaceSpring(), false, false, false, "replace", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
            PunchcardExecutor.EXPLOSION_CHAMBER,
            PunchcardExecutor.SPRING_LAUNCHER
    })
    ;

    private Function<ExecutorInfo, Void> function;
    private boolean requestParam;
    private boolean needNumericInput;
    private PunchcardExecutor[] executors;
    private String nameFunction;
    private boolean isCondition;
    private boolean isSpecal = false;

    PunchcardFunction(Function<ExecutorInfo, Void> function, boolean requestParam, boolean needNumericInput, boolean isCondition, String nameFunction, PunchcardExecutor[] executors){
        this.function = function;
        this.isCondition = isCondition;
        this.requestParam = requestParam;
        this.executors = executors;
        this.nameFunction = nameFunction;
        this.needNumericInput = needNumericInput;
    }

    PunchcardFunction(Function<ExecutorInfo, Void> function, boolean requestParam, boolean needNumericInput, boolean isCondition, boolean isSpecal, String nameFunction, PunchcardExecutor[] executors){
        this.function = function;
        this.isCondition = isCondition;
        this.requestParam = requestParam;
        this.executors = executors;
        this.nameFunction = nameFunction;
        this.needNumericInput = needNumericInput;
        this.isSpecal = isSpecal;
    }

    PunchcardFunction(Function<ExecutorInfo, Void> function, boolean requestParam, boolean needNumericInput, String nameFunction, PunchcardExecutor executor){
        this.function = function;
        this.requestParam = requestParam;
        this.executors = new PunchcardExecutor[]{executor};
        this.nameFunction = nameFunction;
        this.needNumericInput = needNumericInput;
    }

    public boolean isCondition() {return isCondition;}

    public boolean isNeedNumericInput() {
        return needNumericInput;
    }

    public String getFunctionName() {
        return nameFunction;
    }

    public Function<ExecutorInfo, Void> getFunc() {
        return function;
    }

    public boolean isRequestParam() {
        return requestParam;
    }

    public PunchcardExecutor[] getExecutors() {
        return executors;
    }

    public boolean IsInExecutors(PunchcardFunction function, PunchcardExecutor executor){
        PunchcardExecutor[] executors = function.executors;

        for(int i = 0; i < executors.length; i++){
            if(executors[i] == executor)return true;
        }
        return false;
    }

    public static PunchcardFunction getFromName(String nameFunction){
        PunchcardFunction[] functions = PunchcardFunction.values();

        for(PunchcardFunction funk : functions){
            if(funk.nameFunction.equals(nameFunction)){
                return funk;
            }
        }
        return PunchcardFunction.END;
    }

    public static List<Component> getForSelector(PunchcardExecutor executor, boolean forCondition){
        List<Component> ret = new ArrayList<>();
        PunchcardFunction[] functions = PunchcardFunction.values();

        for(PunchcardFunction funk : functions){
            PunchcardExecutor[] executors = funk.getExecutors();

            for (PunchcardExecutor ex : executors){
                if(ex == executor){
                    if(funk.isCondition == forCondition){
                        if(funk.isSpecal){
                            ret.add(Component.translatable(CreateSprings.MODID + ".punchcard." + funk.nameFunction).withStyle(ChatFormatting.YELLOW));
                            continue;
                        }
                        ret.add(Component.translatable(CreateSprings.MODID + ".punchcard." + funk.nameFunction));
                    }
                }
            }
        }
        return ret;
    }

    public static List<PunchcardFunction> getForActions(PunchcardExecutor executor, boolean forCondition){
        List<PunchcardFunction> ret = new ArrayList<>();
        PunchcardFunction[] functions = PunchcardFunction.values();

        for(PunchcardFunction funk : functions){
            PunchcardExecutor[] executors = funk.getExecutors();

            for (PunchcardExecutor ex : executors){
                if(ex == executor){
                    if(funk.isCondition == forCondition){
                        ret.add(funk);
                    }
                }
            }
        }
        if(!ret.contains(END)){
            ret.add(END);
        }
        return ret;
    }

    public static int getEndNum(PunchcardExecutor executor, int selector){
        boolean forCondition = selector == 0;
        PunchcardFunction[] functions = PunchcardFunction.getForActions(executor, forCondition).toArray(new PunchcardFunction[0]);

        for(int i = 0; i < functions.length; i++){
            if(functions[i].nameFunction.equals(END.nameFunction)) return i;
        }

        return 0;
    }
}
