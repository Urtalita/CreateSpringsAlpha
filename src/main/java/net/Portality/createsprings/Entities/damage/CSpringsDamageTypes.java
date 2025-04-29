package net.Portality.createsprings.Entities.damage;

import net.Portality.createsprings.CreateSprings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class CSpringsDamageTypes {
    public static final ResourceKey<DamageType>
            SPRING = key("spring"),
            SPRING_BOX = key("spring_box")
    ;

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CreateSprings.asResource(name));
    }

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new CSpringsDamageTypeBuilder(SPRING).register(ctx);
        new CSpringsDamageTypeBuilder(SPRING_BOX).register(ctx);
    }
}
