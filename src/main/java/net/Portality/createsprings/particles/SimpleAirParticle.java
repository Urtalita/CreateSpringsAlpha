package net.Portality.createsprings.particles;

import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.particle.AirParticle;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class SimpleAirParticle extends SimpleAnimatedParticle {

    protected SimpleAirParticle(ClientLevel p_107647_, double p_107648_, double p_107649_, double p_107650_, SpriteSet p_107651_, float p_107652_) {
        super(p_107647_, p_107648_, p_107649_, p_107650_, p_107651_, p_107652_);
    }

    protected SimpleAirParticle(ClientLevel world, double x, double y, double z,
                              SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
        this.quadSize *= 0.75F;
        this.lifetime = 40;
        hasPhysics = false;
        selectSprite(7);
        Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, random, .25f);
        this.setPos(x + offset.x, y + offset.y, z + offset.z);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        setColor(0xEEEEEE);
        setAlpha(.25f);
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 8));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return null;
    }

    public static class Factory implements ParticleProvider<SimpleAirParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        @Override
        public Particle createParticle(SimpleAirParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            BlockEntity be = worldIn.getBlockEntity(new BlockPos(data.posX, data.posY, data.posZ));
            if (!(be instanceof IAirCurrentSource))
                be = null;
            return new SimpleAirParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
