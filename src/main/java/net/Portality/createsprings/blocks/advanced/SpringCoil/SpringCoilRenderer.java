package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.Portality.createsprings.utill.CSpringsPartalModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class SpringCoilRenderer extends KineticBlockEntityRenderer {
    public SpringCoilRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BlockState state) {
        SuperByteBuffer coilRenderer = CachedBuffers.partialFacing(CSpringsPartalModels.LARGE_SPRING_COIL, state);
        return coilRenderer;
    }
}
