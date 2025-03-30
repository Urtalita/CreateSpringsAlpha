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

    LAUNCHER_SPRING_CHARGED = item("launcher/spring_charged"),
    LAUNCHER_SPRING_UNCHARGED = item("launcher/spring_uncharged"),
    LAUNCHER_AMMO = item("launcher/spring_ammo"),
    LAUNCHER_SPYGLASS = item("launcher/spyglass"),
    STRESOMETR = item("drill/stressometr"),

    SPRING_SAW = item("saw/saw"),
        SAW_HEAD = item("saw/saw_head"),
        SAW_SHAFT = item("saw/saw_shaft"),

    WelderHead = block("friction_welder/top");

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
