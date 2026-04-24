package com.Portality.createsprings.server;

import com.Portality.createsprings.CreateSprings;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

public class CSpringsDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateSprings.ID);

    public static final DataComponentType<Integer> SPRING_AMOUNT = register(
            "springs_rn",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<CompoundTag> MODIFIERS = register(
            "contains",
            builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
    );

    public static final DataComponentType<List<Float>> STORED_LIST = register(
            "stored",
            builder -> builder
                    .persistent(Codec.FLOAT.listOf())
                    .networkSynchronized(ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()))
    );

    public static final DataComponentType<Float> TOOL_SPEED = register(
            "speed",
            builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT)
    );

    public static final DataComponentType<Float> TOOL_LAST_SPEED = register(
            "last_speed",
            builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT)
    );

    public static final DataComponentType<CompoundTag> PUNCHCARD = register(
            "punchcard",
            builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
    );

    public static final DataComponentType<Boolean> IS_USING = register(
            "is_using",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void registerAllComponents(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
