package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class LargeSpringBlockEntity extends GeneratingKineticBlockEntity {

    public static final float capacity = CreateSprings.SPRING_CAPACITY;
    private float progres;
    public float stored = 0;
    private int len = 32;
    private int curLen = len;
    private boolean isGenerating;

    public LargeSpringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getProgres() {return progres;}

    public int getLen() {return len;}

    @Override
    public float calculateStressApplied() {
        if (stored < capacity && !isGenerating) {
            return 2.0f;
        } else if (isGenerating) {
            return -128f;
        }
        return 0f;
    }

    public void onPlace(BlockPos pos){
        for(int y = 0; y <= curLen; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        level.setBlock(new BlockPos(pos.getX() + i, pos.getY() + y, pos.getZ() + j), ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    public void onBreak(BlockPos pos){
        for(int y = 0; y <= curLen; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        level.setBlock(new BlockPos(pos.getX() + i, pos.getY() + y, pos.getZ() + j), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        int targetMinLength = (int) (len * 0.5f);
        int maxCompressionSteps = len - targetMinLength;

        float CurSpeed = Math.abs(getSpeed());
        if (isGenerating && stored > 0) {
            stored = Math.max(stored - 256, 0);
            updateGeneratedRotation();

            float threshold = (capacity / maxCompressionSteps) * (len - curLen);
            if (stored <= threshold) {
                curLen++;
                restoreLayer(curLen);
            }
        }
        // Режим накопления, если не активировано
        else if (!isGenerating) {
            stored = Mth.clamp(stored + CurSpeed, 0, capacity);

            float threshold = (capacity / maxCompressionSteps) * (len - curLen + 1);
            if (stored >= threshold) {
                removeLayer(curLen);
                curLen--;
            }
        }

        // Если высота изменилась (пружина расширяется)

        updateExtensionBlocks();
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

    private void removeLayer(int yLevel) {
        BlockPos pos = getBlockPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    level.setBlock(
                            new BlockPos(pos.getX() + i, pos.getY() + (yLevel), pos.getZ() + j),
                            Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL
                    );
                }
            }
        }
    }

    private void restoreLayer(int yLevel) {
        BlockPos pos = getBlockPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    level.setBlock(
                            new BlockPos(pos.getX() + i, pos.getY() + (yLevel), pos.getZ() + j),
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

    private void updateExtensionBlocks() {
        int compressionLevel = Math.round(16 - (calculateCurPos() - ((int) calculateCurPos())) * 16);
        BlockPos pos = getBlockPos();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    BlockPos extPos = new BlockPos(pos.getX() + i, pos.getY() + (curLen), pos.getZ() + j);
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
        return isGenerating && stored > 0 ? 16.0f : 0.0f;
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
