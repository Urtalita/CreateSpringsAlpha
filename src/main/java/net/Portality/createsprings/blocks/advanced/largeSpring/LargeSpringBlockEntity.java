package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.piston.PistonContraption;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.awt.*;
import java.time.Year;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static net.Portality.createsprings.utill.LargeSpringContraptionHelper.activateContraption;
import static net.Portality.createsprings.utill.LargeSpringContraptionHelper.create3x3EarthContraption;

public class LargeSpringBlockEntity extends GeneratingKineticBlockEntity {

    public static final float capacity = CreateSprings.SPRING_CAPACITY;
    private float progres;
    public float stored = 0;
    private int len = 1;
    private int curLen = len;
    private boolean isGenerating;
    protected PistonContraption movedContraption;

    public LargeSpringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getProgres() {return progres;}

    public int getLen() {return len;}

    @Override
    public float calculateStressApplied() {
        if (stored < capacity && !isGenerating) {
            return 128.0f;
        } else if (isGenerating) {
            return -1024;
        }
        return 0f;
    }

    public void onPlace(BlockPos pos, Direction facing){
        for(int y = 0; y <= curLen; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        level.setBlock(calcPos(i, y, j, pos, facing), ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    public void onBreak(BlockPos pos, Direction facing){
        for(int y = 0; y <= curLen; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        level.setBlock(calcPos(i, y, j, pos, facing), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    public void assemble() throws AssemblyException {
        Contraption contraption = create3x3EarthContraption(getBlockState().getValue(FACING), level);
    }

    public void disassemble(){

    }

    private BlockPos calcPos(int x, int y, int z,BlockPos pos, Direction facing){
        int dierectionFactor = 1;
        if(facing == Direction.DOWN || facing == Direction.WEST || facing == Direction.NORTH){
            dierectionFactor = -1;
        }
        if(facing == Direction.UP || facing == Direction.DOWN){
            return new BlockPos(
                    (pos.getX() + x),
                    (pos.getY() + y * dierectionFactor),
                    (pos.getZ() + z)
            );
        } else if(facing == Direction.EAST || facing == Direction.WEST){
            return new BlockPos(
                    (pos.getX() + y * dierectionFactor),
                    (pos.getY() + x),
                    (pos.getZ() + z)
            );
        }
        return new BlockPos(
                (pos.getX() + x),
                (pos.getY() + z),
                (pos.getZ() + y * dierectionFactor)
        );
    }

    @Override
    public void tick() {
        super.tick();
        int targetMinLength = (int) (len * 0.5f);
        int maxCompressionSteps = len - targetMinLength;
        Direction facing = getBlockState().getValue(FACING);

        float CurSpeed = Math.abs(getSpeed());
        if (isGenerating && stored > 0) {
            stored = Math.max(stored - 256, 0);
            updateGeneratedRotation();

            float threshold = (capacity / maxCompressionSteps) * (len - curLen);
            if (stored <= threshold) {
                curLen++;
                //restoreLayer(curLen, facing);
            }
        }
        // Режим накопления, если не активировано
        else if (!isGenerating) {
            stored = Mth.clamp(stored + CurSpeed*4, 0, capacity);

            float threshold = (capacity / maxCompressionSteps) * (len - curLen + 1);
            if (stored >= threshold) {
                //removeLayer(curLen, facing);
                curLen--;
            }
        }

        //updateExtensionBlocks(facing);
        progres = stored / capacity;
    }

    private boolean canBreakBlock(BlockState state) {
        if(state.is(Blocks.AIR)){ return true;}

        if (state.is(ModBlocks.LARGE_SPRING_EXTENTION.get()) || state.getDestroySpeed(level, BlockPos.ZERO) < 0) {
            return false;
        }

        // Эмпирическая проверка: блоки с сопротивлением <= 50 (примерное значение для алмазной кирки)
        float hardness = state.getDestroySpeed(level, BlockPos.ZERO);
        boolean isMineable = state.is(BlockTags.MINEABLE_WITH_PICKAXE);

        return isMineable && hardness <= 50.0f && hardness >= 0;
    }

    private boolean destroyBlocksInLayer(BlockPos springPos, int y) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = springPos.offset(x, y, z);
                BlockState state = level.getBlockState(checkPos);

                if (canBreakBlock(state)) {
                    level.destroyBlock(checkPos, true);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private void removeLayer(int yLevel, Direction facing) {
        BlockPos pos = getBlockPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    level.setBlock(
                            calcPos(i, yLevel, j, pos, facing),
                            Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL
                    );
                }
            }
        }
    }

    private void restoreLayer(int yLevel, Direction facing) {
        BlockPos pos = getBlockPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    level.setBlock(
                            calcPos(i, yLevel, j, pos, facing),
                            ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState(),
                            Block.UPDATE_ALL
                    );
                }
            }
        }
    }

    private float calculateCurPos(){
        return len - progres * (len * 0.5f);
    }

    private void updateExtensionBlocks(Direction facing) {
        int compressionLevel = Math.round(16 - (calculateCurPos() - ((int) calculateCurPos())) * 16);
        BlockPos pos = getBlockPos();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    BlockPos extPos = calcPos(i, curLen, j, pos, facing);
                    BlockState extState = level.getBlockState(extPos);
                    if (extState.getBlock() instanceof LargeSpringExstentionBlock) {
                        level.setBlock(
                                extPos,
                                extState.setValue(LargeSpringExstentionBlock.COMPRESSION, compressionLevel),
                                Block.UPDATE_ALL
                        );
                    }
                }
            }
        }
    }

    @Override
    public float getGeneratedSpeed() {
        return isGenerating && stored > 0 ? 1.0f : 0.0f;
    }

    public void setGenerating(boolean generating) {
        isGenerating = generating;
        updateGeneratedRotation(); // Обновляем физику
        sendData(); // Синхронизация
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("Generating", isGenerating);
        compound.putFloat("progres", progres);
        compound.putFloat("stored", stored);
        compound.putInt("len", len);
        compound.putInt("curLen", curLen);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        isGenerating = compound.getBoolean("Generating");
        len = compound.getInt("len");
        progres = compound.getFloat("progres");
        stored = compound.getFloat("stored");
        curLen = compound.getInt("curLen");
    }
}
