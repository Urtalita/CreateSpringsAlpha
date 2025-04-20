package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonContraption;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.contraption.SpringContraption;
import net.Portality.createsprings.utill.CspringsMath;
import net.Portality.createsprings.utill.RenderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class LargeSpringBlockEntity extends GeneratingKineticBlockEntity implements IControlContraption {

    public static final float capacity = CreateSprings.SPRING_CAPACITY;
    private float progres;
    public float stored = 0;
    private int len = 32;
    private int curLen = len;
    private boolean isGenerating;
    protected ControlledContraptionEntity movedContraption;
    private final Vec3i movementDirection;
    private boolean running = false;

    private float prevProgress;

    public LargeSpringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.movementDirection = getBlockState().getValue(FACING).getOpposite().getNormal();;
    }

    public float getProgres(float partalTicks) {
        float progressFrames = Mth.lerp(partalTicks + .5f, prevProgress, progres);

        if (movedContraption != null){
            movedContraption.moveTo(
                    CspringsMath.MoveWithoutVectors(
                            platePos(progressFrames),
                            worldPosition,
                            movementDirection));
        }

        return  progressFrames;
    }

    private float platePos(float progress){
        return calculateCurPos(progres) + 4/16f*(1-progress) - 6/16f;
    }

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

    public void onPlace(BlockPos pos, Direction facing) throws AssemblyException {
        for(int y = 0; y < curLen - 1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        level.setBlock(calcPos(i, y, j, pos, facing),
                                ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState(),
                                Block.UPDATE_ALL);
                    }
                }
            }
        }

        level.setBlock(worldPosition.relative(facing), Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_ALL);
        assemble();
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
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof LargeSpringBlock))
            return;

        BearingContraption contraption = new BearingContraption(false ,getBlockState().getValue(FACING));
        boolean canAssembleStructure = contraption.assemble(level, worldPosition);

        if (!canAssembleStructure) {
            return;
        }

        if (contraption.getBlocks().isEmpty()) {
            return;
        }
        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        movedContraption = ControlledContraptionEntity.create(level, this, contraption);

        running = true;

        movedContraption.setPos(
                CspringsMath.MoveWithoutVectors(
                        calculateCurPos(progres),
                        worldPosition,
                        movementDirection));

        level.addFreshEntity(movedContraption);

        if (contraption.containsBlockBreakers())
            award(AllAdvancements.CONTRAPTION_ACTORS);

        sendData();
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

        prevProgress = progres;

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
                restoreLayer((int) Math.floor(platePos(progres)), facing);
            }
        }
        // Режим накопления, если не активировано
        else if (!isGenerating) {
            stored = Mth.clamp(stored + CurSpeed*4, 0, capacity);

            float threshold = (capacity / maxCompressionSteps) * (len - curLen + 1);
            if (stored >= threshold) {
                removeLayer((int) Math.floor(platePos(progres)), facing);
                curLen--;
            }
        }

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

    private float calculateCurPos(float progres){
        return len - progres * (len * 0.5f);
    }

    private void updateExtensionBlocks(Direction facing) {
        int compressionLevel = Math.round(16 - (calculateCurPos(progres) - ((int) calculateCurPos(progres))) * 16);
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
        compound.putBoolean("running", running);
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
        running = compound.getBoolean("running");
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return movedContraption == contraption;
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        BlockState blockState = getBlockState();
        if (!(contraption.getContraption() instanceof BearingContraption))
            return;
        if (!blockState.hasProperty(LargeSpringBlock.FACING))
            return;

        this.movedContraption = contraption;
        setChanged();
        BlockPos anchor = worldPosition.relative(blockState.getValue(LargeSpringBlock.FACING));
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        if (!level.isClientSide) {
            sendData();
        }
    }

    @Override
    public void onStall() {
        if (!level.isClientSide)
            sendData();
    }

    @Override
    public boolean isValid() {
        return !isRemoved();
    }

    @Override
    public BlockPos getBlockPosition() {
        return worldPosition;
    }
}
