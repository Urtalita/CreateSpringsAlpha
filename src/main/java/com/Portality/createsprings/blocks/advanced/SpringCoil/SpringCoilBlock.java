package com.Portality.createsprings.blocks.advanced.SpringCoil;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.CSpringsBlockEntities;
import com.Portality.createsprings.utill.Helpers.HitboxHelper;
import com.google.common.base.Predicates;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;

public class SpringCoilBlock extends DirectionalKineticBlock implements IBE<SpringCoilBlockEntity> {
    public static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public SpringCoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<SpringCoilBlockEntity> getBlockEntityClass() {
        return SpringCoilBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpringCoilBlockEntity> getBlockEntityType() {
        return CSpringsBlockEntities.LARGE_SPRING_COIL.get();
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return HitboxHelper.calculateDierectionalVoxelShape(this, state.getValue(FACING), new Vec3(4, 4, 0), new Vec3(12, 12, 16));
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);

        if(oldState == Blocks.COBBLESTONE.defaultBlockState()){
            return;
        }
        withBlockEntityDo(worldIn, pos, be-> {
            be.onPlace(pos, state);
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        IPlacementHelper helper = PlacementHelpers.get(placementHelperId);

        if (helper.matchesItem(player.getItemInHand(hand))){
            PlacementOffset offset = helper.getOffset(player, level, state, pos, result);
            ItemInteractionResult result1 = offset.placeInWorld(level, (BlockItem) player.getItemInHand(hand).getItem(), player, hand, result);
            BlockPos newPos = offset.getBlockPos();
            //correct facing of placed coil
            for(Direction direction : Direction.values()){
                if(level.getBlockState(newPos.relative(direction)).getBlock() == CSpringsBlocks.LARGE_SPRING_COIL.get()){
                    Direction facing = level.getBlockState(newPos.relative(direction)).getValue(FACING);
                    state.setValue(FACING, facing);
                    level.setBlock(newPos, state, 3);
                }
            }
            return result1;
        }

        if (player.getItemInHand(hand).getItem() != CSpringsBlocks.OBSIDIAN_PLATE.get().asItem()){
            return super.useItemOn(stack, state, level, pos, player, hand, result);
        }

        withBlockEntityDo(level, pos, be-> {
            if(be.plate){
                return;
            }
            be.plate = true;
            be.plateFacing = result.getDirection().getOpposite();
            if(player.isCreative()){
                return;
            }
            player.getItemInHand(hand).shrink(1);
        });
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(FACING);
        return face == facing || face == facing.getOpposite();
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper extends PoleHelper<Direction> {
        private PlacementHelper() {
            super(state -> state.getBlock() instanceof SpringCoilBlock, state -> state.getValue(FACING).getAxis(), FACING);
        }

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem
                    && ((BlockItem) i.getItem()).getBlock() instanceof SpringCoilBlock;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return Predicates.or(CSpringsBlocks.LARGE_SPRING_COIL::has);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful())
                offset.withTransform(offset.getTransform()
                        .andThen(s -> world.isClientSide() ? s : CSpringsBlocks.LARGE_SPRING_COIL.getDefaultState()));
            return offset;
        }
    }
}
