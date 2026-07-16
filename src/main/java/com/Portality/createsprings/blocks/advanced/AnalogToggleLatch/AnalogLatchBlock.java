package com.Portality.createsprings.blocks.advanced.AnalogToggleLatch;

import com.Portality.createsprings.blocks.CSpringsBlockEntities;
import com.simibubi.create.content.redstone.diodes.ToggleLatchBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import java.util.Optional;

public class AnalogLatchBlock extends ToggleLatchBlock implements IBE<AnalogLatchBe> {
    public AnalogLatchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<AnalogLatchBe> getBlockEntityClass() {
        return AnalogLatchBe.class;
    }

    @Override
    public BlockEntityType<? extends AnalogLatchBe> getBlockEntityType() {
        return CSpringsBlockEntities.ANALOG_TOGGLE_LATCH.get();
    }

    @Override
    protected int getOutputSignal(BlockGetter worldIn, BlockPos pos, BlockState state) {
        boolean inverse = !state.getValue(POWERING);
        boolean powered = state.getValue(POWERED);
        int selectedSignal = 0;

        Optional<AnalogLatchBe> optional = getBlockEntityOptional(worldIn, pos);
        if(optional.isPresent()) selectedSignal = optional.get().getValue();

        if(!inverse){
            if(!powered) return selectedSignal;
            return 0;
        }

        if(powered) return selectedSignal;
        return 0;
    }
}
