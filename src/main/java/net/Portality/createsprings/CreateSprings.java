package net.Portality.createsprings;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.renderer.SpringAlloyBlockProjectileRenderer;
import net.Portality.createsprings.Entities.renderer.SpringProjectileRenderer;
import net.Portality.createsprings.Items.ModItemColors;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardInterpritator;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.MouseSensitivityHandler;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.OverlayHandler;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.ViewModificationHandler;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.Items.advanced.hat.HatItem;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.blocks.advanced.Spring.ISpringBE;
import net.Portality.createsprings.blocks.advanced.Spring.ISpringBlock;
import net.Portality.createsprings.contraption.CspringsContraptionTypes;
import net.Portality.createsprings.datagen.CSpringsDatagen;
import net.Portality.createsprings.fluid.CSpringsFluids;
import net.Portality.createsprings.menus.MenuInit;
import net.Portality.createsprings.menus.ModCreativeModeTabs;
import net.Portality.createsprings.menus.Spring.SpringScreen;
import net.Portality.createsprings.particles.CSpringsParticles;
import net.Portality.createsprings.ponders.CSpringsPonderPlugin;
import net.Portality.createsprings.recipe.ModRecipes;
import net.Portality.createsprings.recipe.NbtShapelessRecipe.NbtAwareShapelessRecipe;
import net.Portality.createsprings.recipe.NbtShapelessRecipe.NbtHatShapelessRecipe;
import net.Portality.createsprings.server.CSpringsPackets;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateSprings.MODID)
public class CreateSprings {

    public static final String MODID = "createsprings";
    public static final CreateRegistrate CSPRINGS_REGISTRATE = CreateRegistrate.create(CreateSprings.MODID);
    public static Item[] SPRING_TOOLS;

    public CreateSprings() {
        this(FMLJavaModLoadingContext.get());
    }

    public CreateSprings(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        //modEventBus.addListener(this::test);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);
        CSpringsPartalModels.register();
        ModEntities.register(modEventBus);
        CSpringsParticles.register(modEventBus);
        CSpringsPackets.registerPackets();

        //ModFluids.FLUID_TYPES.register(modEventBus);
        MenuInit.MENUS.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModEntities::registerEntityAttributes);
        modEventBus.addListener(CreateSprings::onRegister);
        modEventBus.addListener(EventPriority.LOWEST, CSpringsDatagen::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(ViewModificationHandler::onFovUpdate);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            forgeBus.addListener(OverlayHandler::onRenderOverlay);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStart);
            forgeBus.addListener(MouseSensitivityHandler::onItemUseStop);
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "create_springs-common.toml");

        CSPRINGS_REGISTRATE.registerEventListeners(modEventBus);

        CSpringsFluids.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CSpringsClient.onCtorClient(modEventBus, forgeBus));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SPRING_TOOLS = new Item[]{
                    ModItems.SPRING_BASE.get(),
                    ModItems.SPRING_LAUNCHER.get(),
                    ModItems.SPRING_SAW.get(),
                    ModItems.SPRING_DRILL.get(),
                    ModItems.SPRING_SHOVE.get(),
                    ModItems.EXPLOSION_CHAMBER.get(),
                    ModItems.PORTATIVE_STEAM_ENGINE.get(),
                    ModItems.SPRING_FAN.get()
            };
        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.SPRING_MENU.get(), SpringScreen::new);
    }

    public static void onRegister(final RegisterEvent event) {
        CspringsContraptionTypes.init();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID,  path);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModItemColors.register();
            EntityRenderers.register(ModEntities.SPRING_PROJECTILE.get(), SpringProjectileRenderer::new);
            EntityRenderers.register(ModEntities.SPRING_ALLOY_BLOCK_PROJECTILE.get(), SpringAlloyBlockProjectileRenderer::new);

            PonderIndex.addPlugin(new CSpringsPonderPlugin());
        }

        @SubscribeEvent
        public static void onTooltipRegistration(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpringPoweredCore.SpringSlotTooltipComponent.class, SpringPoweredCore.SpringSlotRenderer::new);
            event.register(HatItem.HatSlotTooltipComponent.class, HatItem.HatSlotRenderer::new);
        }

        @SubscribeEvent
        public static void registerSerializers(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
                helper.register(new ResourceLocation(CreateSprings.MODID, "nbt_aware_shapeless"), NbtAwareShapelessRecipe.Serializer.INSTANCE);
                helper.register(new ResourceLocation(CreateSprings.MODID, "nbt_hat_shapeless"), NbtHatShapelessRecipe.Serializer.INSTANCE);
            });
        }

        @SubscribeEvent
        public static void onModelRegistry(ModelEvent.RegisterAdditional event) {
            event.register(CSpringsPartalModels.HAT.modelLocation());
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event){
            event.enqueueWork(() -> {
               //NetworkHandler.register();
               PunchcardInterpritator.registerActions();
            });
        }
    }

    @SubscribeEvent
    public void onExplosionStart(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        Level level = event.getLevel();
        BlockPos pos = BlockPos.containing(explosion.getPosition());

        int radius = 3;
        int radiusSquared = radius * radius; // pre-calculated
        int distanceSquared;

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

