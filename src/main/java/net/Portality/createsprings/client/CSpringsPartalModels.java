package net.Portality.createsprings.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageStyles;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.resources.ResourceLocation;

public class CSpringsPartalModels {

    public static final PartialModel

    SPRING = block("spring/spring"),
        SPRING_PIECE = block("spring/springpiece"),
        SPRING_PLATE = block("spring/springplate"),
        LARGE_SPRING_PLATE = block("spring/large_spring_plate"),

    SPRING_SAW = item("spring_saw/saw"),
        SAW_HEAD = item("spring_saw/saw_head"),
        SAW_SHAFT = item("spring_saw/saw_shaft"),

    SPRING_SHOVE = item("spring_shove/head"),

    SUS_BOX = block("sus_box"),

    SPRING_TOOL_SPRING_PLATE = item("spring_drill/spring_tool_spring_plate"),
    SPRING_TOOL_SPRING_PIECE = item("spring_drill/spring_tool_spring_piece"),

    CHAMBER_SPRING_PIECE = item("explosion_chamber/chamber_spring_piece"),
    CHAMBER_SPRING_PLATE = item("explosion_chamber/chamber_spring_plate"),
    CHAMBER_GUNPOWDER = item("explosion_chamber/chamber_gunpowder"),

    HAT = armor("hat"),
    HAT2 = armor("hat2"),
    HAT3 = armor("hat3"),
    HAT4 = armor("hat4"),

    PORTATIVE_ENGINE = item("portative_steam_engine/base"),
    ENGINE_SHAFT = item("portative_steam_engine/shaft"),
    ENGINE_MID = item("portative_steam_engine/mid"),
    ENGINE_PISTON = item("portative_steam_engine/piston"),
    WHOLE_ENGINE = item("portative_steam_engine/portative_steam_engine"),

    STRESS_ARROW = item("spring_drill/speedometer_arrow"),
    SPRING_DRILL_HEAD = item("spring_drill/drill_head"),

    SPRING_FAN_HEAD = item("spring_fan/fan"),

    MOLD = block("andesite_mold"),

    INTERFACE_TOP = block("kinetic_interface/top"),
    INTERFACE_PULLEY = block("kinetic_interface/pulley"),

    CENTERED_SHAFT = block("shaft_sentered"),

    DEBUG = block("debug"),

    SPRING_CATAPULT_CONNECTION = block("spring_catapult/connections"),
    SPRING_CATAPULT_HOLDER = block("spring_catapult/second_connections"),

    WelderHead = block("friction_welder/top"),
            LARGE_SPRING_COIL_ROTATED = block("large_spring_coil_partal_rotated"),
    LARGE_SPRING_COIL = block("large_spring_coil_partal"),
            LARGE_SPRING_COIL_CORNER = block("large_spring_coil_partal_corner");


    private static PartialModel block(String path) {
        return PartialModel.of(CreateSprings.asResource("block/" + path));
    }

    private static PartialModel armor(String path){return PartialModel.of(CreateSprings.asResource("armor/" + path));}

    private static PartialModel item(String path) {
        return PartialModel.of(CreateSprings.asResource("item/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(CreateSprings.asResource("entity/" + path));
    }

    public static void register(){
        AllPartialModels.PACKAGE_RIGGING.put(CreateSprings.asResource("sus_package"), PartialModel.of(PackageStyles.STYLES.get(0).getRiggingModel()));
        AllPartialModels.PACKAGES.put(CreateSprings.asResource("sus_package"), SUS_BOX);

        AllPartialModels.PACKAGE_RIGGING.put(CreateSprings.asResource("hat"), PartialModel.of(PackageStyles.STYLES.get(0).getRiggingModel()));
        AllPartialModels.PACKAGES.put(CreateSprings.asResource("hat"), HAT);
    }
}
