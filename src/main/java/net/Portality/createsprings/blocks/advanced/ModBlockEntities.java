package net.Portality.createsprings.blocks.advanced;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.Spring.SpringRenderer;
import net.Portality.createsprings.blocks.advanced.Spring.SpringVisual;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderRenderer;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderVisual;

import static net.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;

public class ModBlockEntities {

    public static final BlockEntityEntry<SpringBlockEntity> SPRING = CSPRINGS_REGISTRATE
            .blockEntity("spring", SpringBlockEntity::new)
            .visual(() -> SpringVisual::new, false)
            .validBlocks(ModBlocks.SPRING)
            .renderer(() -> SpringRenderer::new)
            .register();

    public static final BlockEntityEntry<WelderBlockEntity> FRICTION_WELDER = CSPRINGS_REGISTRATE
            .blockEntity("mechanical_bearing", WelderBlockEntity::new)
            .visual(() -> WelderVisual::new)
            .validBlocks(ModBlocks.FRICTION_WELDER)
            .register();


    public static void register() {}
}
