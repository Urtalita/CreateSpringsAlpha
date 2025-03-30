package net.Portality.createsprings.Entities;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.Projectile.SpringAlloyBlockProjectile;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CreateSprings.MODID);

    public static final RegistryObject<EntityType<SpringProjectile>> SPRING_PROJECTILE =
            ENTITY_TYPES.register("spring_projectile", () -> EntityType.Builder.<SpringProjectile>of(SpringProjectile::new, MobCategory.MISC)
                    .sized(1f, 1f).build("spring_projectile"));

    public static final RegistryObject<EntityType<SpringAlloyBlockProjectile>> SPRING_ALLOY_BLOCK_PROJECTILE =
            ENTITY_TYPES.register("spring_alloy_block_projectile", () -> EntityType.Builder.<SpringAlloyBlockProjectile>of(SpringAlloyBlockProjectile::new,
                            MobCategory.MISC).sized(0.5f, 0.5f).build("spring_alloy_block_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
