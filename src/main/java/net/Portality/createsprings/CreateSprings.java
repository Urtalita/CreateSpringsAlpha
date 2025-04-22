package net.Portality.createsprings;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.renderer.SpringAlloyBlockProjectileRenderer;
import net.Portality.createsprings.Entities.renderer.SpringProjectileRenderer;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.MouseSensitivityHandler;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.OverlayHandler;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.ViewModificationHandler;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.contraption.CspringsContraptionTypes;
import net.Portality.createsprings.fluid.ModFluids;
import net.Portality.createsprings.menus.MenuInit;
import net.Portality.createsprings.menus.ModCreativeModeTabs;
import com.mojang.logging.LogUtils;
import net.Portality.createsprings.menus.Spring.SpringScreen;
import net.Portality.createsprings.recipe.ModRecipes;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.Portality.createsprings.menus.Punchcard.PunchcardScreen;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateSprings.MODID)
public class CreateSprings {

    public static final String MODID = "createsprings";
    public static final float SPRING_CAPACITY = 160 * 1000;
    public static final CreateRegistrate CSPRINGS_REGISTRATE = CreateRegistrate.create(CreateSprings.MODID);
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Item[] SPRING_TOOLS;

    public CreateSprings(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        ModBlocks.register(modEventBus);
        ModBlockEntities.register();
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);
        CSpringsPartalModels.register();
        ModEntities.register(modEventBus);

        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        MenuInit.MENUS.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModEntities::registerEntityAttributes);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(ViewModificationHandler::onFovUpdate);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            forgeBus.addListener(OverlayHandler::onRenderOverlay);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStart);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStop);
        });

        CSPRINGS_REGISTRATE.registerEventListeners(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SPRING_TOOLS = new Item[]{
                    ModItems.SPRING_BASE.get(),
                    ModItems.SPRING_LAUNCHER.get(),
                    ModItems.SPRING_SAW.get(),
                    ModItems.SPRING_DRILL.get()
            };
        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.PUNCHCARD_MENU.get(), PunchcardScreen::new);
        MenuScreens.register(MenuInit.SPRING_MENU.get(), SpringScreen::new);
        CspringsContraptionTypes.init();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID,  path);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("hi");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            EntityRenderers.register(ModEntities.SPRING_PROJECTILE.get(), SpringProjectileRenderer::new);
            EntityRenderers.register(ModEntities.SPRING_ALLOY_BLOCK_PROJECTILE.get(), SpringAlloyBlockProjectileRenderer::new);
        }

        @SubscribeEvent
        public static void onTooltipRegistration(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpringPoweredCore.SpringSlotTooltipComponent.class, SpringPoweredCore.SpringSlotRenderer::new);
        }
    }
}

