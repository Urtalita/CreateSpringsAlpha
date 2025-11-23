package net.Portality.createsprings.fluid;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.Portality.createsprings.CreateSprings;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class CSpringsFluids {
    /*
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SPRING_ALLOY =
            CreateSprings.CSPRINGS_REGISTRATE.standardFluid("molten_spring_alloy",
                            CSpringsSolidRenderedPlaceableFluidType.create(0xEAAE2F,
                                    () -> 1f / 8f * AllConfigs.client().honeyTransparencyMultiplier.getF()))
                    .lang("Molten Spring Alloy")
                    .properties(b -> b.viscosity(2000)
                            .density(1400))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(ForgeFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE))
                    .build()
                    .bucket()
                    .build()
                    .register();

     */
/*
    public static FluidEntry<ForgeFlowingFluid.Flowing> SPRING_ALLOY =
            CreateSprings.CSPRINGS_REGISTRATE.fluid("molten_spring_alloy", new ResourceLocation("createsprings","fluid/molten_spring_alloy_still"),
                    new ResourceLocation("createsprings","fluid/molten_spring_alloy_flow"),
                    NoColorFluidAttributes::new)
            .properties(b -> b.viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(15)
                    .slopeFindDistance(6)
                    .explosionResistance(100f))
            .source(ForgeFlowingFluid.Source::new).register();

 */

    public static final FluidEntry<ForgeFlowingFluid.Flowing> SPRING_ALLOY =
            CreateSprings.CSPRINGS_REGISTRATE.standardFluid("molten_spring_alloy",
                            NoColorFluidAttributes::new)
                    .lang("Molten Spring Alloy")
                    .properties(b -> b.viscosity(2000)
                            .density(1400))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(ForgeFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
                    .build()
                    .bucket()
                    .onRegister(CSpringsFluids::registerFluidDispenseBehavior)
                    .build()
                    .register();


    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
    private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior(){
        @Override
        protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
            DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
            BlockPos pos = pSource.getPos().relative(pSource.getBlockState().getValue(DispenserBlock.FACING));
            Level level = pSource.getLevel();
            if (dispensibleContainerItem.emptyContents(null, level, pos, null, pStack)) {
                return new ItemStack(Items.BUCKET);
            }
            return DEFAULT.dispense(pSource, pStack);
        }
    };


    private static void registerFluidDispenseBehavior(BucketItem bucket) {
        DispenserBlock.registerBehavior(bucket, DISPENSE_FLUID);
    }

    public static void register() {
        /*
        var alloy = CreateSprings.CSPRINGS_REGISTRATE.fluid("molten_spring_alloy", new ResourceLocation("createsprings","fluid/molten_spring_alloy_still"),
                        new ResourceLocation("createsprings","fluid/molten_spring_alloy_flow"),
                        NoColorFluidAttributes::new)
                .properties(b -> b.viscosity(2000)
                        .density(1400))
                .fluidProperties(p -> p.levelDecreasePerBlock(2)
                        .tickRate(15)
                        .slopeFindDistance(6)
                        .explosionResistance(100f))
                .source(ForgeFlowingFluid.Source::new);

        var alloyBucket = alloy.bucket()
                .properties(p -> p.stacksTo(1))
                .register();
        SPRING_ALLOY = alloy.register();

         */
    }

    public static void registerFluidInteractions() {
        FluidInteractionRegistry.addInteraction(ForgeMod.WATER_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                SPRING_ALLOY.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.SMOOTH_STONE.defaultBlockState();
                    } else {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    }
                }
        ));
    }

    private static class NoColorFluidAttributes extends AllFluids.TintedFluidType {

        public NoColorFluidAttributes(Properties properties, ResourceLocation stillTexture,
                                      ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return NO_TINT;
        }

        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 0x00ffffff;
        }

    }
}
