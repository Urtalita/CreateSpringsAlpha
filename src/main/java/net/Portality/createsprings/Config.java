package net.Portality.createsprings;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = CreateSprings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue SPRINGS_CAN_SPLASH =
            BUILDER.comment("Springs may or may not discharge instantly when in splash mode").define("splash_mode", true);

    private static final ForgeConfigSpec.IntValue SPRING_CAPACITY =
            BUILDER.comment("capacity of the spring").defineInRange("spring_capacity", 160000, 2, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue SPRING_SPLASH_DURATION =
            BUILDER.comment("spring splash duration in ticks").defineInRange("spring_splash_duration", 40, 10, 10000);

    private static final ForgeConfigSpec.DoubleValue KNOCKBACK_COEF =
            BUILDER.comment("knockback coefficient of springs").defineInRange("knockback_coef", 4f, 0, 10);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean springs_can_splash;
    public static int spring_capacity;
    public static int spring_splash_duration;
    public static double knockback_coef;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        springs_can_splash = SPRINGS_CAN_SPLASH.get();
        spring_capacity = SPRING_CAPACITY.get();
        spring_splash_duration = SPRING_SPLASH_DURATION.get();
        knockback_coef = KNOCKBACK_COEF.get();

        // convert the list of strings into a set of items
    }
}
