package com.Portality.createsprings.blocks.advanced;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.blocks.advanced.AndesiteMold.MoldBlockEntity;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlockEntity;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultRenderer;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultVisual;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlockEntity;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilRenderer;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilVisual;
import com.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import com.Portality.createsprings.blocks.advanced.friction_welder.WelderRenderer;
import com.Portality.createsprings.blocks.advanced.friction_welder.WelderVisual;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlockEntity;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceRenderer;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceVisual;
import com.Portality.createsprings.blocks.advanced.largeSpring.ExtentionBlockEntity;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringRenderer;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringVisual;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import com.Portality.createsprings.blocks.advanced.spring.SpringRenderer;
import com.Portality.createsprings.blocks.advanced.spring.SpringVisual;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;

public class ModBlockEntities {

    private static final CreateRegistrate REGISTRATE = CreateSprings.registrate();

    public static final BlockEntityEntry<KineticInterfaceBlockEntity> KINETIC_INTERFACE = CSPRINGS_REGISTRATE
            .blockEntity("kinetic_interface", KineticInterfaceBlockEntity::new)
            .visual(() -> KineticInterfaceVisual::new)
            .renderer(() -> KineticInterfaceRenderer::new)
            .validBlocks(ModBlocks.KINETIC_INTERFACE)
            .register();

    public static final BlockEntityEntry<LargeSpringBlockEntity> LARGE_SPRING = CSPRINGS_REGISTRATE
            .blockEntity("large_spring", LargeSpringBlockEntity::new)
            .visual(() -> LargeSpringVisual::new)
            .renderer(() -> LargeSpringRenderer::new)
            .validBlocks(ModBlocks.LARGE_SPRING)
            .register();

    public static final BlockEntityEntry<MoldBlockEntity> MOLD = CSPRINGS_REGISTRATE
            .blockEntity("mold", MoldBlockEntity::new)
            .validBlocks(ModBlocks.FILLED_ANDESITE_MOLD)
            .register();

    public static final BlockEntityEntry<SpringBlockEntity> SPRING = REGISTRATE
            .blockEntity("spring", SpringBlockEntity::new)
            .visual(() -> SpringVisual::new, false)
            .validBlocks(ModBlocks.SPRING)
            .renderer(() -> SpringRenderer::new)
            .register();

    public static final BlockEntityEntry<SpringCatapultBlockEntity> SPRING_CATAPULT = CSPRINGS_REGISTRATE
            .blockEntity("spring_catapult", SpringCatapultBlockEntity::new)
            .visual(() -> SpringCatapultVisual::new)
            .validBlocks(ModBlocks.SPRING_CATAPULT)
            .renderer(() -> SpringCatapultRenderer::new)
            .register();


    public static final BlockEntityEntry<WelderBlockEntity> FRICTION_WELDER = REGISTRATE
            .blockEntity("friction_welder", WelderBlockEntity::new)
            .visual(() -> WelderVisual::new)
            .validBlocks(ModBlocks.FRICTION_WELDER)
            .renderer(() -> WelderRenderer::new)
            .register();


    public static final BlockEntityEntry<SpringCoilBlockEntity> LARGE_SPRING_COIL = REGISTRATE
            .blockEntity("large_spring_coil", SpringCoilBlockEntity::new)
            .visual(() -> SpringCoilVisual::new, false)
            .renderer(() -> SpringCoilRenderer::new)
            .validBlocks(ModBlocks.LARGE_SPRING_COIL)
            .register();

     /*

    public static final BlockEntityEntry<TestBlockEntity> TEST = CSPRINGS_REGISTRATE
            .blockEntity("test", TestBlockEntity::new)
            .visual(() -> TestVisual::new)
            .validBlocks(ModBlocks.TEST)
            .register();

      */

    public static final BlockEntityEntry<ExtentionBlockEntity> EXTENTION_BLOCK_ENTITY = CSPRINGS_REGISTRATE
            .blockEntity("extention_block_entity", ExtentionBlockEntity::new)
            .validBlocks(ModBlocks.LARGE_SPRING_EXTENTION)
            .register();


    public static void register() {}
}
