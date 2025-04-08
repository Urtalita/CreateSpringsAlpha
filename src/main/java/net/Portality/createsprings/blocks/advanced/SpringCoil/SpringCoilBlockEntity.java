package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SpringCoilBlockEntity extends KineticBlockEntity {
    private boolean controler = false;
    private boolean assembled = false;
    public BlockPos[][][] spring = new BlockPos[3][3][16];

    public SpringCoilBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public boolean isControler() {
        return controler;
    }

    public boolean getAssembled(){
        return this.assembled;
    }

    public void setAssembled(boolean bool){
        assembled = bool;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        assembled = compound.getBoolean("assembled");
        controler = compound.getBoolean("controller");
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("assembled", assembled);
        compound.putBoolean("controller", controler);
    }

    public void onPlace(BlockPos pos){
        BlockPos centerPos = isSpringLayerCompleted(pos.getX(), pos.getY(), pos.getZ());
        if(centerPos != null){

            Optional<SpringCoilBlockEntity> OcoilBE = getCoil(centerPos);
            if(OcoilBE.isPresent()){
                SpringCoilBlockEntity coilBE = OcoilBE.get();
                coilBE.assemble(centerPos, true);
                coilBE.notifyUpdate();
            }
        }
    }

    public void onRemove(BlockPos pos){
        BlockPos centerPos = isSpringLayerCompleted(pos.getX(), pos.getY(), pos.getZ());
        if(centerPos != null){

            Optional<SpringCoilBlockEntity> OcoilBE = getCoil(centerPos);
            if(OcoilBE.isPresent()){
                SpringCoilBlockEntity coilBE = OcoilBE.get();
                coilBE.assemble(centerPos, false);
                coilBE.notifyUpdate();
            }
        }
    }

    public void assemble(BlockPos pos, boolean mode){
        controler = mode;

        for (int i = -1; i < 2; i++){
            for (int j = -1; j < 2; j++){
                setAssemble(new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + j), mode);
            }
        }
    }

    private void setAssemble(BlockPos pos, boolean mode){
        BlockEntity coilBE = level.getBlockEntity(pos);
        if ((coilBE instanceof SpringCoilBlockEntity)){
            ((SpringCoilBlockEntity) coilBE).setAssembled(mode);
            ((SpringCoilBlockEntity) coilBE).notifyUpdate();
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
            if(!((SpringCoilBlockEntity) coilBE).assembled){
                return true;
            }
        }
        return false;
    }
}
