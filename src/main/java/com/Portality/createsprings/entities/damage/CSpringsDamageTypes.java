package com.Portality.createsprings.entities.damage;

import com.Portality.createsprings.CreateSprings;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class CSpringsDamageTypes {
    public static final ResourceKey<DamageType>
            SPRING = key("spring"),
            SPRING_BOX = key("spring_box"),
            PSE = key("pse")
    ;

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CreateSprings.asResource(name));
    }

    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        new DamageTypeBuilder(SPRING).scaling(DamageScaling.ALWAYS).register(ctx);
        new DamageTypeBuilder(SPRING_BOX).scaling(DamageScaling.ALWAYS).register(ctx);
    }
}
