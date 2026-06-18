package com.Portality.createsprings;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.CSpringsBlockEntities;
import com.Portality.createsprings.blocks.advanced.spring.ISpringBE;
import com.Portality.createsprings.blocks.advanced.spring.ISpringBlock;
import com.Portality.createsprings.blocks.displaySource.CSpringsDisplaySources;
import com.Portality.createsprings.client.CSpringsKeybindings;
import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.client.menus.CSpringsMenus;
import com.Portality.createsprings.client.particles.CSpringsParticles;
import com.Portality.createsprings.client.ponders.CSpringsPonderPlugin;
import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.datagen.CSpringsDatagen;
import com.Portality.createsprings.datagen.advancement.CSpringsAdvancements;
import com.Portality.createsprings.datagen.advancement.CSpringsTriggers;
import com.Portality.createsprings.entities.CSpringsEntityes;
import com.Portality.createsprings.entities.renderer.SpringAlloyBlockProjectileRenderer;
import com.Portality.createsprings.entities.renderer.SpringProjectileRenderer;
import com.Portality.createsprings.items.CSpringsArmorMaterials;
import com.Portality.createsprings.items.CSpringsItemColors;
import com.Portality.createsprings.items.advanced.hat.HatItem;
import com.Portality.createsprings.recipe.CSpringsRecipes;
import com.Portality.createsprings.recipe.NBTShapelessRecipe;
import com.Portality.createsprings.server.contraption.CspringsContraptionTypes;
import com.Portality.createsprings.server.contraption.SpringContraption;
import com.Portality.createsprings.server.fluid.CSpringsFluids;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.MouseSensitivityHandler;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.OverlayHandler;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateSprings.MODID)
public class CreateSprings {
    public static final String MODID = "createsprings";
    public static final String MOD_ID = MODID;
    public static final String ID = MOD_ID;

    public static final CreateRegistrate CSPRINGS_REGISTRATE =
            CreateRegistrate.create(CreateSprings.MODID)
                    .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
                    .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
                    );
    public static Item[] SPRING_TOOLS;
    public static final float STANDARD_SPRING_CAPACITY = 160000;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("create_springs_main_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(CSpringsBlocks.SPRING::asStack)
            .title(Component.translatable("creativetab.create_springs_main_tab"))
            .noScrollBar()
            .build());


    public CreateSprings(IEventBus modEventBus, ModContainer modContainer) {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus forgeBus = NeoForge.EVENT_BUS;

        CSPRINGS_REGISTRATE.registerEventListeners(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);
        CSpringsItems.register(modEventBus);
        CSpringsBlocks.register();
        CSpringsDisplaySources.register();
        CSpringsDataComponents.registerAllComponents(modEventBus);
        CSpringsBlockEntities.register();
        CSpringsParticles.register(modEventBus);
        CSpringsEntityes.register(modEventBus);
        CSpringsRecipes.register(modEventBus);
        CSpringsFluids.register();
        CSpringsPackets.register();
        CSpringsMenus.register();
        CSpringsArmorMaterials.register(modEventBus);
        CSpringsSounds.register(modEventBus);

        CSpringsDatagen.addExtraRegistrateData();

        ModConfigs.register(modLoadingContext, modContainer);
        modEventBus.addListener(CSpringsEntityes::registerEntityAttributes);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(EventPriority.LOWEST, CSpringsDatagen::gatherData);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::registerBuiltInPacks);

        if (FMLEnvironment.dist.isClient()) {
            forgeBus.addListener(OverlayHandler::onRenderOverlay);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStart);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStop);
            forgeBus.addListener(MouseSensitivityHandler::onClientTick);
        }

        NeoForge.EVENT_BUS.register(this);
    }

    public static CreateRegistrate registrate() {
        return CSPRINGS_REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CSpringsFluids.registerFluidInteractions();
        event.enqueueWork(() -> {
            SPRING_TOOLS = new Item[]{
                    CSpringsItems.SPRING_BASE.get(),
                    CSpringsItems.SPRING_LAUNCHER.get(),
                    CSpringsItems.SPRING_SAW.get(),
                    CSpringsItems.SPRING_DRILL.get(),
                    CSpringsItems.SPRING_SHOVE.get(),
                    CSpringsItems.EXPLOSION_CHAMBER.get(),
                    CSpringsItems.PORTATIVE_STEAM_ENGINE.get(),
                    CSpringsItems.SPRING_FAN.get()
            };
        });
    }

    private void onRegister(final RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            CSpringsAdvancements.register();
            CSpringsTriggers.register();
        }

        CspringsContraptionTypes.init();
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CSpringsBlockEntities.MOLD.get(),
                (be, side) -> be.getItemHandler()
        );
    }

    private void registerBuiltInPacks(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addPackFinders(
                    ResourceLocation.fromNamespaceAndPath(MODID, "resourcepacks/legacy_textures"),
                    PackType.CLIENT_RESOURCES,
                    Component.literal("Create: Springs legacy textures"),
                    PackSource.BUILT_IN,
                    false,
                    Pack.Position.TOP
            );
        }
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("create springs loaded");
    }


    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            CSpringsItemColors.register();
            EntityRenderers.register(CSpringsEntityes.SPRING_PROJECTILE.get(), SpringProjectileRenderer::new);
            EntityRenderers.register(CSpringsEntityes.SPRING_ALLOY_BLOCK_PROJECTILE.get(), SpringAlloyBlockProjectileRenderer::new);
            CSpringsPartalModels.register();

            PonderIndex.addPlugin(new CSpringsPonderPlugin());
        }

        @SubscribeEvent
        public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpringPoweredCore.SpringSlotTooltipComponent.class, SpringPoweredCore.SpringSlotRenderer::new);
            event.register(HatItem.HatSlotTooltipComponent.class, HatItem.HatSlotRenderer::new);
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event){
            event.register(CSpringsKeybindings.INSTANCE.PSEOpenKey);
            event.register(CSpringsKeybindings.INSTANCE.PSEBoostKey);
            event.register(CSpringsKeybindings.INSTANCE.PSEDashKey);
            event.register(CSpringsKeybindings.INSTANCE.PSEReleaseKey);
            event.register(CSpringsKeybindings.INSTANCE.ActivatePunchcard);
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void registerSerializers(RegisterEvent event) {
            event.register(BuiltInRegistries.RECIPE_SERIALIZER.key(), helper -> {
                helper.register(ResourceLocation.fromNamespaceAndPath(CreateSprings.MODID, "nbt_shapeless"), NBTShapelessRecipe.Serializer.INSTANCE);
            });
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event){
            event.enqueueWork(PunchcardInterpritator::registerActions);
        }
    }

    @SubscribeEvent
    public void onExplosionStart(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        Level level = event.getLevel();
        BlockPos pos = BlockPos.containing(explosion.center());

        int radius = 3;
        int radiusSquared = radius * radius;
        int distanceSquared;

        AABB aabb = new AABB(explosion.center().add(radius, radius, radius), explosion.center().add(-radius, -radius, -radius));

        List<AbstractContraptionEntity> inArea = level.getEntitiesOfClass(AbstractContraptionEntity.class, aabb);

        for(int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    distanceSquared = x * x + y * y + z * z;

                    if (distanceSquared <= radiusSquared) {
                        if(level.getBlockState(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ())).getBlock() instanceof ISpringBlock){

                            BlockPos foundedPos = new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ());

                            if(level.getBlockEntity(foundedPos) instanceof ISpringBE springBE){
                                springBE.onBlockExploded(foundedPos, explosion);
                            }
                        }
                    }
                }
            }
        }
    }
}
