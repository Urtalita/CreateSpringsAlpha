package com.Portality.createsprings.entities;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.entities.Packages.HatPackageEntity;
import com.Portality.createsprings.entities.Packages.SusPackageEntity;
import com.Portality.createsprings.entities.Projectile.SpringAlloyBlockProjectile;
import com.Portality.createsprings.entities.Projectile.SpringProjectile;
import com.Portality.createsprings.entities.Visual.HatPackageVisual;
import com.Portality.createsprings.entities.Visual.SusPackageVisual;
import com.Portality.createsprings.entities.renderer.HatPackageRenderer;
import com.Portality.createsprings.entities.renderer.SusPackageRenderer;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CreateSprings.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SpringProjectile>> SPRING_PROJECTILE =
            ENTITY_TYPES.register("spring_projectile", () -> EntityType.Builder.<SpringProjectile>of(SpringProjectile::new, MobCategory.MISC)
                    .sized(1f, 1f).setShouldReceiveVelocityUpdates(true).build("spring_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpringAlloyBlockProjectile>> SPRING_ALLOY_BLOCK_PROJECTILE =
            ENTITY_TYPES.register("spring_alloy_block_projectile", () -> EntityType.Builder.<SpringAlloyBlockProjectile>of(SpringAlloyBlockProjectile::new,
                            MobCategory.MISC).sized(0.5f, 0.5f).setShouldReceiveVelocityUpdates(true).build("spring_alloy_block_projectile"));

    public static final EntityEntry<SusPackageEntity> SUS_PACKAGE = register("sus_package", SusPackageEntity::new, () -> SusPackageRenderer::new,
                    MobCategory.MISC, 10, 3, true, false, SusPackageEntity::build)
            .visual(() -> SusPackageVisual::new, true)
            .register();

    public static final EntityEntry<HatPackageEntity> HAT_PACKAGE = register("hat", HatPackageEntity::new, () -> HatPackageRenderer::new,
            MobCategory.MISC, 10, 3, true, false, HatPackageEntity::build)
            .visual(() -> HatPackageVisual::new, true)
            .register();




    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    private static <T extends Entity> CreateEntityBuilder<T, ?> register(String name, EntityType.EntityFactory<T> factory,
                                                                         NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                         MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
                                                                         NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        String id = Lang.asId(name);
        return (CreateEntityBuilder<T, ?>) CreateSprings.CSPRINGS_REGISTRATE
                .entity(id, factory, group)
                .properties(b -> b.setTrackingRange(range)
                        .setUpdateInterval(updateFrequency)
                        .setShouldReceiveVelocityUpdates(sendVelocity))
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                })
                .renderer(renderer);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {

        event.put(SUS_PACKAGE.get(), PackageEntity.createPackageAttributes()
                .build());

        event.put(HAT_PACKAGE.get(), PackageEntity.createPackageAttributes()
                .build());
    }
}
