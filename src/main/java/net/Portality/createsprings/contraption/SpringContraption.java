package net.Portality.createsprings.contraption;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.utill.Helpers.CspringsMath;
import net.createmod.catnip.data.UniqueLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static net.Portality.createsprings.utill.Helpers.CspringsMath.blockPosSum;
import static net.Portality.createsprings.utill.Helpers.CspringsMath.calcPos;

public class SpringContraption extends BearingContraption {

    public SpringContraption(Direction facing) {super(false ,facing);}

    public SpringContraption(){}

    @Override
    public ContraptionType getType() {
        return CspringsContraptionTypes.SPRING.get();
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
        BlockState blockstate = ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState();
        return Pair.of(new StructureTemplate.StructureBlockInfo(pos, blockstate, null), null);
    }
}
