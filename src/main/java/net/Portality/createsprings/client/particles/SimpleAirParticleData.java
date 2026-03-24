package net.Portality.createsprings.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class SimpleAirParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SimpleAirParticleData> {
    public static final Codec<SimpleAirParticleData> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                            Codec.INT.fieldOf("x").forGetter(p -> p.posX),
                            Codec.INT.fieldOf("y").forGetter(p -> p.posY),
                            Codec.INT.fieldOf("z").forGetter(p -> p.posZ))
                    .apply(i, SimpleAirParticleData::new));

    public static final ParticleOptions.Deserializer<SimpleAirParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public SimpleAirParticleData fromCommand(ParticleType<SimpleAirParticleData> particleTypeIn, StringReader reader)
                throws CommandSyntaxException {
            reader.expect(' ');
            int x = reader.readInt();
            reader.expect(' ');
            int y = reader.readInt();
            reader.expect(' ');
            int z = reader.readInt();
            return new SimpleAirParticleData(x, y, z);
        }

        public SimpleAirParticleData fromNetwork(ParticleType<SimpleAirParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            return new SimpleAirParticleData(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
    };

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
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(posX);
        buffer.writeInt(posY);
        buffer.writeInt(posZ);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d %d %d", CSpringsParticles.SIMPLE_AIR_PARTICLE.parameter(), posX, posY, posZ);
    }

    @Override
    public ParticleOptions.Deserializer<SimpleAirParticleData> getDeserializer() {
        return DESERIALIZER;
    }

    @Override
    public Codec<SimpleAirParticleData> getCodec(ParticleType<SimpleAirParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<SimpleAirParticleData> getMetaFactory() {
        return SimpleAirParticle.Factory::new;
    }
}
