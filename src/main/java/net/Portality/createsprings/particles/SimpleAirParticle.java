package net.Portality.createsprings.particles;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.foundation.particle.AirParticle;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class SimpleAirParticle extends SimpleAnimatedParticle {

    protected SimpleAirParticle(ClientLevel p_107647_, double p_107648_, double p_107649_, double p_107650_, SpriteSet p_107651_, float p_107652_) {
        super(p_107647_, p_107648_, p_107649_, p_107650_, p_107651_, p_107652_);
    }

    protected SimpleAirParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprite) {
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
        this.gravity = 0;
        setParticleSpeed(dx, dy, dz);
        setColor(0xEEEEEE);
        setAlpha(.25f);
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 8));
    }

    @Override
    public void tick() {
        super.tick();

        BlockState block = level.getBlockState(BlockPos.containing(getPos()));
        if(block.isAir()){return;}
        Block notState = block.getBlock();

        if(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SPLASHING.matches(block) || notState == Blocks.WATER){
            setColor(0x3C90FB);
        } else if(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.matches(block) || notState == Blocks.LAVA){
            setColor(0xE05116);
        } else if(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.matches(block)){
            setColor(0x494949);
        } else if(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.matches(block)){
            setColor(0x0c4648);
        }
    }

    @Nonnull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleAirParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        @Override
        public Particle createParticle(SimpleAirParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new SimpleAirParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
