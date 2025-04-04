package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class SpringCoilBlockEntity extends KineticBlockEntity {
    public boolean Controler = true;
    public SpringCoilBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public void onPlace(BlockPos pos){

        level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 0);
        notifyUpdate();
    }

    private boolean checkRing(BlockPos pos){
        if(checkBlock(pos.east())){return true;}
        if(checkBlock(pos.west())){return true;}
        if(checkBlock(pos.north())){return true;}
        if(checkBlock(pos.south())){return true;}

        return false;
    }

    private boolean checkBlock(BlockPos pos){
        if(level.getBlockEntity(pos) instanceof SpringCoilBlockEntity){
            return true;
        }
        return false;
    }
}
