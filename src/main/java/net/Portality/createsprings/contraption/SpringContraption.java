package net.Portality.createsprings.contraption;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
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

        addBlock(world, offset , capture(world, pos));

        startMoving(world);
        return true;
    }

    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockState blockstate = Blocks.COBBLESTONE.defaultBlockState();
        CompoundTag compoundnbt = getBlockEntityNBT(world, pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PoweredShaftBlockEntity)
            blockEntity = AllBlockEntityTypes.BRACKETED_KINETIC.create(pos, blockstate);
        if (blockEntity instanceof FactoryPanelBlockEntity fpbe)
            fpbe.writeSafe(compoundnbt);

        return Pair.of(new StructureTemplate.StructureBlockInfo(pos, blockstate, compoundnbt), blockEntity);
    }

    public boolean searchMovedStructure(Level world, BlockPos pos, @Nullable Direction forcedDirection){
        return true;
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
