package com.Portality.createsprings.blocks.advanced.largeSpring;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.CSpringsBlockEntities;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static com.Portality.createsprings.blocks.advanced.spring.SpringBlock.getSpringChargeCoefficient;

public class LargeSpringBlock extends DirectionalKineticBlock implements IBE<LargeSpringBlockEntity> {

    public static final IntegerProperty LEN = IntegerProperty.create("len", 1,  384);

    public LargeSpringBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        withBlockEntityDo(level, pos, b ->{
            if(level.isClientSide()){return;}
            b.onExtensionChanged(pos);
        });
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.center();
        float coef = getSpringChargeCoefficient(state.getValue(FACING).getOpposite(), pos, ExpPos);

        if(coef < 0.30f){
            super.onBlockExploded(state, level, pos, explosion);
        }
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (isMoving) {
            withBlockEntityDo(worldIn, pos, be-> {
                try {
                    be.assemble();
                } catch (AssemblyException e) {
                    throw new RuntimeException(e);
                }

                be.updateGeneratedRotation();
                be.notifyUpdate();
                be.sendData();
            });

            worldIn.scheduleTick(pos, this, 1);
            super.onPlace(state, worldIn, pos, oldState, isMoving);
            return;
        }

        withBlockEntityDo(worldIn, pos, be-> {
            try {
                be.onPlace(pos, state.getValue(FACING), state.getValue(LEN));
            } catch (AssemblyException e) {
                throw new RuntimeException(e);
            }
        });
        worldIn.scheduleTick(pos, this, 1);
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        LargeSpringBlockEntity largeSpringBlockEntity = getBlockEntity(getter, pos);
        if(!largeSpringBlockEntity.canDisassemble(state.getValue(FACING))){
            return 0;
        }
        return super.getDestroyProgress(state, player, getter, pos);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return canNotWrench(context.getPlayer());
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return canNotWrench(context.getPlayer());
    }

    public InteractionResult canNotWrench(Player player){
        if(player instanceof ServerPlayer){return InteractionResult.CONSUME;}
        AllSoundEvents.DENY.playFrom(player);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(pIsMoving) {
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            return;
        }

        withBlockEntityDo(pLevel, pPos, be-> {
            be.onBreak(pPos, pState.getValue(FACING));
        });
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult use = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        ItemStack itemInHand = player.getItemInHand(hand);

        if(!ModConfigs.common().SPRINGS_CAN_SPLASH.get()){
            if(itemInHand.getItem() == Blocks.TRIPWIRE_HOOK.asItem()){
                player.playSound(SoundEvents.ITEM_BREAK, 0.5F, 1.0F);
                if(!player.isCreative()){
                    itemInHand.shrink(1);
                }
            }
            return use;
        }

        if(itemInHand.getItem() == Blocks.TRIPWIRE_HOOK.asItem()){
            return onBlockEntityUseItemOn(level, pos, be ->{
                if(be.splashMode){
                    return ItemInteractionResult.FAIL;
                }
                if(!player.isCreative()){itemInHand.shrink(1);}
                be.splashMode = true;
                AllSoundEvents.WRENCH_ROTATE.playOnServer(level, pos);
                return ItemInteractionResult.SUCCESS;
            });
        }

        if(itemInHand.getItem() == ItemStack.EMPTY.getItem()){
            return onBlockEntityUseItemOn(level, pos, be ->{
                if(be.splashMode){
                    if(!player.isCreative()){player.addItem(new ItemStack(Blocks.TRIPWIRE_HOOK));}
                    be.splashMode = false;
                    AllSoundEvents.WRENCH_ROTATE.playOnServer(level, pos);
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.FAIL;
            });
        }
        return use;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public Class<LargeSpringBlockEntity> getBlockEntityClass() {
        return LargeSpringBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(FACING);
        return face == facing || face == facing.getOpposite();
    }

    @Override
    public BlockEntityType<? extends LargeSpringBlockEntity> getBlockEntityType() {
        return CSpringsBlockEntities.LARGE_SPRING.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean canStickTo(BlockState state, BlockState other) {
        if (other.getBlock() == Blocks.SLIME_BLOCK) return true;
        if (other.getBlock() == Blocks.HONEY_BLOCK) return true;
        if (other.getBlock() == CSpringsBlocks.LARGE_SPRING.get()) return true;
        if (other.getBlock() == CSpringsBlocks.LARGE_SPRING_EXTENTION.get()) return true;
        return false;
    }
}
