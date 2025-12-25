package net.Portality.createsprings.blocks.advanced;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.AndesiteMold.MoldBlockEntity;
import net.Portality.createsprings.blocks.advanced.AndesiteMold.MoldVisual;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.Spring.SpringRenderer;
import net.Portality.createsprings.blocks.advanced.Spring.SpringVisual;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlockEntity;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultRenderer;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultVisual;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlockEntity;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilRenderer;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilVisual;
import net.Portality.createsprings.blocks.advanced.StorageFlywheel.SFlywheelBE;
import net.Portality.createsprings.blocks.advanced.StorageFlywheel.SFlywheelRenderer;
import net.Portality.createsprings.blocks.advanced.StorageFlywheel.SFlywheelVisual;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderRenderer;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderVisual;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlockEntity;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceRenderer;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceVisual;
import net.Portality.createsprings.blocks.advanced.largeSpring.ExtentionBlockEntity;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringRenderer;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringVisual;
import net.Portality.createsprings.blocks.advanced.test.TestBlockEntity;
import net.Portality.createsprings.blocks.advanced.test.TestVisual;

import static net.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;

public class ModBlockEntities {

    public static final BlockEntityEntry<KineticInterfaceBlockEntity> KINETIC_INTERFACE = CSPRINGS_REGISTRATE
            .blockEntity("kinetic_interface", KineticInterfaceBlockEntity::new)
            .visual(() -> KineticInterfaceVisual::new)
            .renderer(() -> KineticInterfaceRenderer::new)
            .validBlocks(ModBlocks.KINETIC_INTERFACE)
            .register();

    public static final BlockEntityEntry<MoldBlockEntity> MOLD = CSPRINGS_REGISTRATE
            .blockEntity("mold", MoldBlockEntity::new)
            .visual(() -> MoldVisual::new)
            .validBlocks(ModBlocks.FILLED_ANDESITE_MOLD)
            .register();

    public static final BlockEntityEntry<LargeSpringBlockEntity> LARGE_SPRING = CSPRINGS_REGISTRATE
            .blockEntity("large_spring", LargeSpringBlockEntity::new)
            .visual(() -> LargeSpringVisual::new)
            .renderer(() -> LargeSpringRenderer::new)
            .validBlocks(ModBlocks.LARGE_SPRING)
            .register();

    public static final BlockEntityEntry<SpringBlockEntity> SPRING = CSPRINGS_REGISTRATE
            .blockEntity("spring", SpringBlockEntity::new)
            .visual(() -> SpringVisual::new, false)
            .renderer(() -> SpringRenderer::new)
            .validBlocks(ModBlocks.SPRING)
            .register();

    public static final BlockEntityEntry<SpringCatapultBlockEntity> SPRING_CATAPULT = CSPRINGS_REGISTRATE
            .blockEntity("spring_catapult", SpringCatapultBlockEntity::new)
            .visual(() -> SpringCatapultVisual::new)
            .validBlocks(ModBlocks.SPRING_CATAPULT)
            .renderer(() -> SpringCatapultRenderer::new)
            .register();

    public static final BlockEntityEntry<WelderBlockEntity> FRICTION_WELDER = CSPRINGS_REGISTRATE
            .blockEntity("friction_welder", WelderBlockEntity::new)
            .visual(() -> WelderVisual::new)
            .validBlocks(ModBlocks.FRICTION_WELDER)
            .renderer(() -> WelderRenderer::new)
            .register();

    public static final BlockEntityEntry<SFlywheelBE> STORAGE_FLYWHEEL = CSPRINGS_REGISTRATE
            .blockEntity("storage_flywheel", SFlywheelBE::new)
            .visual(() -> SFlywheelVisual::new, false)
            .validBlocks(ModBlocks.STORAGE_FLYWHEEL)
            .renderer(() -> SFlywheelRenderer::new)
            .register();

    public static final BlockEntityEntry<SpringCoilBlockEntity> LARGE_SPRING_COIL = CSPRINGS_REGISTRATE
            .blockEntity("large_spring_coil", SpringCoilBlockEntity::new)
            .visual(() -> SpringCoilVisual::new, false)
            .renderer(() -> SpringCoilRenderer::new)
            .validBlocks(ModBlocks.LARGE_SPRING_COIL)
            .register();

    public static final BlockEntityEntry<TestBlockEntity> TEST = CSPRINGS_REGISTRATE
            .blockEntity("test", TestBlockEntity::new)
            .visual(() -> TestVisual::new)
            .validBlocks(ModBlocks.TEST)
            .register();

    public static final BlockEntityEntry<ExtentionBlockEntity> EXTENTION_BLOCK_ENTITY = CSPRINGS_REGISTRATE
            .blockEntity("extention_block_entity", ExtentionBlockEntity::new)
            .validBlocks(ModBlocks.LARGE_SPRING_EXTENTION)
            .register();

    /*
    public static final BlockEntityEntry<KineticBlockEntity> ENCASED_SHAFT = CSPRINGS_REGISTRATE
            .blockEntity("casing_encased_shaft", KineticBlockEntity::new)
            .visual(() -> ShaftVisual::new, false)
            .validBlocks(ModBlocks.SPRING_ALLOY_ENCASED_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();

     */


    public static void register() {}
}
