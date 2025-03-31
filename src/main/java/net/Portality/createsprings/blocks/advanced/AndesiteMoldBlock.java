package net.Portality.createsprings.blocks.advanced;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import org.openjdk.nashorn.internal.ir.Statement;

import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class AndesiteMoldBlock extends DirectionalBlock {
    public AndesiteMoldBlock(Properties p_52591_) {
        super(p_52591_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
        if(true){
            Optional<SpoutBlockEntity> SpBe = FindSpout(level, state, pos);

            if(SpBe.isPresent()) {
                SpoutBlockEntity SpoutEntity = SpBe.get();
                level.setBlock(pos, Blocks.ANDESITE.defaultBlockState(), 1);
            }
        }

        super.tick(state, level, pos, source);
    }

    private Optional<SpoutBlockEntity> FindSpout(Level level, BlockState state, BlockPos pos){
        if (level == null)
            return Optional.empty();

        BlockEntity SpoutEntity = level.getBlockEntity(pos.above(2));

        if ((SpoutEntity instanceof SpoutBlockEntity))
            return Optional.of((SpoutBlockEntity) SpoutEntity);
        return Optional.empty();
    }
}
