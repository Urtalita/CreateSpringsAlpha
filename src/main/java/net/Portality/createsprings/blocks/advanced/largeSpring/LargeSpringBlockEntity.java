package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.CreateLang;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlockEntity;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import net.Portality.createsprings.contraption.SpringContraption;
import net.Portality.createsprings.utill.Helpers.CspringsMath;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.*;
import static net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock.LEN;

public class LargeSpringBlockEntity extends GeneratingKineticBlockEntity implements IControlContraption, IConnectableToPSKI {

    public float progress;
    public float stored = 0;
    private int len;

    private int curLen = len;
    private boolean isGenerating;
    protected ControlledContraptionEntity movedContraption;
    private final Vec3i movementDirection;
    public boolean splashMode = false;
    private int phase = 0;
    public float capacity;
    private float hardness = DEFAULT_HARDNESS;

    private BlockPos stoppedPos = null;

    public float prevProgress;

    public LargeSpringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.movementDirection = getBlockState().getValue(FACING).getOpposite().getNormal();
        if(Config.debug_capacity){
            capacity = Config.spring_capacity / 2;
        } else {
            capacity = Config.spring_capacity * 9 * Config.large_spring_capacity_coef;
        }
    }

    public BlockPos getStoppedPos() {
        return stoppedPos;
    }

    public int getCurLen() {
        return curLen;
    }

    public float getProgres(float partalTicks) {
        if(prevProgress == 0){
            prevProgress = progress;
        }
        return Mth.lerp(partalTicks, prevProgress, progress);
    }

    public float getPlatePos(){
        return platePos(progress);
    }

    private float platePos(float progress){
        return calculateCurPos(this.progress) + 4/16f*(1-progress) - 6/16f;
    }

    public int getLen() {
        return len;
    }

    public void setHardness(int value){
        if (level == null || level.isClientSide) return;

        if (hardness != value) {
            hardness = value;
            sendData();
            setChanged();
            updateNetwork();
        }
    }

    @Override
    public float calculateStressApplied() {
        float stressApplied = calcStress();
        this.lastStressApplied = stressApplied;
        return stressApplied;
    }

    private float calcStress() {
        if (stored < capacity && !isGenerating) {
            return 2f * hardness * 9;
        } else if (isGenerating && stored >= 2f * hardness) {
            return -2f * hardness * 9;
        }
        return 0;
    }

    public void onPlace(BlockPos pos, Direction facing, int len) throws AssemblyException {
        this.len = len;
        curLen = len;

        if(!Config.debug_capacity){
            capacity = capacity * len;
        }

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

        updateGeneratedRotation();
        notifyUpdate();
        sendData();
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

        SpringContraption contraption = new SpringContraption(getFacing());
        boolean canAssembleStructure = contraption.assemble(level, worldPosition);

        if (!canAssembleStructure) {
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

        if(len == 0){
            len = getBlockState().getValue(LEN);
            curLen = len;
            capacity = capacity * len;
        }

        if(stoppedPos != null && isGenerating){
            if(breakBySpring(stoppedPos, level, (float) Config.spring_capacity)){
                stoppedPos = null;
                breakBlocksInLayer(Mth.floor(platePos(progress) + 1), getFacing());
            }
            return;
        }

        if(isGenerating && splashMode && stored > 1){
            prevProgress = progress;
            progress = springAnimation(phase) * (stored / capacity);

            if(phase == 0){
                if(movedContraption != null){
                    movedContraption.moveTo(Vec3.atLowerCornerOf(worldPosition));
                }
            }

            if(phase < 5){
                pushEntitiesInArea(worldPosition.north().east(),
                        worldPosition.relative(getFacing(), len).west().south());
            }

            phase++;
            if(phase == Config.spring_splash_duration){
                phase = 0;
                stored = 1;

                for (int i = (len-1); i > curLen; i--){
                    breakBlocksInLayer(i, getFacing());
                }

                for (int i = (len-1); i > curLen; i--){
                    restoreLayer(i, getFacing());
                }

                curLen = len;

                boolean hasSignal = level.hasNeighborSignal(worldPosition);
                setGenerating(hasSignal);

                updateGeneratedRotation();
                notifyUpdate();
            }
            return;
        }

        if(movedContraption != null){
            Vec3 move = getContraptionPos(progress);
            movedContraption.moveTo(move);
        }

        prevProgress = progress;
        progress = stored / capacity;

        Direction facing = getFacing();
        float platePos = platePos(progress) + 0.5f;

        float CurSpeed = Math.abs(getSpeed());
        if (isGenerating && stored > 0) {
            stored = Math.max(stored - CurSpeed / DEFAULT_HARDNESS * hardness * 9, 0);
            if (platePos > (curLen+1)) {
                curLen++;
                breakBlocksInLayer(Mth.floor(platePos(progress) + 1), facing);
                restoreLayer(Mth.floor(platePos(progress)), facing);
            }
        }
        // Режим накопления, если не активировано
        else if (!isGenerating) {
            stored = Math.min(stored + CurSpeed / DEFAULT_HARDNESS * hardness * 9, capacity);
            if (platePos < (curLen+1)) {
                removeLayer(Mth.floor(platePos(progress)), facing);
                curLen--;
            }
        }
        if(stored ==0 && this.speed != 0){
            updateNetwork();
            updateGeneratedRotation();
        }
    }

    public void onExploded(float distance, float power, BlockPos sourcePos){
        int oldLen = Mth.floor(platePos(progress));
        stored += power / distance * 40000 / 9;
        if(stored > capacity){stored = capacity;}
        prevProgress = progress;
        progress = stored / capacity;
        int newLen = Mth.floor(platePos(progress));

        if(newLen == oldLen){return;}

        Direction facing = getFacing();

        for(int i = newLen; i <= oldLen; i++){
            removeLayer(i, facing);
            curLen--;
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

    private float calculateCurPos(float progres){
        return len - progres * (len * 0.5f);
    }

    @Override
    public float getGeneratedSpeed() {
        if(level.getBestNeighborSignal(worldPosition) == 0){return 0;}
        float stress = 16.0f * level.getBestNeighborSignal(worldPosition) + 16;
        return isGenerating && stored > 0 ? stress : 0.0f;
    }

    public void setGenerating(boolean generating) {
        if(phase > 0){return;}
        phase = 0;

        isGenerating = generating;
        updateGeneratedRotation();
        updateNetwork();
        sendData(); // Синхронизация
    }

    public Direction getFacing(){return getBlockState().getValue(FACING);}

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if(stoppedPos != null){
            compound.putInt("stoppedX", stoppedPos.getX());
            compound.putInt("stoppedY", stoppedPos.getY());
            compound.putInt("stoppedZ", stoppedPos.getZ());
        }

        compound.putBoolean("Generating", isGenerating);
        compound.putFloat("progres", progress);
        compound.putFloat("stored", stored);
        compound.putInt("len", len);
        compound.putInt("curLen", curLen);
        compound.putInt("phase", phase);
        compound.putBoolean("splashMode", splashMode);
        compound.putFloat("capacity", capacity);
        compound.putFloat("hardness", hardness);
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
        capacity = compound.getFloat("capacity");
        hardness = compound.getFloat("hardness");

        if(compound.contains("stoppedX")){
            stoppedPos = new BlockPos(
                    compound.getInt("stoppedX"),
                    compound.getInt("stoppedY"),
                    compound.getInt("stoppedZ")
            );
        }
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
                    BlockPos breakBlock = calcPos(i, yLevel, j, pos, facing);
                    level.setBlock(
                            breakBlock,
                            ModBlocks.LARGE_SPRING_EXTENTION.get().defaultBlockState().setValue(FACING, facing),
                            Block.UPDATE_ALL
                    );

                    if(level.getBlockEntity(breakBlock) instanceof ExtentionBlockEntity extentionBlockEntity){
                        extentionBlockEntity.targetHardness.setValue((int) hardness);
                    }
                }
            }
        }
    }

    private void breakBlocksInLayer(int yLevel, Direction facing){
        BlockPos pos = getBlockPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!(i == 0 && j == 0)) {
                    BlockPos breakBlock = calcPos(i, yLevel, j, pos, facing);
                    if(!breakBySpring(breakBlock, level, (float) Config.spring_capacity)){
                        stoppedPos = breakBlock;
                    }
                }
            }
        }
    }

    public boolean canDisassemble(Direction facing){
        BlockPos pos = getBlockPos();
        for (int yLevel = (len-1); yLevel > curLen; yLevel--){
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (!(i == 0 && j == 0)) {
                        BlockPos breakBlock = calcPos(i, yLevel, j, pos, facing);
                        if(!canBreakBySpring(breakBlock, level, (float) Config.spring_capacity)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void updateNetwork() {
        if (level == null || level.isClientSide || isRemoved()) return;

        if (hasNetwork()) {
            getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        if (this.stoppedPos != null){
            CreateLang.translate("spring.stopped").style(ChatFormatting.YELLOW).forGoggles(tooltip);
            return true;
        }

        CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(
                        CreateLang.number(stored).style(ChatFormatting.AQUA).space()
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                        .add(CreateLang.number(capacity).style(ChatFormatting.AQUA).space()
                                .add(CreateLang.translate("spring.su").style(ChatFormatting.DARK_GRAY))))
                .forGoggles(tooltip);

        CreateLang.translate("spring.len").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(
                        CreateLang.number(Math.round(getPlatePos())).style(ChatFormatting.AQUA).space()
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                        .add(CreateLang.number(len).style(ChatFormatting.AQUA).space()))
                .forGoggles(tooltip);
        return true;
    }

    @Override
    public float getStored() {
        return stored;
    }

    @Override
    public float getCapacity() {
        return capacity;
    }

    @Override
    public void setStored(float newStored) {
        stored = newStored;
    }

    @Override
    public float getHardness() {
        return hardness;
    }

    @Override
    public float getImpactCof() {
        return 18;
    }
}