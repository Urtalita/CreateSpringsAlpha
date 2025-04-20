package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.Portality.createsprings.utill.HitboxHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class SpringCoilBlock extends DirectionalKineticBlock implements IBE<SpringCoilBlockEntity> {

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
        return ModBlockEntities.LARGE_SPRING_COIL.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return HitboxHelper.calculateDierectionalVoxelShape(state.getValue(FACING), new Vec3(4, 4, 0), new Vec3(12, 12, 16));
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);

        withBlockEntityDo(worldIn, pos, be-> {
            be.onPlaceOnBreak(pos, true);
        });
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (player.getItemInHand(hand).getItem() != ModBlocks.OBSIDIAN_PLATE.get().asItem()){
            return super.use(state, level, pos, player, hand, result);
        }
        withBlockEntityDo(level, pos, be-> {
            if(be.plate){
                return;
            }
            be.plate = true;
            be.plateFacing = result.getDirection();
            if(player.isCreative()){
                return;
            }
            player.getItemInHand(hand).shrink(1);
        });
        return super.use(state, level, pos, player, hand, result);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        withBlockEntityDo(pLevel, pPos, be->{
            be.onPlaceOnBreak(pPos, false);
        });

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(FACING);
        return face == facing || face == facing.getOpposite();
    }
}
