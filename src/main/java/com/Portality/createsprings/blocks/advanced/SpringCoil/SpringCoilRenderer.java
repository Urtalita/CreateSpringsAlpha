package com.Portality.createsprings.blocks.advanced.SpringCoil;

import com.Portality.createsprings.client.CSpringsPartalModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class SpringCoilRenderer extends KineticBlockEntityRenderer {
    public SpringCoilRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BlockState state) {
        SuperByteBuffer coilRenderer = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_COIL_ROTATED, state);
        return coilRenderer;
    }
}
