package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.Portality.createsprings.blocks.ModBlocks;
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
    }

    public void onPlace(BlockPos pos, boolean mode){
        BlockPos centerPos = isSpringLayerCompleted(pos.getX(), pos.getY(), pos.getZ());
        if(centerPos != null){

            Optional<SpringCoilBlockEntity> OcoilBE = getCoil(centerPos);
            if(OcoilBE.isPresent()){
                SpringCoilBlockEntity coilBE = OcoilBE.get();
                coilBE.assemble(centerPos);
                coilBE.notifyUpdate();
            }
        }
    }

    public void assemble(BlockPos pos){
        level.setBlock(pos ,ModBlocks.LARGE_SPRING.get().defaultBlockState().setValue(FACING, Direction.UP).setValue(LEN, 1), 0);
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
        BlockEntity coilBE = level.getBlockEntity(pos);
        if(coilBE instanceof SpringCoilBlockEntity){
            return true;
        }
        return false;
    }
}
