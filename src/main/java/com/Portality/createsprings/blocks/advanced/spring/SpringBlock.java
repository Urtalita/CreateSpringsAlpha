package com.Portality.createsprings.blocks.advanced.spring;

import com.Portality.createsprings.blocks.advanced.ModBlockEntities;
import com.Portality.createsprings.compat.SplashCallback;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpringBlock extends DirectionalKineticBlock implements IBE<SpringBlockEntity>, ISpringBlock , BlockWithSubLevelCollisionCallback {
    public SpringBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return !successExplosionCharging(state, pos, explosion);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if(!successExplosionCharging(state, pos, explosion)){
            super.onBlockExploded(state, level, pos, explosion);
        }
    }

    public static boolean successExplosionCharging(BlockState state, BlockPos pos, Explosion explosion){
        Vec3 ExpPos = explosion.center();

        Direction facing = state.getValue(FACING);
        float coef = getSpringChargeCoefficient(facing, pos, ExpPos);

        if(coef < 0.30f){
            return false;
        }
        return true;
    }

    public static float getSpringChargeCoefficient(Direction facing, BlockPos springPos, Vec3 explosionPos) {
        facing = facing.getOpposite();
        // Центр блока пружины
        Vec3 springCenter = Vec3.atCenterOf(springPos);

        // Вектор от пружины к точке взрыва
        Vec3 toExplosion = explosionPos.subtract(springCenter);

        // Вектор нормали направления пружины (уже нормализован)
        Vec3 facingVector = Vec3.atLowerCornerOf(facing.getNormal());

        // Нормализуем вектор к взрыву (с проверкой нулевой длины)
        double distanceSqr = toExplosion.lengthSqr();
        if (distanceSqr < 1e-7) {
            // Взрыв точно в центре пружины
            return 1.0f;
        }
        Vec3 normalizedToExplosion = toExplosion.normalize();

        // Вычисляем скалярное произведение
        double dotProduct = normalizedToExplosion.dot(facingVector);

        // Преобразуем в коэффициент 0-1:
        // - Отрицательные значения → 0 (взрыв с обратной стороны)
        // - Положительные значения → плавно возрастает до 1
        return (float) Math.max(0, dotProduct);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);

        // Получаем BlockEntity из параметров контекста
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        drops.set(0, copyFromBe(drops.get(0), blockEntity));

        return drops;
    }

    public static ItemStack copyFromBe(ItemStack stack, BlockEntity blockEntity){
        if (blockEntity instanceof SpringBlockEntity springEntity) {
            float charge = springEntity.getStored();
            boolean splashMode = springEntity.splashMode;

            stack.set(DataComponents.BLOCK_ENTITY_DATA,
                    CustomData.EMPTY.update(tag -> {
                                tag.putFloat("Stored", charge);
                                tag.putBoolean("splashMode", splashMode);
                                tag.putLong("Id", -99999999999999L);
                                tag.putString("id", "createsprings:spring");
                            }
                    ));
        }

        return stack;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemInHand, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemInteractionResult use = super.useItemOn(itemInHand, state, level, pos, player, hand, result);

        /*
        if(!ModConfigs.common().SPRINGS_CAN_SPLASH.get()){
            if(itemInHand.getItem() == Blocks.TRIPWIRE_HOOK.asItem()){
            player.playSound(SoundEvents.ITEM_BREAK, 0.5F, 1.0F);
            if(!player.isCreative()){
                itemInHand.shrink(1);
            }
        }
        return use;
        }

         */

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
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        withBlockEntityDo(worldIn, pos, be -> be.setGenerating(worldIn.hasNeighborSignal(pos)));
    }

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

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SpringBlockEntity myBE) {
            return myBE.getComparatorOutput(); // Получаем значение от BlockEntity
        }
        return 0;
    }

    @Override
    public BlockSubLevelCollisionCallback sable$getCallback() {
        return SplashCallback.INSTANCE;
    }
}
