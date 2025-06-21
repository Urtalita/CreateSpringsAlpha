package net.Portality.createsprings.utill;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;

public class CSpringsPartalModels {

    public static final PartialModel

    SPRING = block("spring/spring"),
        SPRING_PIECE = block("spring/springpiece"),
        SPRING_PLATE = block("spring/springplate"),
        LARGE_SPRING_PLATE = block("spring/large_spring_plate"),
        SPRING_RING = block("spring/springring"),

    SPRING_SAW = item("saw/saw"),
        SAW_HEAD = item("saw/saw_head"),
        SAW_SHAFT = item("saw/saw_shaft"),

    SPRING_SHOVE = item("spring_shove_head"),

    SUS_BOX = block("sus_box"),

    SPRING_TOOL_SPRING_PLATE = item("drill/spring_tool_spring_plate"),
    SPRING_TOOL_SPRING_PIECE = item("drill/spring_tool_spring_piece"),

    CHAMBER_SPRING_PIECE = item("chamber/chamber_spring_piece"),
    CHAMBER_SPRING_PLATE = item("chamber/chamber_spring_plate"),
    CHAMBER_GUNPOWDER = item("chamber/chamber_gunpowder"),

    HAT = armor("hat"),
    HAT2 = armor("hat2"),
    HAT3 = armor("hat3"),
    HAT4 = armor("hat4"),

    PORTATIVE_ENGINE = item("portative_steam_engine"),

    MOLD = block("andesite_mold"),

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

    public static void register(){}
}
