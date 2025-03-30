package net.Portality.createsprings.fluid;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, CreateSprings.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, CreateSprings.MODID);

    // Тип жидкости
    public static final RegistryObject<FluidType> SPRING_ALLOY_TYPE = FLUID_TYPES.register(
            "spring_alloy_fluid",
            SpringAlloyType::new
    );

    // Источник и текущая версия
    public static final RegistryObject<FlowingFluid> SOURCE = FLUIDS.register(
            "spring_alloy_fluid",
            () -> new ForgeFlowingFluid.Source(ModFluids.PROPERTIES)
    );

    public static final RegistryObject<FlowingFluid> FLOWING = FLUIDS.register(
            "spring_alloy_flowing",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.PROPERTIES)
    );

    // Настройки жидкости
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            SPRING_ALLOY_TYPE,
            SOURCE,
            FLOWING
    )
            .bucket(ModItems.SPRING_ALLOY_BUCKET) // Ведро
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .explosionResistance(100F)
            .block(ModBlocks.SPRING_ALLOY_FLUID); // Блок
}
