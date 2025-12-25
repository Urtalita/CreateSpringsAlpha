package net.Portality.createsprings.blocks;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.data.*;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.Spring.SpringItem;
import net.Portality.createsprings.blocks.advanced.AndesiteMold.AndesiteMoldBlock;
import net.Portality.createsprings.blocks.advanced.CSpringsDierectionalBlock;
import net.Portality.createsprings.blocks.advanced.ObsidianPlateBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlock;
import net.Portality.createsprings.blocks.advanced.Spring.SpringMovement;
import net.Portality.createsprings.blocks.advanced.SpringAlloyCasing.SpringAlloyEncasedShaftBlock;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.CatapultItem;
import net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlock;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import net.Portality.createsprings.blocks.advanced.StorageFlywheel.SFlywheelBlock;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlock;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlock;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceMovement;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockExstentionBlock;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringMovement;
import net.Portality.createsprings.blocks.advanced.test.TestBlock;
import net.Portality.createsprings.client.CSpringsAssetLookup;
import net.Portality.createsprings.client.CSpringsSpriteShifts;
import net.Portality.createsprings.config.CSStress;
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
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static net.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;
import static net.minecraft.world.level.block.Blocks.*;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static final BlockEntry<SFlywheelBlock> STORAGE_FLYWHEEL = CSPRINGS_REGISTRATE
            .block("storage_flywheel", SFlywheelBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.axisBlockProvider(true))
            .transform(CSStress.setImpact(16))
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

    //stolen code from create connected

    private static BlockEntry<SpringAlloyEncasedShaftBlock> createShaft(String name, Supplier<Block> casing, CTSpriteShiftEntry sprite){
        return createShaft(AllBlocks.SHAFT,name,casing,sprite,SpringAlloyEncasedShaftBlock::new);
    }

    private static <E extends Block & EncasableBlock, T extends EncasedShaftBlock> BlockEntry<T> createShaft(BlockEntry<E> shaft, String name, Supplier<Block> casing, CTSpriteShiftEntry sprite, NonNullBiFunction<BlockBehaviour.Properties,Supplier<Block>, T> factory){
        String s = shaft.getId().getPath().replace("_shaft","");
        return CreateSprings.CSPRINGS_REGISTRATE.block(name+"_encased"+(shaft.equals(AllBlocks.SHAFT) ? "" : "_"+ s)+"_shaft", p -> factory.apply(p,casing))
                .properties(p -> p.mapColor(MapColor.PODZOL))
                .transform(encasedShaft(shaft,name, () -> sprite))
                .transform(EncasingRegistry.addVariantTo(shaft))
                .transform(axeOrPickaxe())
                .onRegisterAfter(Registries.ITEM, CreateSprings::hideItem)
                .register();
    }

    public static <B extends EncasedShaftBlock, P,E extends Block & EncasableBlock> NonNullUnaryOperator<BlockBuilder<B, P>> encasedShaft(BlockEntry<E> shaft, String casing, Supplier<CTSpriteShiftEntry> casingShift) {
        String sId = shaft.getId().getPath().replace("_shaft","");
        if (shaft.equals(AllBlocks.SHAFT)) sId = "normal";
        String finalSId = sId;
        return builder -> {
            BlockBuilder<B,P> b = encasedBase(builder, shaft::get)
                    .blockstate(encasedShaft( finalSId, casing))
                    .item()
                    .model((ctx, prov) -> prov.getBuilder(ctx.getName()).parent(Objects.requireNonNull(encasedShaftModel(prov, finalSId, casing, true))))
                    .build();
            if (casingShift.get() != null){
                b=b.onRegister(CreateSprings.CSPRINGS_REGISTRATE.connectedTextures(() -> new EncasedCTBehaviour(casingShift.get())))
                        .onRegister(CreateSprings.CSPRINGS_REGISTRATE.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
                                (s, f) -> f.getAxis() != s.getValue(EncasedShaftBlock.AXIS))));
            }
            return b;
        };
    }

    private static <B extends RotatedPillarKineticBlock, P> BlockBuilder<B, P> encasedBase(BlockBuilder<B, P> b,
                                                                                           Supplier<ItemLike> drop) {
        return b.initialProperties(SharedProperties::stone)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .transform(CSStress.setNoImpact())
                .loot((p, lb) -> p.dropOther(lb, drop.get()));
    }

    public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> encasedShaft(String shaft, String casing){
        return (ctx,prov)->axisBlock(ctx,prov, encasedShaftModel(prov,shaft,casing,false));
    }

    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,ModelFile model){
        axisBlock(ctx,prov,model,true);
    }

    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,ModelFile model, boolean uvLock){
        axisBlock(ctx,prov,bs->model,uvLock);
    }
    public static <T extends Block> void axisBlock(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, Function<BlockState,ModelFile> model, boolean uvLock){
        if (model == null) {
            prov.simpleBlock(ctx.get(),new ModelFile.UncheckedModelFile("block/dirt"));
            return;
        }
        prov.getVariantBuilder(ctx.getEntry())
                .forAllStatesExcept(state -> {
                    Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                    return ConfiguredModel.builder()
                            .modelFile(model.apply(state))
                            .uvLock(uvLock)
                            .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                            .rotationY(axis == Direction.Axis.X ? 90 : axis == Direction.Axis.Z ? 180 : 0)
                            .build();
                }, BlockStateProperties.WATERLOGGED);
    }

    public static <T> ModelFile encasedShaftModel(RegistrateProvider p, String shaft, String casing, boolean item){
        if (!item)
            return Objects.requireNonNull(createModelInBlock(p, "encased_shaft/" + casing))
                    .parent(new ModelFile.UncheckedModelFile("create:block/encased_shaft/block"))
                    .texture("casing",getCasingTexture(casing))
                    .texture("opening",getGearboxTexture(casing))
                    .texture("particle",getCasingTexture(casing));
        else
            return Objects.requireNonNull(createModelInBlock(p, "encased_shaft/items/" + shaft + "/" + casing))
                    .parent(new ModelFile.UncheckedModelFile("create:block/encased_shaft/item"))
                    .texture("casing",getCasingTexture(casing))
                    .texture("opening",getGearboxTexture(casing))
                    .texture("1_0",getShaftTexture(shaft))
                    .texture("1_1", getShaftTexture(shaft) + (!shaft.equals("mldeg") ? "_top": ""))
                    .texture("particle",getCasingTexture(casing));
    }

    public static ModelBuilder<? extends ModelBuilder<?>> createModelInBlock(RegistrateProvider p, String path){
        if (p instanceof RegistrateBlockstateProvider provider)
            return provider.models()
                    .getBuilder("block/"+path);
        else if (p instanceof RegistrateItemModelProvider provider)
            return provider.getBuilder("block/"+path);
        return null;
    }

    public static String getCasingTexture(String casing){
        if (casing.equals("normal")) return Create.ID+":block/andesite_casing";
        String modid = getModForCasing(casing);
        if (casing.equals("industrial_iron") || casing.equals("weathered_iron")) return modid + ":block/"+casing+"_block";
        return modid + ":block/"+casing+"_casing";
    }

    public static String getModForCasing(String casing){
        if (casing.equals("brass") || casing.equals("andesite") || casing.equals("copper") || casing.equals("railway") || casing.equals("industrial_iron") || casing.equals("creative") || casing.equals("weathered_iron") || casing.equals("shadow_steel") || casing.equals("refined_radiance")) return Create.ID;
        return CreateSprings.MODID;
    }

    public static String getGearboxTexture(String casing){
        if (casing.equals("andesite") || casing.equals("normal")) return Create.ID+":block/gearbox";
        if (casing.equals("brass")) return Create.ID + ":block/"+casing+"_gearbox";
        return CreateSprings.MODID + ":block/gearboxes/"+casing;
    }

    public static String getShaftTexture(String shaft){
        if (shaft.equals("normal")) return Create.ID + ":block/axis";
        if (shaft.equals("bamboo")) return "minecraft:block/stripped_bamboo_block";
        return CreateSprings.MODID + ":block/shafts/"+shaft;
    }

}
