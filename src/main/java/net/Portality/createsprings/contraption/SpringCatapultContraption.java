package net.Portality.createsprings.contraption;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import net.Portality.createsprings.blocks.ModBlocks;
import net.createmod.catnip.data.UniqueLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Queue;

import static net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity.calcPos;

public class SpringCatapultContraption extends BearingContraption {
    public SpringCatapultContraption(Direction facing) {super(false ,facing);}

    public SpringCatapultContraption(){}

    @Override
    public ContraptionType getType() {
        return CspringsContraptionTypes.SPRING_CATAPULT.get();
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
        BlockPos posBlock = pos1.above();
        addBlock(world, posBlock, capture(world, posBlock));

        return true;
    }

    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockState blockstate = AllBlocks.MECHANICAL_DRILL.get().defaultBlockState();
        return Pair.of(new StructureTemplate.StructureBlockInfo(pos, blockstate, null), null);
    }
}
