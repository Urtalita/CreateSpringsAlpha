package net.Portality.createsprings.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;

public class CSCommon extends ConfigBase {

    public final CSKinetics kinetics = nested(0, CSKinetics::new,Comments.kinetics);
    public final ConfigBool SPRINGS_CAN_SPLASH = new ConfigBool("spring_splash", true, Comments.SPRINGS_CAN_SPLASH);
    public final ConfigInt SPRING_CAPACITY = new ConfigInt("spring_capacity", 160000, 1000, Integer.MAX_VALUE, Comments.SPRING_CAPACITY);
    public final ConfigInt LARGE_SPRING_CAPACITY = new ConfigInt("large_spring_capacity", 4, 1, Integer.MAX_VALUE, Comments.LARGE_SPRING_CAPACITY);
    public final ConfigInt SPRING_SPLASH_DURATION = new ConfigInt("spring_splash_duration", 40, 5, Integer.MAX_VALUE, Comments.SPRING_SPLASH_DURATION);
    public final ConfigInt SPRING_LEN = new ConfigInt("spring_len", 32, 1, 384, Comments.SPRING_LEN);
    public final ConfigBool DEBUG_CAPACITY = new ConfigBool("debug_capacity_of_large_spring", false, Comments.DEBUG_CAPACITY);
    public final ConfigFloat KNOCKBACK_COEF = new ConfigFloat("knockback_coef_for_springs", 4f, 0, 24, Comments.KNOCKBACK_COEF);
    public final ConfigFloat SPRING_TOOL_SPEED_COEF = new ConfigFloat("soring_tool_speed_coef", 1f, 0, 1024, Comments.SPRING_TOOLS_SPEED_COEF);
    public final ConfigFloat PSE_FUEL_USAGE = new ConfigFloat("pse_fuel_usage", 2.5f, 0, 1024, Comments.PSE_FUEL_USAGE);

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String kinetics = "Modify Create Springs blocks comportements";
        static String SPRINGS_CAN_SPLASH = "Springs may or may not discharge instantly when in splash mode";
        static String SPRING_CAPACITY = "capacity of the spring";
        static String LARGE_SPRING_CAPACITY = "capacity coefficent of the large spring";
        static String SPRING_SPLASH_DURATION = "spring splash duration in ticks";
        static String SPRING_LEN = "max large spring len";
        static String DEBUG_CAPACITY = "debug large spring capacity";
        static String KNOCKBACK_COEF = "knockback coefficient of springs";
        static String SPRING_TOOLS_SPEED_COEF = "speed coefficient of spring tools";
        static String PSE_FUEL_USAGE = "how much fuel PSE will consume per 1 level";
    }
}
