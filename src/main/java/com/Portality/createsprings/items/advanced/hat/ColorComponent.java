package com.Portality.createsprings.items.advanced.hat;

import com.Portality.createsprings.server.CSpringsDataComponents;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public record ColorComponent(Color color) {

    public static final Codec<ColorComponent> CODEC = Codec.INT.xmap(
            rgb -> new ColorComponent(new Color(rgb)),
            component -> component.color().getRGB()
    );

    public static final StreamCodec<ByteBuf, ColorComponent> STREAM_CODEC = ByteBufCodecs.INT.map(
            rgb -> new ColorComponent(new Color(rgb)),
            component -> component.color().getRGB()
    );

    public static Color getColour(ItemStack stack){
        return (Color) stack.getOrDefault(CSpringsDataComponents.COLOUR, Color.WHITE);
    }
}