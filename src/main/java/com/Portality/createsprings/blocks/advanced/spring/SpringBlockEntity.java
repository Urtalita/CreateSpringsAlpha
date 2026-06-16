package com.Portality.createsprings.blocks.advanced.spring;

import com.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import com.Portality.createsprings.client.CSpringsLang;
import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.compat.SableCompatAbstractionLayer;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

import static com.Portality.createsprings.blocks.advanced.spring.SpringBlock.getSpringChargeCoefficient;
import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class SpringBlockEntity extends GeneratingKineticBlockEntity implements ThresholdSwitchObservable, IConnectableToPSKI, ISpringBE {

    public double capacity;
    public double stored = 0;
    private double progress;
    private double prevProgress;
    private boolean isGenerating;
    public boolean splashMode;
    private int phase = 0;
    private float hardness = DEFAULT_HARDNESS;

    public static final float DEFAULT_HARDNESS = 0;
    public SpringValueBehavior targetHardness;
    // public SpringPressingBehaviour pressingBehaviour;
    public boolean disableBreakingBlocks = false;
    public UUID createdSubLevel = null;

    public boolean reverseRotation = false;
    public boolean autoMode = true;

    public SpringBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        capacity = ModConfigs.common().SPRING_CAPACITY.get();
    }

    private void updateHardness(int i) {
        if (level == null || level.isClientSide) return;

        sendData();
        setChanged();
        updateNetwork();
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());

        reverseRotation = i < 0;

        updateHardnessSafe(Math.abs(i) - 1);
        autoMode = (hardness < 1);
        updateGeneratedRotation();
        sendData();
    }


    public int getPhase() {
        return phase;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = 256 + 1;

        targetHardness = new SpringValueBehavior(Component.translatable("spring.hardness"),
                this, new DirectionalSidedValueBox((s) -> s.getValue(FACING).getOpposite(), 8, 4));
        targetHardness.between(-max, max);
        targetHardness.value = (int) DEFAULT_HARDNESS;
        targetHardness.withFormatter(this::formatter);
        targetHardness.withCallback(this::updateHardness);

        behaviours.add(targetHardness);
    }

    public void onBlockExploded(BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.center();
        Vec3 BlPos = pos.getCenter();

        Vec3 distVector = BlPos.subtract(ExpPos);
        double distance = (double) distVector.length();
        double power = 4;
        Direction facing = getBlockState().getValue(FACING);
        double coef = getSpringChargeCoefficient(facing, pos, ExpPos);

        if(coef < 0.30f){
            return;
        }

        stored += power / distance * 100000 * coef;
        if(stored > capacity) stored = capacity;
        updateGeneratedRotation();
    }

    @Override
    public GeneratingKineticBlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public void setHardness(double hardness) {
        this.hardness = (float) hardness;
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
        if(isGenerating){
            stressApplied = 0;
        }
        this.lastStressApplied = stressApplied;
        return stressApplied;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = -calcStress();
        if(!isGenerating){
            capacity = 0;
        }
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    public float calcStress() {
        if (stored < capacity && !isGenerating) {
            return 2f * hardness;
        } else if (isGenerating && stored >= 2f * hardness) {
            return -2f * hardness;
        }
        return 0;
    }

    @Override
    public boolean isGenerating() {
        return isGenerating;
    }

    public Direction getFacing(){
        return getBlockState().getValue(FACING).getOpposite();
    }

    public BlockPos getFront(){
        return worldPosition.relative(getFacing());
    }

    @Override
    public void tick() {
        super.tick();

        if(isGenerating && splashMode && stored != 0){
            prevProgress = progress;
            progress = ISpringBE.springAnimation(phase) * (stored / capacity);

            if(phase == 1){
                SableCompatAbstractionLayer.pushSubLevels(this);
                launchEntitiesInFront();
                breakBlocksInFront();
                CSpringsSounds.playBweum(level, worldPosition);
            }

            if(phase == 2){
                SableCompatAbstractionLayer.pushCreatedSubLevels(this);
            }

            phase++;
            if(phase == ModConfigs.common().SPRING_SPLASH_DURATION.get()){
                phase = 0;
                stored = 0;
                isGenerating = false;
                updateGeneratedRotation();
                notifyUpdate();
            }
            return;
        }

        double CurSpeed = Math.abs(getSpeed());

        if (isGenerating && stored >= 0) {
            stored = Math.max(stored - CurSpeed * hardness * 2 / 20f, 0);
        }

        else if (!isGenerating) {
            stored = Mth.clamp(stored + CurSpeed * hardness * 2 / 20f, 0, capacity);
        }

        if(stored == 0 && prevProgress != progress){
            updateGeneratedRotation();
        }

        if(stored == capacity && prevProgress != progress){
            updateNetwork();
        }

        prevProgress = progress;
        progress = stored / capacity;

        if(autoMode) calculateIdealHardness(isGenerating);

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
            network.updateStress();
        }
        super.remove();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Generating", isGenerating);
        tag.putFloat("Stored", (float) stored);
        tag.putInt("phase", phase);
        tag.putBoolean("splashMode", splashMode);
        tag.putFloat("hardness", hardness);
        tag.putBoolean("auto", autoMode);
        tag.putBoolean("reverseRotation", reverseRotation);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag,registries, clientPacket);
        isGenerating = tag.getBoolean("Generating");
        stored = tag.getFloat("Stored");
        phase = tag.getInt("phase");
        splashMode = tag.getBoolean("splashMode");
        hardness = tag.getFloat("hardness");
        autoMode = tag.getBoolean("auto");
        reverseRotation = tag.getBoolean("reverseRotation");

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
        //stored

        if(splashMode){
            addChargeInSplashMode(tooltip, isPlayerSneaking, progress);
        } else {
            CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.text(" ").add(
                            CSpringsLang.transformTime(stored)
                    ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                            .add(CSpringsLang.transformTime(capacity)))
                    .forGoggles(tooltip);
        }

        MutableComponent state = (splashMode) ?
                Component.translatable("createsprings.on").withStyle(ChatFormatting.GREEN) :
                Component.translatable("createsprings.off").withStyle(ChatFormatting.RED);

        CreateLang.translate("createsprings.splash_mode").style(ChatFormatting.GRAY)
                .add(Component.literal(" ").append(state)).forGoggles(tooltip);


        if(!isGenerating){
            if (!IRotate.StressImpact.isEnabled())
                return true;
            float stressAtBase = calculateStressApplied();
            if (Mth.equal(stressAtBase, 0))
                return true;

            CreateLang.translate("gui.goggles.kinetic_stats")
                    .forGoggles(tooltip);

            addStressImpactStats(tooltip, stressAtBase);
            addRemainingTime(tooltip, isPlayerSneaking, isGenerating, progress, prevProgress);

        } else {
            //generator

            if (!IRotate.StressImpact.isEnabled())
                return true;

            float stressBase = calculateAddedStressCapacity();
            if (Mth.equal(stressBase, 0))
                return true;

            CreateLang.translate("gui.goggles.generator_stats")
                    .forGoggles(tooltip);
            CreateLang.translate("tooltip.capacityProvided")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            float speed = getTheoreticalSpeed();
            if (speed != getGeneratedSpeed() && speed != 0)
                stressBase *= getGeneratedSpeed() / speed;

            float stressTotal = Math.abs(stressBase * speed);

            CreateLang.number(stressTotal)
                    .translate("generic.unit.stress")
                    .style(ChatFormatting.AQUA)
                    .space()
                    .add(CreateLang.translate("gui.goggles.at_current_speed")
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);

            addRemainingTime(tooltip, isPlayerSneaking, isGenerating, progress, prevProgress);
        }

        return true;
    }

    public float getProgress(float pt) {
        return (float) Mth.lerp(pt, prevProgress, progress);
    }

    @Override
    public float getGeneratedSpeed() {
        if (isGenerating && !splashMode && stored > 0){
            if (level != null) {
                int signal = level.getBestNeighborSignal(worldPosition);
                int multiplayer = 1;
                if(reverseRotation) multiplayer = -1;
                return (8 + signal * 8) * multiplayer;
            }
        }
        return 0;
    }

    public void setGenerating(boolean generating) {
        if (phase > 0) return;

        boolean wasGenerating = isGenerating;
        isGenerating = generating;

        updateGeneratedRotation();

        if(autoMode){
            updateHardnessSafe(0);
            calculateIdealHardness(isGenerating);
        }

        if (wasGenerating != isGenerating) {
            updateNetwork();
            sendData();
        }
    }

    public void launchEntitiesInFront() {
        if (level == null || level.isClientSide) return;

        Direction facing = getBlockState().getValue(FACING).getOpposite();
        BlockPos targetPos = worldPosition.relative(facing);

        AABB searchArea = new AABB(targetPos);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, searchArea);

        Vec3 vector = getVectorSpeedForLaunch();

        if(SableCompatAbstractionLayer.launchEntitiesInFront(this, vector)) return;

        for (Entity entity : entities) {
            entity.addDeltaMovement(vector);
            entity.hurtMarked = true;
        }
    }

    private void breakBlocksInFront(){
        if(disableBreakingBlocks){
            disableBreakingBlocks = false;
            return;
        }

        BlockPos pos = worldPosition.relative(getBlockState().getValue(FACING).getOpposite());
        BlockState breakState = level.getBlockState(pos);

        if(breakState.getBlock() instanceof PressurePlateBlock){return;}
        if(breakState.getBlock() instanceof KineticBlock){return;}
        if(breakState.getBlock() == AllBlocks.DEPOT.get()){return;}

        breakBySpring(pos, level, (float) stored);
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
        return (float) stored;
    }

    @Override
    public float getCapacity() {
        return (float) capacity;
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

    //sable compat

    public Vec3 getVectorSpeedForLaunch() {
        Direction facing = getBlockState().getValue(FACING).getOpposite();

        Vec3 direction = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );

        double energyRatio = (double) (stored * 16) / ModConfigs.common().SPRING_CAPACITY.get();
        double speedModifier = Math.sqrt(Math.max(0, energyRatio));
        return direction.scale(ModConfigs.common().KNOCKBACK_COEF.get())
                .scale(speedModifier);
    }
}