package com.Portality.createsprings.recipe.Casting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class CastingRecipeParams extends ProcessingRecipeParams {
    public static MapCodec<CastingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(CastingRecipeParams::new).forGetter(Function.identity())
    ).apply(instance, (params) -> {
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, CastingRecipeParams> STREAM_CODEC = streamCodec(CastingRecipeParams::new);

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
    }
}
