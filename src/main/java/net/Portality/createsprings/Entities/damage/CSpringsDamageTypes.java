package net.Portality.createsprings.Entities.damage;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.Portality.createsprings.CreateSprings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class CSpringsDamageTypes {
    public static final ResourceKey<DamageType>
            TEST = key("test");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Create.asResource(name));
    }

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new CSpringsDamageTypeBuilder(TEST).register(ctx);
    }
}
