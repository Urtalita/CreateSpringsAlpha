package net.Portality.createsprings.Entities;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.Packages.HatPackageEntity;
import net.Portality.createsprings.Entities.Packages.SusPackageEntity;
import net.Portality.createsprings.Entities.Projectile.SpringAlloyBlockProjectile;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.Portality.createsprings.Entities.Visual.HatPackageVisual;
import net.Portality.createsprings.Entities.renderer.HatPackageRenderer;
import net.Portality.createsprings.Entities.renderer.SusPackageRenderer;
import net.Portality.createsprings.Entities.Visual.SusPackageVisual;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
    }
}
