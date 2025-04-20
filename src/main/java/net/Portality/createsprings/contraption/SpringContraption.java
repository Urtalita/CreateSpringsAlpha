package net.Portality.createsprings.contraption;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

import static net.Portality.createsprings.utill.CspringsMath.blockPosSum;
import static net.Portality.createsprings.utill.CspringsMath.calcPos;

public class SpringContraption extends TranslatingContraption {
    protected Direction facing;

    public SpringContraption(Direction facing) {this.facing = facing;}

    public SpringContraption(){}

    protected boolean isAnchoringBlockAt(BlockPos pos) {
        if (pos.getX() != anchor.getX() || pos.getZ() != anchor.getZ())
            return false;
        int y = pos.getY();
        if (y <= anchor.getY() || y > anchor.getY()  + 1)
            return false;
        return true;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        BlockPos offset = pos.relative(facing);
        if(!searchMovedStructure(world, offset, null)){
            return false;
        }

        startMoving(world);
        return true;
    }

    private void addSimpleDonut(Level level, Block block){
    }

    private void addSimpleBlock(Level level, BlockPos localPos, Block block) {
        localPos = new BlockPos(localPos.getX(), localPos.getY() + 1, localPos.getZ());

        BlockState state = block.defaultBlockState();
        StructureTemplate.StructureBlockInfo blockInfo = new StructureTemplate.StructureBlockInfo(
                localPos,
                state,
                null
        );

        Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair = Pair.of(blockInfo, null);

        addBlock(level, blockPosSum(localPos, anchor), pair);
    }

    @Override
    public CompoundTag writeNBT(boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(spawnPacket);
        tag.putInt("Facing", facing.get3DDataValue());
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
        super.readNBT(world, tag, spawnData);
        facing = Direction.from3DDataValue(tag.getInt("Facing"));
    }

    @Override
    public ContraptionType getType() {
        return CspringsContraptionTypes.SPRING.get();
    }

}
