package net.Portality.createsprings.blocks;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.Items.advanced.frictionWelder.WelderItem;
import net.Portality.createsprings.blocks.advanced.AndesiteMoldBlock;
import net.Portality.createsprings.blocks.advanced.ObsidianPlateBlock;
import net.Portality.createsprings.blocks.advanced.ObsidianSlabBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlock;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlock;
import net.Portality.createsprings.fluid.ModFluids;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateSprings.MODID);

    public static final RegistryObject<Block> SPRING_ALLOY_BLOCK = registerBlock("spring_alloy_block",
            () -> new Block(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK).sound(SoundType.METAL).destroyTime(2)), "fireResistant");

    public static final RegistryObject<Block> UNFINISHED_SPRING = registerBlock("unfinished_spring",
            () -> new Block(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK).sound(SoundType.METAL).destroyTime(1).noOcclusion()), "StacksTo1");

    public static final RegistryObject<Block> OBSIDIAN_PLATE = registerBlock("obsidian_plate",
            () -> new ObsidianPlateBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).noOcclusion()), "fireResistant");

    public static final RegistryObject<Block> OBSIDIAN_SLAB = registerBlock("obsidian_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)), "fireResistant");

    public static final RegistryObject<Block> ANDESITE_MOLD = registerBlock("andesite_mold",
            () -> new AndesiteMoldBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).noOcclusion()), "");

    public static final RegistryObject<Block> FILLED_ANDESITE_MOLD = registerBlock("filled_andesite_mold",
            () -> new AndesiteMoldBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)), "");

    /*public static final RegistryObject<Block> LARGE_SPRING_COIL = registerBlock("large_spring_coil",
            () -> new SpringCoilBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()
                    ,new Vec3(4, 4, 0),
                    new Vec3(12, 12, 16))
            , "fireResistant");

     */

    public static final RegistryObject<LiquidBlock> SPRING_ALLOY_FLUID = BLOCKS.register(
            "custom_fluid_block",
            () -> new LiquidBlock(ModFluids.SOURCE, Block.Properties.copy(Blocks.WATER))
    );

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
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .register();

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, String ItemProperty) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, ItemProperty);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, String ItemProperty) {
        if(ItemProperty.isEmpty()){
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        } else if(ItemProperty.equals("fireResistant")){
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().fireResistant()));
        } else if(ItemProperty.equals("StacksTo1")){
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
        } else if (ItemProperty.equals("fireResistant, StacksTo1")){
            ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1).fireResistant()));
        }
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
