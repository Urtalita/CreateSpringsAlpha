package com.Portality.createsprings;

import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.blocks.advanced.ModBlockEntities;
import com.Portality.createsprings.client.CSpringsPartalModels;
import com.Portality.createsprings.client.particles.CSpringsParticles;
import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.datagen.CSpringsDatagen;
import com.Portality.createsprings.datagen.advancement.CSpringsAdvancements;
import com.Portality.createsprings.datagen.advancement.CSpringsTriggers;
import com.Portality.createsprings.entities.ModEntities;
import com.Portality.createsprings.entities.renderer.SpringAlloyBlockProjectileRenderer;
import com.Portality.createsprings.entities.renderer.SpringProjectileRenderer;
import com.Portality.createsprings.fluid.CSpringsFluids;
import com.Portality.createsprings.items.ModItems;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.MouseSensitivityHandler;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.OverlayHandler;
import com.Portality.createsprings.items.SpringStufs.SpringPoweredCore;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardInterpritator;
import com.Portality.createsprings.recipe.ModRecipes;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.CSpringsPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(MODID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(ModBlocks.SPRING::asStack)
            .title(Component.translatable("creativetab.create_springs_main_tab"))
            .displayItems((itemDisplayParameters, output) -> CSPRINGS_REGISTRATE.getAll(Registries.ITEM).forEach((item -> {
                output.accept(item.get());
            })))
            .build());

    public CreateSprings(IEventBus modEventBus, ModContainer modContainer) {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus forgeBus = NeoForge.EVENT_BUS;

        CSPRINGS_REGISTRATE.registerEventListeners(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register();
        CSpringsPartalModels.register();
        CSpringsDataComponents.registerAllComponents(modEventBus);
        ModBlockEntities.register();
        CSpringsSounds.register(modEventBus);
        CSpringsParticles.register(modEventBus);
        ModEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        CSpringsFluids.register();
        CSpringsPackets.register();

        CSpringsDatagen.addExtraRegistrateData();

        ModConfigs.register(modLoadingContext, modContainer);
        modEventBus.addListener(ModEntities::registerEntityAttributes);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(EventPriority.LOWEST, CSpringsDatagen::gatherData);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::onRegister);

        if (FMLEnvironment.dist.isClient()) {
            forgeBus.addListener(OverlayHandler::onRenderOverlay);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStart);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStop);
            forgeBus.addListener(MouseSensitivityHandler::onClientTick);
        }

        NeoForge.EVENT_BUS.register(this);
        CREATIVE_MODE_TABS.register(modEventBus);
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
                    ModItems.SPRING_BASE.get(),
                    ModItems.SPRING_LAUNCHER.get(),
                    ModItems.SPRING_SAW.get(),
                    ModItems.SPRING_DRILL.get(),
                    ModItems.SPRING_SHOVE.get(),
                    //ModItems.EXPLOSION_CHAMBER.get(),
                    //ModItems.PORTATIVE_STEAM_ENGINE.get(),
                    ModItems.SPRING_FAN.get()
            };
        });
    }

    private void onRegister(final RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            CSpringsAdvancements.register();
            CSpringsTriggers.register();
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Регистрируем обработчик предметов для нашего Block Entity
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK, // Тип капсулы (инвентарь)
                ModBlockEntities.MOLD.get(), // Ваш BlockEntityType
                (be, side) -> be.getItemHandler() // Лямбда, вызывающая метод из BE
        );
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.SPRING_PROJECTILE.get(), SpringProjectileRenderer::new);
            EntityRenderers.register(ModEntities.SPRING_ALLOY_BLOCK_PROJECTILE.get(), SpringAlloyBlockProjectileRenderer::new);
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event){
            event.enqueueWork(PunchcardInterpritator::registerActions);
        }

        @SubscribeEvent
        public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpringPoweredCore.SpringSlotTooltipComponent.class,
                    SpringPoweredCore.SpringSlotRenderer::new);
        }
    }
}
