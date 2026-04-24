package com.Portality.createsprings.recipe.Welding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

public class WelderRecipeParams extends ProcessingRecipeParams {
    public static final MapCodec<WelderRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(WelderRecipeParams::new).forGetter(Function.identity()),
            WelderRecipeSpeed.CODEC.fieldOf("speed").forGetter(WelderRecipeParams::getSpeed)
    ).apply(instance, (params, speed) -> {
        params.speed = speed;
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, WelderRecipeParams> STREAM_CODEC = StreamCodec.composite(
            streamCodec(WelderRecipeParams::new), Function.identity(),
            WelderRecipeSpeed.STREAM_CODEC, p -> p.speed,
            (params, spd) -> {
                params.speed = spd;
                return params;
            }
    );

    public WelderRecipeSpeed getSpeed() {
        return speed;
    }

    public WelderRecipeSpeed speed = WelderRecipeSpeed.NORMAL;
}
