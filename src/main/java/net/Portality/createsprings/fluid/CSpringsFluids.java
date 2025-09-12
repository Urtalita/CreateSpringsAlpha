package net.Portality.createsprings.fluid;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.Portality.createsprings.CreateSprings;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
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

    public static FluidEntry<ForgeFlowingFluid.Flowing> SPRING_ALLOY;


    public static void register() {
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
