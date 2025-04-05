package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class SpringCoilBlockEntity extends KineticBlockEntity {
    public boolean controler = true;
    public BlockPos[][][] spring = new BlockPos[3][3][16];
    public SpringCoilBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public void onPlace(BlockPos pos){
        BlockPos centerPos = isSpringLayerCompleted(pos.getX(), pos.getY(), pos.getZ());
        if(centerPos != null){
            Optional<SpringCoilBlockEntity> OcoilBE = getCoil(centerPos);
            if(OcoilBE.isPresent()){
                SpringCoilBlockEntity coilBE = OcoilBE.get();
                coilBE.rotateCoilsAsCenter(centerPos);
                coilBE.notifyUpdate();
            }
        }
    }

    public void rotateCoilsAsCenter(BlockPos pos){
        level.setBlock(pos.north(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(FACING, Direction.WEST), 3);
        level.setBlock(pos.south(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(FACING, Direction.WEST), 3);
        level.setBlock(pos.west(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState(), 3);
        level.setBlock(pos.east(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState(), 3);

        level.setBlock(pos.north().east(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
        level.setBlock(pos.south().west(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
        level.setBlock(pos.west().north(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
        level.setBlock(pos.east().south(), ModBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(FACING, Direction.UP), 3);
    }

    private Optional<SpringCoilBlockEntity> getCoil(BlockPos pos){
        if (level == null)
            return Optional.empty();

        BlockEntity coilBE = level.getBlockEntity(pos);

        if ((coilBE instanceof SpringCoilBlockEntity))
            return Optional.of((SpringCoilBlockEntity) coilBE);
        return Optional.empty();
    }

    private BlockPos isSpringLayerCompleted(int x, int layer, int z) {

        for (int startX = x - 2; startX <= x; startX++) {
            for (int startZ = z - 2; startZ <= z; startZ++) {
                boolean isComplete = true;

                outerLoop:
                for (int dx = 0; dx < 3; dx++) {
                    for (int dz = 0; dz < 3; dz++) {
                        int currentX = startX + dx;
                        int currentZ = startZ + dz;
                        if (!checkBlock(new BlockPos(currentX, layer, currentZ))) {
                            isComplete = false;
                            break outerLoop;
                        }
                    }
                }

                if (isComplete) {
                    return new BlockPos(startX+1, layer, startZ+1);
                }
            }
        }
        return null;
    }

    private boolean checkBlock(BlockPos pos){
        if(level.getBlockEntity(pos) instanceof SpringCoilBlockEntity){
            return true;
        }
        return false;
    }
}
