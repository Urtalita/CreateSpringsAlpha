package com.Portality.createsprings.recipe.Welding;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum WelderRecipeSpeed implements StringRepresentable {
    FAST(4, "fast"),
    NORMAL(3, "normal"),
    SLOW(2, "slow");

    private final int speedValue;
    private final String name;

    WelderRecipeSpeed(int speedValue, String name) {
        this.speedValue = speedValue;
        this.name = name;
    }

    public int getSpeedValue() {
        return speedValue;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    // Передаем Supplier (лямбду) вместо прямого вызова массива
    public static final Codec<WelderRecipeSpeed> CODEC =
            StringRepresentable.fromEnum(() -> WelderRecipeSpeed.values());

    public static final StreamCodec<ByteBuf, WelderRecipeSpeed> STREAM_CODEC =
            ByteBufCodecs.idMapper(index -> WelderRecipeSpeed.values()[index], Enum::ordinal);
}