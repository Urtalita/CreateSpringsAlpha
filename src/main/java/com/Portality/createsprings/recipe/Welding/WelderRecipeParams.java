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
            ItemStack.CODEC.fieldOf("first_block").forGetter(WelderRecipeParams::getFirstBlock),
            ItemStack.CODEC.fieldOf("second_block").forGetter(WelderRecipeParams::getSecondBlock),
            ItemStack.CODEC.fieldOf("result").forGetter(WelderRecipeParams::getResult),
            WelderRecipeSpeed.CODEC.fieldOf("speed").forGetter(WelderRecipeParams::getSpeed)
    ).apply(instance, (params, firstBlock, secondBlock, result, speed) -> {
        params.firstBlock = firstBlock;
        params.secondBlock = secondBlock;
        params.result = result;
        params.speed = speed;
        return params;
    }));

    public static StreamCodec<RegistryFriendlyByteBuf, WelderRecipeParams> STREAM_CODEC = streamCodec(WelderRecipeParams::new);

    public ItemStack getFirstBlock() {
        return firstBlock;
    }

    public ItemStack getSecondBlock() {
        return secondBlock;
    }

    public ItemStack getResult() {
        return result;
    }

    public WelderRecipeSpeed getSpeed() {
        return speed;
    }

    public ItemStack firstBlock = ItemStack.EMPTY;
    public ItemStack secondBlock = ItemStack.EMPTY;
    public ItemStack result = ItemStack.EMPTY;
    public WelderRecipeSpeed speed = WelderRecipeSpeed.NORMAL;

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        this.firstBlock = ItemStack.STREAM_CODEC.decode(buffer);
        this.secondBlock = ItemStack.STREAM_CODEC.decode(buffer);
        this.result = ItemStack.STREAM_CODEC.decode(buffer);
        this.speed = WelderRecipeSpeed.STREAM_CODEC.decode(buffer);
    }
}
