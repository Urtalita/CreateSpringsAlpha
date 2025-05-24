package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock.LEN;

public class SpringCoilBlockEntity extends KineticBlockEntity {
    public boolean plate = false;
    public Direction plateFacing = Direction.UP;

    public SpringCoilBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        plate = compound.getBoolean("plate");
        plateFacing = Direction.byName(compound.getString("plateface"));
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("plateface", plateFacing.getName());
        compound.putBoolean("plate", plate);
    }

    public void onPlace(BlockPos pos){
        BlockPos centerPos = isSpringLayerCompleted(pos, Direction.Axis.Y, true);
        Direction direction = Direction.UP;
        if(centerPos == null){
            centerPos = isSpringLayerCompleted(pos, Direction.Axis.X, true);
            direction = Direction.EAST;
        }
        if(centerPos == null){
            centerPos = isSpringLayerCompleted(pos, Direction.Axis.Z, true);
            direction = Direction.SOUTH;
        }
        if(centerPos != null){

            Optional<SpringCoilBlockEntity> OcoilBE = getCoil(centerPos);
            if(OcoilBE.isPresent()){
                SpringCoilBlockEntity coilBE = OcoilBE.get();
                coilBE.assemble(centerPos, direction);
                coilBE.notifyUpdate();
            }
        }
    }

    private void assemble(BlockPos pos, Direction direction){
        int len = 1;
        int axisCoefficient = 1;

        if(isSpringLayerCompleted(pos.relative(direction), direction.getAxis(), false) != null){

        } else if (isSpringLayerCompleted(pos.relative(direction.getOpposite()), direction.getAxis(), false) != null){
            direction = direction.getOpposite();
            axisCoefficient = axisCoefficient * -1;

        } else {setSpring(pos, direction, len); return;}

        len++;
        goDeeper(direction, axisCoefficient, pos, len);
    }

    private void goDeeper(Direction direction, int axisCoefficient, BlockPos pos, int len){
        BlockPos pos1 = isSpringLayerCompleted(pos.getX(), pos.getY() + len*axisCoefficient, pos.getZ(), direction.getAxis(), false);

        if(pos1 == null){setSpring(pos, direction, len);return;}
        len++;
        goDeeper(direction, axisCoefficient, pos, len);
    }

    private void setSpring(BlockPos pos, Direction direction, int len){
        level.setBlock(pos ,ModBlocks.LARGE_SPRING.get().defaultBlockState().setValue(FACING, direction).setValue(LEN, len), 0);
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
        int fixedCoord;
        int primaryStart, primaryEnd;
        int secondaryStart, secondaryEnd;

        // Определение фиксированной координаты и диапазонов для проверки
        switch (axis) {
            case X:
                fixedCoord = x;
                primaryStart = layer - 2; // primary ось Y
                primaryEnd = layer;
                secondaryStart = z - 2;    // secondary ось Z
                secondaryEnd = z;
                break;
            case Y:
                fixedCoord = layer;
                primaryStart = x - 2;      // primary ось X
                primaryEnd = x;
                secondaryStart = z - 2;    // secondary ось Z
                secondaryEnd = z;
                break;
            case Z:
                fixedCoord = z;
                primaryStart = x - 2;      // primary ось X
                primaryEnd = x;
                secondaryStart = layer - 2; // secondary ось Y
                secondaryEnd = layer;
                break;
            default:
                return null;
        }

        // Перебор всех возможных стартовых позиций для квадрата 3x3
        for (int p = primaryStart; p <= primaryEnd; p++) {
            for (int s = secondaryStart; s <= secondaryEnd; s++) {
                boolean isComplete = true;

                // Проверка всех блоков в квадрате 3x3
                outerLoop:
                for (int dp = 0; dp < 3; dp++) {
                    for (int ds = 0; ds < 3; ds++) {
                        if (!mode && dp == 1 && ds == 1) {
                            continue;
                        }

                        int currentP = p + dp;
                        int currentS = s + ds;
                        BlockPos pos;

                        // Формируем BlockPos в зависимости от оси
                        switch (axis) {
                            case X:
                                pos = new BlockPos(fixedCoord, currentP, currentS);
                                break;
                            case Y:
                                pos = new BlockPos(currentP, fixedCoord, currentS);
                                break;
                            case Z:
                                pos = new BlockPos(currentP, currentS, fixedCoord);
                                break;
                            default:
                                pos = null;
                        }

                        if (pos == null || !checkBlock(pos, axis)) {
                            isComplete = false;
                            break outerLoop;
                        }
                    }
                }

                // Если слой завершен, возвращаем центр квадрата
                if (isComplete) {
                    switch (axis) {
                        case X:
                            return new BlockPos(fixedCoord, p + 1, s + 1);
                        case Y:
                            return new BlockPos(p + 1, fixedCoord, s + 1);
                        case Z:
                            return new BlockPos(p + 1, s + 1, fixedCoord);
                    }
                }
            }
        }
        return null;
    }

    private boolean checkBlock(BlockPos pos, Direction.Axis axis){
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
