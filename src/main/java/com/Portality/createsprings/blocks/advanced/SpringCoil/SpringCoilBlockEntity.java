package com.Portality.createsprings.blocks.advanced.SpringCoil;

import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class SpringCoilBlockEntity extends KineticBlockEntity {
    private static final Logger log = LoggerFactory.getLogger(SpringCoilBlockEntity.class);
    public boolean plate = false;
    public Direction plateFacing = Direction.UP;

    public SpringCoilBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        plate = compound.getBoolean("plate");
        plateFacing = Direction.byName(compound.getString("plateface"));
        super.read(compound, registries, clientPacket);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if(plateFacing != null){
            compound.putString("plateface", plateFacing.getName());
        }
        compound.putBoolean("plate", plate);
    }

    public void onPlace(BlockPos pos, BlockState state){
        Direction direction = state.getValue(FACING).getOpposite();
        BlockPos centerPos = isSpringLayerCompleted(pos, direction.getAxis(), true);

        if(centerPos == null){return;}

        Optional<SpringCoilBlockEntity> OcoilBE = getCoil(centerPos);
        if(!OcoilBE.isPresent()){return;}

        SpringCoilBlockEntity coilBE = OcoilBE.get();
        coilBE.assemble(centerPos, direction);
    }


    private void assemble(BlockPos pos, Direction direction) {
        int axisCoefficient = (direction == Direction.UP || direction == Direction.EAST || direction == Direction.SOUTH) ? 1 : -1;

        int len = goDeeper(direction, axisCoefficient, pos, 0);
        if(len != 1){setSpring(pos, direction, len);}

        int secondTryLen = goDeeper(direction.getOpposite(), axisCoefficient * -1, pos, 0);
        setSpring(pos, direction.getOpposite(), secondTryLen);
    }

    private int goDeeper(Direction direction, int axisCoefficient, BlockPos pos, int len) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Direction.Axis axis = direction.getAxis();

        switch (axis) {
            case X: x += len * axisCoefficient; break;
            case Y: y += len * axisCoefficient; break;
            case Z: z += len * axisCoefficient; break;
        }

        BlockPos nextLayerPos = isSpringLayerCompleted(x, y, z, axis, false);

        if (nextLayerPos == null || len >= ModConfigs.common().SPRING_LEN.get()) {
            return len;
        }

        return goDeeper(direction, axisCoefficient, pos, len + 1);
    }

    private void setSpring(BlockPos pos, Direction direction, int len){
        if(len == 1){
            direction = getBlockState().getValue(FACING);
        }

        if(len <= 0){return;}
        //level.setBlock(pos ,ModBlocks.LARGE_SPRING.get().defaultBlockState().setValue(FACING, direction).setValue(LEN, len), 3);

        BlockPos min = worldPosition.above(5).east(5).north(5);
        BlockPos max = worldPosition.below(5).west(5).south(5);
        AABB area = new AABB(
                min.getX(), min.getY(), min.getZ(),
                max.getX() + 1.0, max.getY() + 1.0, max.getZ() + 1.0
        );

        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            //CSpringsAdvancements.LARGE_SPRING.awardTo(player);
            return;
        }
    }

    private Optional<SpringCoilBlockEntity> getCoil(BlockPos pos){
        if (level == null)
            return Optional.empty();

        BlockEntity coilBE = level.getBlockEntity(pos);

        if ((coilBE instanceof SpringCoilBlockEntity))
            return Optional.of((SpringCoilBlockEntity) coilBE);
        return Optional.empty();
    }

    private BlockPos isSpringLayerCompleted(BlockPos pos, Direction.Axis axis, boolean mode) {
        return isSpringLayerCompleted(pos.getX(), pos.getY(), pos.getZ(), axis, mode);
    }

    private BlockPos isSpringLayerCompleted(int x, int layer, int z, Direction.Axis axis, boolean mode) {
        // Преобразование координат в зависимости от оси
        switch (axis) {
            case X:
                // Для оси X: fixed = x, primary = layer (Y), secondary = z (Z)
                BlockPos resultX = checkLayerInYPlane(layer, x, z, mode, axis);
                return resultX != null ? new BlockPos(x, resultX.getX(), resultX.getZ()) : null;

            case Y:
                // Для оси Y: fixed = layer, primary = x (X), secondary = z (Z)
                return checkLayerInYPlane(x, layer, z, mode, axis);

            case Z:
                // Для оси Z: fixed = z, primary = x (X), secondary = layer (Y)
                BlockPos resultZ = checkLayerInYPlane(x, z, layer, mode, axis);
                return resultZ != null ? new BlockPos(resultZ.getX(), resultZ.getZ(), z) : null;

            default:
                return null;
        }
    }

    private BlockPos checkLayerInYPlane(int primary, int fixedY, int secondary, boolean mode, Direction.Axis axis) {
        int primaryStart = primary - 2;
        int primaryEnd = primary;
        int secondaryStart = secondary - 2;
        int secondaryEnd = secondary;

        for (int p = primaryStart; p <= primaryEnd; p++) {
            for (int s = secondaryStart; s <= secondaryEnd; s++) {
                boolean isComplete = true;

                outerLoop:
                for (int dp = 0; dp < 3; dp++) {
                    for (int ds = 0; ds < 3; ds++) {
                        // Пропуск центра при mode=false
                        if (!mode && dp == 1 && ds == 1) {
                            continue;
                        }

                        // Создаем позицию в Y-плоскости
                        BlockPos pos = new BlockPos(p + dp, fixedY, s + ds);

                        if (!checkBlock(pos, axis)) {
                            isComplete = false;
                            break outerLoop;
                        }
                    }
                }

                if (isComplete) {
                    // Возвращаем центр квадрата в Y-плоскости
                    return new BlockPos(p + 1, fixedY, s + 1);
                }
            }
        }
        return null;
    }

    private boolean checkBlock(BlockPos pos, Direction.Axis axis){
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        switch (axis) {
            case X:
                return checkBlockInY(new BlockPos(y, x, z), axis);
            case Y:
                return checkBlockInY(new BlockPos(x, y, z), axis);
            case Z:
                return checkBlockInY(new BlockPos(x, z, y), axis);
        }
        return false;
    }

    private boolean checkBlockInY(BlockPos pos, Direction.Axis axis){
        BlockEntity coilBE = level.getBlockEntity(pos);
        if(coilBE instanceof SpringCoilBlockEntity){
            SpringCoilBlockEntity be = (SpringCoilBlockEntity) coilBE;
            if(be.getBlockState().getValue(FACING).getAxis() != axis){
                return false;
            }
            return true;
        }
        return false;
    }
}
