package com.Portality.createsprings.blocks;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.advanced.AndesiteMold.AndesiteMoldBlock;
import com.Portality.createsprings.blocks.advanced.CSpringsDierectionalBlock;
import com.Portality.createsprings.blocks.advanced.ObsidianPlateBlock;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultItem;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlock;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import com.Portality.createsprings.blocks.advanced.friction_welder.WelderBlock;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlock;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceMovement;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockExstentionBlock;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringMovement;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlock;
import com.Portality.createsprings.client.CSpringsAssetLookup;
import com.Portality.createsprings.client.CSpringsSpriteShifts;
import com.Portality.createsprings.config.CSStress;
import com.Portality.createsprings.items.advanced.Spring.SpringItem;
import com.simibubi.create.*;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import static com.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;
import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static net.minecraft.world.level.block.Blocks.*;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    static {
        CSPRINGS_REGISTRATE.setCreativeTab(CreateSprings.MAIN_TAB);
    }

    private static final CreateRegistrate REGISTRATE = CreateSprings.registrate();

    public static final BlockEntry<Block> SPRING_ALLOY_BLOCK = CSPRINGS_REGISTRATE
            .block("spring_alloy_block", Block::new)
            .initialProperties(() -> GOLD_BLOCK)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("spring_alloy_block"))
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .tag(BlockTags.BEACON_BASE_BLOCKS)
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .lang("Block of Spring Alloy")
            .register();



    public static final BlockEntry<CSpringsDierectionalBlock> UNFINISHED_SPRING = CSPRINGS_REGISTRATE
            .block("unfinished_spring", CSpringsDierectionalBlock::new)
            .initialProperties(() -> IRON_BARS)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();


    public static final BlockEntry<ObsidianPlateBlock> OBSIDIAN_PLATE = CSPRINGS_REGISTRATE
            .block("obsidian_plate", ObsidianPlateBlock::new)
            .initialProperties(() -> OBSIDIAN)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();



    public static final BlockEntry<CasingBlock> WEATHERED_IRON = CSPRINGS_REGISTRATE
            .block("connectable_weathered_iron", CasingBlock::new)
            .transform(BuilderTransformers.layeredCasing(() -> CSpringsSpriteShifts.WEATHERED_IRON_SIDE, () -> CSpringsSpriteShifts.WEATHERED_IRON))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<SlabBlock> OBSIDIAN_SLAB = CSPRINGS_REGISTRATE
            .block("obsidian_slab", SlabBlock::new)
            .initialProperties(() -> OBSIDIAN)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.slabBlock(c.get(), CSpringsAssetLookup.customBlockModel(c.getName() + "/" + c.getName() ,p),
                    CSpringsAssetLookup.customBlockModel(c.getName() + "/" + c.getName() + "_up" ,p),
                    CSpringsAssetLookup.customBlockModel(c.getName() + "/" + c.getName() + "_double" ,p)))
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();

    public static final BlockEntry<AndesiteMoldBlock> FILLED_ANDESITE_MOLD = CSPRINGS_REGISTRATE
            .block("filled_andesite_mold", AndesiteMoldBlock::new)
            .initialProperties(SharedProperties::wooden)
            .transform(axeOnly())
            .properties(p -> p.noOcclusion())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .simpleItem()
            .register();

    public static final BlockEntry<CSpringsDierectionalBlock> ANDESITE_MOLD = CSPRINGS_REGISTRATE
            .block("andesite_mold", CSpringsDierectionalBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.noOcclusion())
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .simpleItem()
            .register();

    public static final BlockEntry<LargeSpringBlock> LARGE_SPRING = CSPRINGS_REGISTRATE
            .block("large_spring", LargeSpringBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
            .onRegister(movementBehaviour(new LargeSpringMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            //.transform(displaySource(CSpringsDisplaySources.LARGE_CHARGE))
            .register();

    public static final BlockEntry<LargeSpringBlockExstentionBlock> LARGE_SPRING_EXTENTION = CSPRINGS_REGISTRATE
            .block("large_spring_extention", LargeSpringBlockExstentionBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .tag(AllTags.AllBlockTags.NON_HARVESTABLE.tag)
            //.transform(displaySource(CSpringsDisplaySources.LARGE_CHARGE))
            .register();

    public static final BlockEntry<SpringCoilBlock> LARGE_SPRING_COIL = CSPRINGS_REGISTRATE
            .block("large_spring_coil", SpringCoilBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.forceSolidOn())
            .transform(pickaxeOnly())
            .transform(CSStress.setNoImpact())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .simpleItem()
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<WelderBlock> FRICTION_WELDER = CSPRINGS_REGISTRATE
            .block("friction_welder", WelderBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .transform(CSStress.setImpact(4))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();


    public static final BlockEntry<SpringBlock> SPRING = CSPRINGS_REGISTRATE
            .block("spring", SpringBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            .properties(p -> p.noOcclusion())
            //.transform(displaySource(CSpringsDisplaySources.CHARGE))
            .item(SpringItem::new)
            .transform(customItemModel())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            //.onRegister(movementBehaviour(new SpringMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();


    public static final BlockEntry<SpringCatapultBlock> SPRING_CATAPULT = CSPRINGS_REGISTRATE
            .block("spring_catapult", SpringCatapultBlock::new)
            .initialProperties(SharedProperties::wooden)
            .transform(axeOrPickaxe())
            .properties(p -> p.noOcclusion())
            .item(CatapultItem::new)
            .transform(customItemModel())
            .transform(CSStress.setImpact(2))
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStates(s -> ConfiguredModel.builder()
                            .modelFile(AssetLookup.partialBaseModel(c, p))
                            .rotationX(s.getValue(SpringCatapultBlock.CEILING) ? 180 : 0)
                            .build()))
            .addLayer(() -> RenderType::cutoutMipped)
            .register();
    /*

    public static final BlockEntry<TestBlock> TEST = CSPRINGS_REGISTRATE
            .block("test", TestBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .simpleItem()
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

     */

    public static final BlockEntry<KineticInterfaceBlock> KINETIC_INTERFACE = CSPRINGS_REGISTRATE
            .block("kinetic_interface", KineticInterfaceBlock::new)
            .initialProperties(SharedProperties::wooden)
            .transform(axeOrPickaxe())
            .properties(p -> p.noOcclusion())
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .onRegister(movementBehaviour(new KineticInterfaceMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item().transform(customItemModel())
            .register();


    public static final BlockEntry<CasingBlock> SPRING_ALLOY_CASING = CSPRINGS_REGISTRATE.block("spring_alloy_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                    .sound(SoundType.COPPER))
            .transform(BuilderTransformers.casing(() -> CSpringsSpriteShifts.SPRING_ALLOY_CASING))
            .register();





    //public static final BlockEntry<SpringAlloyEncasedShaftBlock> SPRING_ALLOY_ENCASED_SHAFT = createShaft("spring_alloy",ModBlocks.SPRING_ALLOY_CASING::get,CSpringsSpriteShifts.SPRING_ALLOY_CASING);



    public static void register() {}
}
