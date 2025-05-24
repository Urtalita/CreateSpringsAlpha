package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlockEntity;
import net.Portality.createsprings.contraption.SpringContraption;
import net.Portality.createsprings.utill.Helpers.CspringsMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.springAnimation;

public class LargeSpringBlockEntity extends GeneratingKineticBlockEntity implements IControlContraption {

    public static float capacity = Config.spring_capacity;
    private float progress;
    public float stored = 0;
    private int len = 8;
    private int curLen = len;
    private boolean isGenerating;
    protected ControlledContraptionEntity movedContraption;
    private final Vec3i movementDirection;
    public boolean splashMode = false;
    private int phase = 0;

    private float prevProgress;

    public LargeSpringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.movementDirection = getBlockState().getValue(FACING).getOpposite().getNormal();
        capacity = Config.spring_capacity;
    }

    public float getProgres(float partalTicks) {
        return Mth.lerp(partalTicks, prevProgress, progress);
    }

    private float platePos(float progress){
        return calculateCurPos(this.progress) + 4/16f*(1-progress) - 6/16f;
    }

    public int getLen() {
        return len;
    }

    @Override
    public float calculateStressApplied() {
        if (stored < capacity && !isGenerating) {
            return 128.0f;
        } else if (isGenerating) {
            return -1024;
        }
        return 0f;
    }

    public void onPlace(BlockPos pos, Direction facing, int len) throws AssemblyException {
        this.len = len;
        curLen = len;

        for(int y = 0; y < curLen; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        level.setBlock(calcPos(i, y, j, pos, facing),
                                ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState()
                                        .setValue(FACING, facing),
                                Block.UPDATE_ALL);
                    }
                }
            }
        }

        assemble();
        notifyUpdate();
    }

    public void onBreak(BlockPos pos, Direction facing){

        for(int y = 0; y <= len-1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockPos pos1 = calcPos(i, y, j, pos, facing);
                        level.setBlock(pos1, ModBlocks.LARGE_SPRING_COIL.getDefaultState().setValue(FACING, facing), Block.UPDATE_ALL);
                    }
                }
            }
        }
        for (int i = -1; i < 2; i++){
            for (int j = -1; j < 2; j++){
                if(!(i == 0 && j == 0)){
                    BlockPos pos1 = calcPos(i, 0, j, pos, facing);
                    if(level.getBlockState(pos1).getBlock() == Blocks.AIR){
                        continue;
                    }
                    BlockEntity coil = level.getBlockEntity(pos1);
                    if(coil instanceof SpringCoilBlockEntity){
                        ((SpringCoilBlockEntity) coil).plate = true;
                        ((SpringCoilBlockEntity) coil).plateFacing = facing;
                    }
                }
            }
        }
        for (int i = -1; i < 2; i++){
            for (int j = -1; j < 2; j++){
                if(!(i == 0 && j == 0)){
                    BlockPos pos1 = calcPos(i, len-1, j, pos, facing);
                    if(level.getBlockState(pos1).getBlock() == Blocks.AIR){
                        continue;
                    }
                    BlockEntity coil = level.getBlockEntity(pos1);
                    if(coil instanceof SpringCoilBlockEntity){
                        ((SpringCoilBlockEntity) coil).plate = true;
                        ((SpringCoilBlockEntity) coil).plateFacing = facing.getOpposite();
                    }
                }
            }
        }
    }

    public void assemble() throws AssemblyException {
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof LargeSpringBlock))
            return;

        SpringContraption contraption = new SpringContraption(getBlockState().getValue(FACING));
        boolean canAssembleStructure = contraption.assemble(level, worldPosition);

        if (!canAssembleStructure) {
            return;
        }

        if (contraption.getBlocks().isEmpty()) {
            return;
        }

        movedContraption = ControlledContraptionEntity.create(level, this, contraption);

        movedContraption.setPos(
                CspringsMath.MoveWithoutVectors(
                        calculateCurPos(progress),
                        worldPosition,
                        movementDirection));

        level.addFreshEntity(movedContraption);

        if (contraption.containsBlockBreakers())
            award(AllAdvancements.CONTRAPTION_ACTORS);

        sendData();
    }

    public void disassemble(){

    }

    public static BlockPos calcPos(int x, int y, int z,BlockPos pos, Direction facing){
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

        if(movedContraption != null){
            Vec3 move = getContraptionPos(progress);
            movedContraption.moveTo(move);
        }

        if(isGenerating && splashMode && stored != 0){
            prevProgress = progress;
            progress = springAnimation(phase) * (stored / capacity);

            if(phase < 5){
                pushEntitiesInArea(worldPosition.north().east(),
                        worldPosition.relative(getFacing(), len).west().south());
            }

            phase++;
            if(phase == Config.spring_splash_duration){
                phase = 0;
                stored = 0;
                isGenerating = false;

                for (int i = (len-1); i > curLen; i--){
                    restoreLayer(i, getFacing());
                }

                updateGeneratedRotation();
                notifyUpdate();
            }
            return;
        }

        prevProgress = progress;
        progress = stored / capacity;

        Direction facing = getFacing();
        float platePos = platePos(progress) + 0.5f;

        float CurSpeed = Math.abs(getSpeed());
        if (isGenerating && stored > 0) {
            stored = Math.max(stored - 256, 0);
            updateGeneratedRotation();

            if (platePos > (curLen+1)) {
                curLen++;
                restoreLayer(Mth.floor(platePos(progress)), facing);
            }
        }
        // Режим накопления, если не активировано
        else if (!isGenerating) {
            stored = Mth.clamp(stored + CurSpeed*4, 0, capacity);

            if (platePos < (curLen+1)) {
                removeLayer(Mth.floor(platePos(progress)), facing);
                curLen--;
            }
        }
    }

    public void onExploded(float distance, float power, BlockPos sourcePos){
        int oldLen = Mth.floor(platePos(progress));
        stored += power / distance * 20000 / 9;
        prevProgress = progress;
        progress = stored / capacity;
        int newLen = Mth.floor(platePos(progress));

        if(newLen == oldLen){return;}

        Direction facing = getFacing();

        for(int i = oldLen; i <= newLen; i++){
            removeLayer(i, facing);
        }
    }

    private Vec3 getContraptionPos(float progress){
        return CspringsMath.MoveWithoutVectors(
                platePos(progress),
                worldPosition,
                movementDirection);
    }

    public void pushEntitiesInArea(BlockPos pos1, BlockPos pos2) {
        if (level == null || level.isClientSide) return;

        // Определяем границы области
        BlockPos min = new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ())
        );
        BlockPos max = new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ())
        );

        // Создаём AABB, включая все блоки в области
        AABB area = new AABB(
                min.getX(), min.getY(), min.getZ(),
                max.getX() + 1.0, max.getY() + 1.0, max.getZ() + 1.0
        );

        // Получаем все сущности в области

        for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
            // Толкаем сущности от центра области
            Vec3 center = new Vec3(
                    (area.minX + area.maxX) * 0.5,
                    (area.minY + area.maxY) * 0.5,
                    (area.minZ + area.maxZ) * 0.5
            );

            Vec3 direction = Vec3.atLowerCornerOf(getFacing().getNormal());
            double strength = Config.knockback_coef * stored / capacity * len; // Сила толчка

            entity.setDeltaMovement(direction.scale(strength));
            entity.hurtMarked = true; // Обязательно для синхронизации движения на клиенте
        }
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
                    BlockPos pos1 = calcPos(i, yLevel, j, pos, facing);
                    level.setBlock(
                            pos1,
                            Blocks.COBBLESTONE.defaultBlockState(),
                            Block.UPDATE_ALL
                    );
                    level.setBlock(
                            pos1,
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

    @Override
    public float getGeneratedSpeed() {
        return isGenerating && stored > 0 ? 32.0f : 0.0f;
    }

    public void setGenerating(boolean generating) {
        if(phase > 0){return;}
        phase = 0;

        isGenerating = generating;
        updateGeneratedRotation(); // Обновляем физику
        sendData(); // Синхронизация
    }

    public Direction getFacing(){return getBlockState().getValue(FACING);}

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("Generating", isGenerating);
        compound.putFloat("progres", progress);
        compound.putFloat("stored", stored);
        compound.putInt("len", len);
        compound.putInt("curLen", curLen);
        compound.putInt("phase", phase);
        compound.putBoolean("splashMode", splashMode);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        isGenerating = compound.getBoolean("Generating");
        len = compound.getInt("len");
        progress = compound.getFloat("progres");
        stored = compound.getFloat("stored");
        curLen = compound.getInt("curLen");
        phase = compound.getInt("phase");
        splashMode =  compound.getBoolean("splashMode");
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
