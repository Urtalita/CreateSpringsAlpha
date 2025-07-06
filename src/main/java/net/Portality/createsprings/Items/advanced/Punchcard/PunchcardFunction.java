package net.Portality.createsprings.Items.advanced.Punchcard;

import net.Portality.createsprings.CreateSprings;
import net.minecraft.network.chat.Component;
import org.checkerframework.common.value.qual.EnsuresMinLenIf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public enum PunchcardFunction {
    USE_SPRING_BASE(PunchcardInterpritator.useSpringBase(), false, false, "use", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE
    }),

    WAIT_FOR_SPEED_INCREASED(PunchcardInterpritator.waitForSpeedIncreased(), true, true,"waitForSpeedIncreased", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
    }),

    WAIT_FOR_SPEED_DECREASED(PunchcardInterpritator.waitForSpeedDecreased(), true,true,"waitForSpeedDecreased", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
    }),

    END(PunchcardInterpritator.end(), false, false,"end", PunchcardExecutor.values()),

    DETACH_SPRING(PunchcardInterpritator.detachSpring(), false, false, "detachSpring", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_BASE,
            PunchcardExecutor.EXPLOSION_CHAMBER,
            PunchcardExecutor.SPRING_LAUNCHER
    }),

    SEND_MESSAGE(PunchcardInterpritator.sendMessage(), true, false,"sendMessage", PunchcardExecutor.values()),

    WAIT_TICKS(PunchcardInterpritator.waitTicks(), true, true,"wait", PunchcardExecutor.values()),

    WAIT_FOR_SLOT_SELECTED(PunchcardInterpritator.waitForSlotSelected(), false, false,"waitForSelected", PunchcardExecutor.values()),

    SHOOT_FROM_SPRING_LAUNCHER(PunchcardInterpritator.shootFromSpringLauncher(), false, false, "shootFromSpringLauncher", new PunchcardExecutor[]{
            PunchcardExecutor.SPRING_LAUNCHER
    }),

    EXPLODE_CHAMBER(PunchcardInterpritator.explodeChamber(), false, false, "explodeChamber", new PunchcardExecutor[]{
        PunchcardExecutor.EXPLOSION_CHAMBER
    }),
    ;

    private Function<ExecutorInfo, Void> function;
    private boolean requestParam;
    private boolean needNumericInput;
    private PunchcardExecutor[] executors;
    private String nameFunction;

    PunchcardFunction(Function<ExecutorInfo, Void> function, boolean requestParam, boolean needNumericInput, String nameFunction, PunchcardExecutor[] executors){
        this.function = function;
        this.requestParam = requestParam;
        this.executors = executors;
        this.nameFunction = nameFunction;
        this.needNumericInput = needNumericInput;
    }

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

    public PunchcardFunction getFromName(String nameFunction){
        PunchcardFunction[] functions = PunchcardFunction.values();

        for(PunchcardFunction funk : functions){
            if(funk.nameFunction.equals(nameFunction)){
                return funk;
            }
        }
        return null;
    }

    public static List<Component> getForSelector(PunchcardExecutor executor){
        List<Component> ret = new ArrayList<>();
        PunchcardFunction[] functions = PunchcardFunction.values();

        for(PunchcardFunction funk : functions){
            PunchcardExecutor[] executors = funk.getExecutors();

            for (PunchcardExecutor ex : executors){
                if(ex == executor){
                    ret.add(Component.translatable(CreateSprings.MODID + ".punchcard." + funk.nameFunction));
                }
            }
        }
        return ret;
    }

    public static List<PunchcardFunction> getForActions(PunchcardExecutor executor){
        List<PunchcardFunction> ret = new ArrayList<>();
        PunchcardFunction[] functions = PunchcardFunction.values();

        for(PunchcardFunction funk : functions){
            PunchcardExecutor[] executors = funk.getExecutors();

            for (PunchcardExecutor ex : executors){
                if(ex == executor){
                    ret.add(funk);
                }
            }
        }
        return ret;
    }

    public static int getEndNum(PunchcardExecutor executor){
        PunchcardFunction[] functions = PunchcardFunction.getForActions(executor).toArray(new PunchcardFunction[0]);

        for(int i = 0; i < functions.length; i++){
            if(functions[i].nameFunction.equals(END.nameFunction)) return i;
        }

        return 0;
    }
}
