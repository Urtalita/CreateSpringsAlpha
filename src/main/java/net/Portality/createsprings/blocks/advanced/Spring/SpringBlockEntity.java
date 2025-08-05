package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class SpringBlockEntity extends GeneratingKineticBlockEntity implements ThresholdSwitchObservable, IConnectableToPSKI {

    public final float capacity;
    public float stored = 0;
    private float progress;
    private float prevProgress;
    private boolean isGenerating;
    public boolean splashMode;
    private int phase = 0;
    private float hardness = DEFAULT_HARDNESS;

    public static final float DEFAULT_HARDNESS = 16;
    public ScrollValueBehaviour targetHardness;

    public SpringBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        capacity = Config.spring_capacity;
    }

    public void setHardness(int hardness){
        targetHardness.setValue(hardness);
        updateHardness(hardness);
    }

    private void updateHardness(int i) {
        if (level == null || level.isClientSide) return;

        if (hardness != i) {
            hardness = i;
            sendData();
            setChanged();
            updateNetwork();
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        Integer max = AllConfigs.server().kinetics.maxRotationSpeed.get();

        targetHardness = new ScrollValueBehaviour(Component.translatable("spring.hardness"),
                this, new SpringBlockEntity.SpringValueBoxTransform());
        targetHardness.between(1, max);
        targetHardness.value = (int) DEFAULT_HARDNESS;
        targetHardness.withCallback(this::updateHardness);

        behaviours.add(targetHardness);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    @Override
    public float getSpeed() {
        return super.getSpeed();
    }

    @Override
    public float calculateStressApplied() {
        float stressApplied = calcStress();
        this.lastStressApplied = stressApplied;
        return stressApplied;
    }

    private float calcStress() {
        if (stored < capacity && !isGenerating) {
            return 2f * hardness;
        } else if (isGenerating && stored >= 2f * hardness) {
            return -2f * hardness;
        }
        return 0;
    }

    @Override
    public void tick() {
        super.tick();

        if(isGenerating && splashMode && stored != 0){
            prevProgress = progress;
            progress = springAnimation(phase) * (stored / capacity);

            if(phase == 1){
                launchEntitiesInFront();
                breakBlocksInFront();
            }

            phase++;
            if(phase == Config.spring_splash_duration){
                phase = 0;
                stored = 0;
                isGenerating = false;
                updateGeneratedRotation();
                notifyUpdate();
            }
            return;
        }

        float CurSpeed = Math.abs(getSpeed());

        if (isGenerating && stored >= 0) {
            stored = Math.max(stored - CurSpeed / DEFAULT_HARDNESS * hardness, 0);
        }

        else if (!isGenerating) {
            stored = Mth.clamp(stored + CurSpeed / DEFAULT_HARDNESS * hardness, 0, capacity);
        }

        prevProgress = progress;
        progress = stored / capacity;

        if(level.isClientSide){return;}

        if(Mth.floor(progress * 15f) != Mth.floor(prevProgress * 15f)){
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    @Override
    public void remove() {
        if (!level.isClientSide && hasNetwork()) {
            KineticNetwork network = getOrCreateNetwork();
            network.remove(this);
            network.updateStress(); // Явное обновление
        }
        super.remove();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Generating", isGenerating);
        tag.putFloat("Stored", stored);
        tag.putInt("phase", phase);
        tag.putBoolean("splashMode", splashMode);
        tag.putFloat("hardness", hardness);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        isGenerating = tag.getBoolean("Generating");
        stored = tag.getFloat("Stored");
        phase = tag.getInt("phase");
        splashMode = tag.getBoolean("splashMode");
        hardness = tag.getFloat("hardness");

        if (!clientPacket) {
            updateNetwork();
        }
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
        CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(
                        CreateLang.number(stored).style(ChatFormatting.AQUA).space()
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                        .add(CreateLang.number(Config.spring_capacity).style(ChatFormatting.AQUA).space()
                                .add(CreateLang.translate("spring.su").style(ChatFormatting.DARK_GRAY))))
                .forGoggles(tooltip);
        return true;
    }

    public static float springAnimation(int phase) {
        if (phase == 0) {return 1.0f;}
        if (phase == Config.spring_splash_duration){return 0f;}

        float decay = (float) Math.exp(-0.15 * phase);

        float frequency = (float) (Math.PI * 0.4);

        float oscillation = (float) Math.cos((frequency * phase + Math.PI)/2);

        return decay * oscillation * 2f;
    }

    public float getProgress(float pt) {
        return Mth.lerp(pt, prevProgress, progress);
    }

    @Override
    public float getGeneratedSpeed() {
        return isGenerating && !splashMode && stored > 0 ? 16.0f : 0.0f;
    }

    public void setGenerating(boolean generating) {
        if (phase > 0) return;

        boolean wasGenerating = isGenerating;
        isGenerating = generating;

        if (wasGenerating != isGenerating) {
            updateNetwork();
            updateGeneratedRotation();
            sendData();
        }
    }

    public void launchEntitiesInFront() {
        if (level == null || level.isClientSide) return;

        Direction facing = getBlockState().getValue(FACING).getOpposite();
        BlockPos targetPos = worldPosition.relative(facing);

        AABB searchArea = new AABB(targetPos);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, searchArea);

        for (Entity entity : entities) {
            Vec3 direction = new Vec3(
                    facing.getStepX(),
                    facing.getStepY(),
                    facing.getStepZ()
            ).scale(1.0);

            entity.setDeltaMovement(direction.scale(Config.knockback_coef).scale(stored / Config.spring_capacity));
            entity.hurtMarked = true;
        }
    }

    private void breakBlocksInFront(){
        BlockPos pos = worldPosition.relative(getBlockState().getValue(FACING).getOpposite());
        BlockState breakState = level.getBlockState(pos);
        if(breakState.getBlock() instanceof PressurePlateBlock){return;}
        if(breakState.getBlock() instanceof KineticBlock){return;}
        breakBySpring(pos, level, stored);
    }

    public static boolean breakBySpring(BlockPos pos, Level level, Float stored){
        BlockState breakState = level.getBlockState(pos);
        float blockHardness = breakState.getDestroySpeed(level, pos);

        if(breakState.getBlock() instanceof AirBlock){return true;}
        if(breakState.liquid()){return true;}

        if(!(stored >= blockHardness / 50 * 160000)){return false;}
        if(!BlockBreakingKineticBlockEntity.isBreakable(breakState, blockHardness)){return false;}

        level.playSound(null, pos, breakState.getSoundType()
                .getHitSound(), SoundSource.BLOCKS, .25f, 1);
        onBlockBroken(breakState, pos, level);
        return true;
    }

    public static boolean canBreakBySpring(BlockPos pos, Level level, Float stored){
        BlockState breakState = level.getBlockState(pos);
        float blockHardness = breakState.getDestroySpeed(level, pos);

        if(breakState.getBlock() instanceof AirBlock){return true;}
        if(breakState.liquid()){return true;}

        if(!(stored >= blockHardness / 50 * 160000)){return false;}
        if(!BlockBreakingKineticBlockEntity.isBreakable(breakState, blockHardness)){return false;}
        return true;
    }

    public static void onBlockBroken(BlockState stateToBreak, BlockPos breakingPos, Level level) {
        Vec3 vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), level.random, .125f);
        BlockHelper.destroyBlock(level, breakingPos, 1f, (stack) -> {
            if (stack.isEmpty())
                return;
            if (!level.getGameRules()
                    .getBoolean(GameRules.RULE_DOBLOCKDROPS))
                return;
            if (level.restoringBlockSnapshots)
                return;

            ItemEntity itementity = new ItemEntity(level, vec.x, vec.y, vec.z, stack);
            itementity.setDefaultPickUpDelay();
            itementity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(itementity);
        });
    }

    public int getComparatorOutput() {
        return Mth.floor(progress * 15);
    }

    @Override
    public int getMaxValue() {
        return (int) capacity / 1000;
    }

    @Override
    public int getMinValue() {
        return 0;
    }

    @Override
    public int getCurrentValue() {
        return (int) (progress * capacity / 1000);
    }

    @Override
    public MutableComponent format(int value) {
        return CreateLang.number(value)
                .add(Component.literal(" "))
                .add(CreateLang.translate("spring.switch.su"))
                .component();
    }

    //IConnectableToPKI
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
        this.stored = newStored;
    }

    @Override
    public float getHardness() {
        return hardness;
    }

    @Override
    public float getImpactCof() {
        return 2;
    }

    private class SpringValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5f);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return state.getValue(FACING).getAxis() != direction.getAxis();
        }

        @Override
        public float getScale() {
            return 0.5f;
        }

    }
}