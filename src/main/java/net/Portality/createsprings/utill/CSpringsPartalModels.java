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
        SPRING_RING = block("spring/springring"),

    SPRING_SAW = item("saw/saw"),
        SAW_HEAD = item("saw/saw_head"),
        SAW_SHAFT = item("saw/saw_shaft"),

    WelderHead = block("friction_welder/top"),
    LARGE_SPRING_COIL = block("large_spring_coil_partal");

    private static PartialModel block(String path) {
        return PartialModel.of(CreateSprings.asResource("block/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of(CreateSprings.asResource("item/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(CreateSprings.asResource("entity/" + path));
    }

    public static void register(){}
}
