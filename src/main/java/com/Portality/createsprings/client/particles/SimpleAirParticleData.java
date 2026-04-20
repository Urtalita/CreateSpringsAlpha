package com.Portality.createsprings.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Locale;

public class SimpleAirParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SimpleAirParticleData> {
    public static final MapCodec<SimpleAirParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                            Codec.INT.fieldOf("x").forGetter(p -> p.posX),
                            Codec.INT.fieldOf("y").forGetter(p -> p.posY),
                            Codec.INT.fieldOf("z").forGetter(p -> p.posZ))
                    .apply(i, SimpleAirParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleAirParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.posX,
            ByteBufCodecs.INT, p -> p.posY,
            ByteBufCodecs.INT, p -> p.posZ,
            SimpleAirParticleData::new
    );

    final int posX;
    final int posY;
    final int posZ;

    public SimpleAirParticleData(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public SimpleAirParticleData(int posX, int posY, int posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public SimpleAirParticleData() {
        this(0, 0, 0);
    }

    @Override
    public ParticleType<?> getType() {
        return CSpringsParticles.SIMPLE_AIR_PARTICLE.get();
    }

    @Override
    public ParticleEngine.SpriteParticleRegistration<SimpleAirParticleData> getMetaFactory() {
        return SimpleAirParticle.Factory::new;
    }

    @Override
    public MapCodec<SimpleAirParticleData> getCodec(ParticleType<SimpleAirParticleData> type) {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SimpleAirParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }
}
