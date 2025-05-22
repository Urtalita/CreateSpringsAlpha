package net.Portality.createsprings.blocks.advanced.test;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class TestBlockEntity extends KineticBlockEntity {
    public TestBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        return 1;
    }

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        ArrayList<BlockPos> pos = new ArrayList<>();
        pos.add(worldPosition.relative(state.getValue(FACING), -1));
        pos.add(worldPosition.relative(state.getValue(FACING), 1));
        pos.add(worldPosition.relative(state.getValue(FACING), 2));
        return pos;
    }
}
