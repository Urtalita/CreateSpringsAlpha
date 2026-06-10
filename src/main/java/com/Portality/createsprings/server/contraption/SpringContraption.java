package com.Portality.createsprings.server.contraption;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import net.createmod.catnip.data.UniqueLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Queue;

import static com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity.calcPos;

public class SpringContraption extends BearingContraption {

    public SpringContraption(Direction facing) {super(false ,facing);}

    public SpringContraption(){}

    @Override
    public ContraptionType getType() {
        return CspringsContraptionTypes.SPRING.value();
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        BlockPos offset = pos.relative(facing);
        if (!searchMovedStructure(world, offset, null))
            return false;
        startMoving(world);

        return true;
    }

    public boolean searchMovedStructure(Level world, BlockPos pos, @Nullable Direction forcedDirection)
            throws AssemblyException {
        Queue<BlockPos> frontier = new UniqueLinkedList<>();
        anchor = pos;

        if (bounds == null)
            bounds = new AABB(BlockPos.ZERO);

        if (!BlockMovementChecks.isBrittle(world.getBlockState(pos)))
            frontier.add(pos);
        if (!addToInitialFrontier(world, pos, forcedDirection, frontier))
            return false;

        BlockPos pos1 = frontier.poll();
        for (int i = -1; i < 2; i++){
            for (int j = -1; j < 2; j++){
                BlockPos posBlock = calcPos(i, 0, j, pos1, facing);
                addBlock(world, posBlock, capture(world, posBlock));
            }
        }

        return true;
    }

    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockState blockstate = CSpringsBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState();
        return Pair.of(new StructureTemplate.StructureBlockInfo(pos, blockstate, null), null);
    }
}
