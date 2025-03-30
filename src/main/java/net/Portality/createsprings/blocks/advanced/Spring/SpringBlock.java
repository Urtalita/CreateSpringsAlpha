package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

import java.util.List;

import static net.minecraft.world.level.block.WeepingVinesPlantBlock.SHAPE;

public class SpringBlock extends DirectionalKineticBlock implements IBE<SpringBlockEntity> {
    public SpringBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return false;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.getPosition();
        Vec3 BlPos = pos.getCenter();
        SpringBlockEntity blockEntity = (SpringBlockEntity) level.getBlockEntity(pos);
        double distance = BlPos.distanceTo(ExpPos);
        float addstored = (float) (16 / (distance) * 10000);

        if (blockEntity.stored+1 < blockEntity.capacity){
            blockEntity.stored += addstored;
            if(blockEntity.stored > blockEntity.capacity){
                blockEntity.stored = blockEntity.capacity;
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, builder);
    }

    /*
    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        SpringBlockEntity blockEntity = (SpringBlockEntity) level.getBlockEntity(pos);
        if (blockEntity != null) {
            CompoundTag tag = new CompoundTag();
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.putFloat("Stored", blockEntity.stored);
            tag.put("BlockEntityTag", blockEntityTag);
            stack.setTag(tag);
        }
        return stack;
    }
     */

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        withBlockEntityDo(level, pos, be -> be.setGenerating(hasSignal));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            withBlockEntityDo(level, pos, be -> be.setGenerating(false));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection().getOpposite();
        if (context.getPlayer().isShiftKeyDown()){
            return defaultBlockState().setValue(FACING, facing.getOpposite());
        } else {
            return defaultBlockState().setValue(FACING, facing);
        }
    }

    @Override
    public Class<SpringBlockEntity> getBlockEntityClass() { return SpringBlockEntity.class; }

    @Override
    public BlockEntityType<? extends SpringBlockEntity> getBlockEntityType() { return ModBlockEntities.SPRING.get(); }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(FACING);
        return face == facing || face == facing.getOpposite();
    }
}
