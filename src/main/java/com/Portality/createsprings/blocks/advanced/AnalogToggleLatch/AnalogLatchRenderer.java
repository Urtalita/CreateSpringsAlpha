package com.Portality.createsprings.blocks.advanced.AnalogToggleLatch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class AnalogLatchRenderer extends SmartBlockEntityRenderer<AnalogLatchBe> {
    public AnalogLatchRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(AnalogLatchBe be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState leverState = be.getBlockState();
        float state = be.clientState.getValue(partialTicks);

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        // Handle
        SuperByteBuffer handle = CachedBuffers.partial(AllPartialModels.ANALOG_LEVER_HANDLE, leverState);
        float angle = (float) ((state / 15) * 90 / 180 * Math.PI);
        transform(handle, leverState).translate(1 / 2f, 1 / 16f + 3/16f, 1 / 2f)
                .rotate(angle, Direction.EAST)
                .translate(-1 / 2f, -1 / 16f, -1 / 2f);
        handle.light(light)
                .renderInto(ms, vb);

        // Indicator

        int color = Color.mixColors(0x2C0300, 0xCD0000, state / 15f);
        SuperByteBuffer indicator = transform(CachedBuffers.partial(AllPartialModels.ANALOG_LEVER_INDICATOR, leverState), leverState);
        indicator.translate(0, 3/16f, 0);
        indicator.light(light)
                .color(color)
                .renderInto(ms, vb);
    }

    private SuperByteBuffer transform(SuperByteBuffer buffer, BlockState state) {
        float rY = AngleHelper.horizontalAngle(state.getValue(AnalogLeverBlock.FACING).getClockWise());
        buffer.rotateCentered((float) (rY / 180 * Math.PI), Direction.UP);
        return buffer;
    }
}
