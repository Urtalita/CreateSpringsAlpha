package net.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SpringCatapultBlock extends KineticBlock implements IBE<SpringCatapultBlockEntity> {
    public SpringCatapultBlock(Properties properties) {
        super(properties);
    }

    public static final BooleanProperty CEILING = BooleanProperty.create("ceiling");

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        withBlockEntityDo(pLevel, pPos, be -> be.onRemove());
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(CEILING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(CEILING, ctx.getClickedFace() == Direction.DOWN);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<SpringCatapultBlockEntity> getBlockEntityClass() {
        return SpringCatapultBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpringCatapultBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SPRING_CATAPULT.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }
}
