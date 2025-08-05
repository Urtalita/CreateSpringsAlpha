package net.Portality.createsprings.blocks;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.Items.advanced.frictionWelder.WelderItem;
import net.Portality.createsprings.blocks.advanced.AndesiteMold.AndesiteMoldBlock;
import net.Portality.createsprings.blocks.advanced.AndesiteMold.UnfilledAndesiteMoldBlock;
import net.Portality.createsprings.blocks.advanced.ObsidianPlateBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringMovement;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlock;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlock;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceMovement;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringExstentionBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringMovement;
import net.Portality.createsprings.blocks.advanced.test.TestBlock;
import net.Portality.createsprings.fluid.ModFluids;
import net.Portality.createsprings.utill.CSpringsSpriteShifts;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Properties;
import java.util.function.Supplier;

import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static net.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;
import static net.minecraft.world.level.block.Blocks.*;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateSprings.MODID);

    public static final BlockEntry<Block> SPRING_ALLOY_BLOCK = CSPRINGS_REGISTRATE
            .block("spring_alloy_block", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.sound(SoundType.METAL))
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();

    public static final BlockEntry<Block> UNFINISHED_SPRING = CSPRINGS_REGISTRATE
            .block("unfinished_spring", Block::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.METAL))
            .item((block, properties) -> new BlockItem(block, properties.stacksTo(1)))
            .build()
            .register();

    public static final BlockEntry<ObsidianPlateBlock> OBSIDIAN_PLATE = CSPRINGS_REGISTRATE
            .block("obsidian_plate", ObsidianPlateBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.METAL))
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();

    public static final BlockEntry<SlabBlock> OBSIDIAN_SLAB = CSPRINGS_REGISTRATE
            .block("obsidian_slab", SlabBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.METAL))
            .item((block, properties) -> new BlockItem(block, properties.fireResistant()))
            .build()
            .register();

    public static final BlockEntry<SlabBlock> ANDESITE_MOLD = CSPRINGS_REGISTRATE
            .block("andesite_mold", SlabBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.METAL))
            .simpleItem()
            .register();

    public static final RegistryObject<LiquidBlock> SPRING_ALLOY_FLUID = BLOCKS.register(
            "custom_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE, Block.Properties.copy(Blocks.WATER))
    );

    public static final BlockEntry<LargeSpringExstentionBlock> LARGE_SPRING_EXTENTION = CSPRINGS_REGISTRATE
            .block("large_spring_extention", LargeSpringExstentionBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .tag(AllTags.AllBlockTags.COPYCAT_DENY.tag)
            .tag(AllTags.AllBlockTags.NON_HARVESTABLE.tag)
            .register();

    public static final BlockEntry<AndesiteMoldBlock> FILLED_ANDESITE_MOLD = CSPRINGS_REGISTRATE
            .block("filled_andesite_mold", AndesiteMoldBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.noOcclusion())
            .simpleItem()
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();


    public static final BlockEntry<LargeSpringBlock> LARGE_SPRING = CSPRINGS_REGISTRATE
            .block("large_spring", LargeSpringBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .simpleItem() // Убрать
            .onRegister(movementBehaviour(new LargeSpringMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<SpringCoilBlock> LARGE_SPRING_COIL = CSPRINGS_REGISTRATE
            .block("large_spring_coil", SpringCoilBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .simpleItem()
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<WelderBlock> FRICTION_WELDER = CSPRINGS_REGISTRATE
            .block("friction_welder", WelderBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .item(WelderItem::new)
            .transform(customItemModel())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    public static final BlockEntry<SpringBlock> SPRING = CSPRINGS_REGISTRATE
            .block("spring", SpringBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion())
            .item(SpringItem::new)
            .transform(customItemModel())
            .onRegister(movementBehaviour(new SpringMovement()))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
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
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .onRegister(movementBehaviour(new KineticInterfaceMovement()))
            .register();

    public static final BlockEntry<CasingBlock> SPRING_ALLOY_CASING = CSPRINGS_REGISTRATE.block("spring_alloy_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                    .sound(SoundType.COPPER))
            .transform(BuilderTransformers.casing(() -> CSpringsSpriteShifts.SPRING_ALLOY_CASING))
            .register();

    public static final BlockEntry<CasingBlock> WEATHERED_IRON = CSPRINGS_REGISTRATE
            .block("weathered_iron", CasingBlock::new)
            .transform(BuilderTransformers.layeredCasing(() -> CSpringsSpriteShifts.WEATHERED_IRON_SIDE, () -> CSpringsSpriteShifts.WEATHERED_IRON))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN).sound(SoundType.NETHERITE_BLOCK))
            .register();

    public static void register() {}
}
