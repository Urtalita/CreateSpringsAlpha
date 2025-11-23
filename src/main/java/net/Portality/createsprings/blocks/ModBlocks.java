package net.Portality.createsprings.blocks;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.blocks.advanced.AndesiteMold.AndesiteMoldBlock;
import net.Portality.createsprings.blocks.advanced.CSpringsDierectionalBlock;
import net.Portality.createsprings.blocks.advanced.ObsidianPlateBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringMovement;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultItem;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlock;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlock;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlock;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceMovement;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockExstentionBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringMovement;
import net.Portality.createsprings.blocks.advanced.test.TestBlock;
import net.Portality.createsprings.client.CSpringsAssetLookup;
import net.Portality.createsprings.client.CSpringsSpriteShifts;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static net.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;
import static net.minecraft.world.level.block.Blocks.*;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateSprings.MODID);

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

    public static final BlockEntry<Block> UNFINISHED_SPRING = CSPRINGS_REGISTRATE
            .block("unfinished_spring", Block::new)
            .initialProperties(() -> IRON_BARS)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.standardModel(c, p)))
            .tag(BlockTags.NEEDS_IRON_TOOL)
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();

    public static final BlockEntry<ObsidianPlateBlock> OBSIDIAN_PLATE = CSPRINGS_REGISTRATE
            .block("obsidian_plate", ObsidianPlateBlock::new)
            .initialProperties(() -> OBSIDIAN)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();

    public static final BlockEntry<CasingBlock> WEATHERED_IRON = CSPRINGS_REGISTRATE
            .block("weathered_iron", CasingBlock::new)
            .transform(BuilderTransformers.layeredCasing(() -> CSpringsSpriteShifts.WEATHERED_IRON_SIDE, () -> CSpringsSpriteShifts.WEATHERED_IRON))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<SlabBlock> OBSIDIAN_SLAB = CSPRINGS_REGISTRATE
            .block("obsidian_slab", SlabBlock::new)
            .initialProperties(SharedProperties::stone)
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

    public static final BlockEntry<LargeSpringBlock> LARGE_SPRING = CSPRINGS_REGISTRATE
            .block("large_spring", LargeSpringBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
            .simpleItem() // Убрать
            .onRegister(movementBehaviour(new LargeSpringMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<CSpringsDierectionalBlock> ANDESITE_MOLD = CSPRINGS_REGISTRATE
            .block("andesite_mold", CSpringsDierectionalBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.noOcclusion())
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .simpleItem()
            .register();

    public static final BlockEntry<LargeSpringBlockExstentionBlock> LARGE_SPRING_EXTENTION = CSPRINGS_REGISTRATE
            .block("large_spring_extention", LargeSpringBlockExstentionBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .tag(AllTags.AllBlockTags.NON_HARVESTABLE.tag)
            .register();

    public static final BlockEntry<SpringCoilBlock> LARGE_SPRING_COIL = CSPRINGS_REGISTRATE
            .block("large_spring_coil", SpringCoilBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
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
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SpringBlock> SPRING = CSPRINGS_REGISTRATE
            .block("spring", SpringBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            .properties(p -> p.noOcclusion())
            .item(SpringItem::new)
            .transform(customItemModel())
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .onRegister(movementBehaviour(new SpringMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<SpringCatapultBlock> SPRING_CATAPULT = CSPRINGS_REGISTRATE
            .block("spring_catapult", SpringCatapultBlock::new)
            .initialProperties(SharedProperties::wooden)
            .transform(axeOrPickaxe())
            .properties(p -> p.noOcclusion())
            .item(CatapultItem::new).build()
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .addLayer(() -> RenderType::cutoutMipped)
            .register();

    public static final BlockEntry<TestBlock> TEST = CSPRINGS_REGISTRATE
            .block("test", TestBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .simpleItem()
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<KineticInterfaceBlock> KINETIC_INTERFACE = CSPRINGS_REGISTRATE
            .block("kinetic_interface", KineticInterfaceBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.noOcclusion())
            .simpleItem()
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .blockstate(BlockStateGen.directionalBlockProvider(false))
            .onRegister(movementBehaviour(new KineticInterfaceMovement()))
            .register();

    public static final BlockEntry<CasingBlock> SPRING_ALLOY_CASING = CSPRINGS_REGISTRATE.block("spring_alloy_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                    .sound(SoundType.COPPER))
            .transform(BuilderTransformers.casing(() -> CSpringsSpriteShifts.SPRING_ALLOY_CASING))
            .register();

    public static void register() {}
}
