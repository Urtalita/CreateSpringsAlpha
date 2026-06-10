package com.Portality.createsprings.blocks.advanced.spring;

import com.Portality.createsprings.blocks.advanced.AnalogToggleLatch.AnalogLatchBlock;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.config.ModConfigs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
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
import java.util.function.Function;

import static com.Portality.createsprings.blocks.advanced.spring.SpringBlock.getSpringChargeCoefficient;
import static com.Portality.createsprings.compat.SableCompatSpring.pushCreatedSubLevels;
import static com.Portality.createsprings.compat.SableCompatSpring.pushSubLevels;
import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class SpringBlockEntity extends GeneratingKineticBlockEntity implements ThresholdSwitchObservable, IConnectableToPSKI, ISpringBE {

    public final double capacity;
    public double stored = 0;
    private double progress;
    private double prevProgress;
    private boolean isGenerating;
    public boolean splashMode;
    private int phase = 0;
    private float hardness = DEFAULT_HARDNESS;

    public static final float DEFAULT_HARDNESS = 16;
    public ScrollValueBehaviour targetHardness;
    // public SpringPressingBehaviour pressingBehaviour;
    public boolean disableBreakingBlocks = false;
    public UUID createdSubLevel = null;

    public boolean autoMode = false;

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

        updateHardnessSafe(i);
        autoMode = (hardness == 0);
        sendData();
    }


    public int getPhase() {
        return phase;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        int max = 256;

        targetHardness = new ScrollValueBehaviour(Component.translatable("spring.hardness"),
                this, new SpringValueBoxTransform());
        targetHardness.between(0, max);
        targetHardness.value = (int) DEFAULT_HARDNESS;
        targetHardness.withFormatter(this::formatter);
        targetHardness.withCallback(this::updateHardness);

        behaviours.add(targetHardness);


        //pressingBehaviour = new SpringPressingBehaviour(this);
        //behaviours.add(pressingBehaviour);
    }

    private String formatter(Integer integer) {
        if(integer == 0) return "AUTO";
        return Integer.toString(integer);
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

        stored += power / distance * 20000 * coef;
        if(stored > capacity) stored = capacity;
        updateGeneratedRotation();
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

    private float calcStress() {
        if (stored < capacity && !isGenerating) {
            return 2f * hardness;
        } else if (isGenerating && stored >= 2f * hardness) {
            return -2f * hardness;
        }
        return 0;
    }

    private float calculateStressForHardness(float hardness) {
        if (stored < capacity && !isGenerating) {
            return 2f * hardness;
        } else if (isGenerating && stored >= 2f * hardness) {
            return -2f * hardness;
        }
        return 0;
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
            progress = springAnimation(phase) * (stored / capacity);

            if(phase == 1){
                pushSubLevels(this);
                launchEntitiesInFront();
                breakBlocksInFront();
                CSpringsSounds.playBweum(level, worldPosition);
            }

            if(phase == 2){
                pushCreatedSubLevels(this);
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

        if(stored == 0 && prevProgress == progress){
            updateGeneratedRotation();
        }

        prevProgress = progress;
        progress = stored / capacity;

        if(autoMode) calculateIdealHardness();

        if(level.isClientSide){return;}

        if(Mth.floor(progress * 15f) != Mth.floor(prevProgress * 15f)){
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    public void calculateIdealHardness() {
        if (getOrCreateNetwork() == null) return;

        float networkCapacity = getOrCreateNetwork().calculateCapacity();
        float networkStress = getOrCreateNetwork().calculateStress();

        float speed = Math.abs(getTheoreticalSpeed());
        if (Mth.equal(speed, 0)) {
            this.hardness = 0;
            return;
        }

        float myCurrentStressContribution = calcStress() * speed;

        if (isGenerating) {
            float netLeftInNetwork = (networkCapacity - Math.abs(myCurrentStressContribution)) - networkStress;
            float neededCapacity = -netLeftInNetwork;

            if (neededCapacity <= 0) {
                updateHardnessSafe(0);
            } else {
                float idealHardness = neededCapacity / speed / 2f;
                updateHardnessSafe(idealHardness);
            }
        } else {
            float netLeftInNetwork = networkCapacity - (networkStress - myCurrentStressContribution);

            if (netLeftInNetwork <= 0) {
                updateHardnessSafe(0);

            } else {
                if(capacity - stored < hardness * speed * 2) {
                    updateHardnessSafe(0);
                    return;
                }
                float idealHardness = netLeftInNetwork / speed / 2f;
                updateHardnessSafe(idealHardness);
            }
        }
    }

    public void updateHardnessSafe(float newHardness) {
        newHardness = Math.max(0, newHardness);

        if (!Mth.equal(this.hardness, newHardness)) {
            this.hardness = newHardness;
            if (getOrCreateNetwork() != null) {
                getOrCreateNetwork().remove(this);
                getOrCreateNetwork().add(this);
                getOrCreateNetwork().updateNetwork();
                sendData();
            }
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

        CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(
                        CreateLang.number(Math.round(stored)).style(ChatFormatting.AQUA).space()
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                        .add(CreateLang.number(ModConfigs.common().SPRING_CAPACITY.get()).style(ChatFormatting.AQUA).space()
                                .add(CreateLang.translate("spring.su").style(ChatFormatting.DARK_GRAY))))
                .forGoggles(tooltip);

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
            addRemainingTime(tooltip, isPlayerSneaking);

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

            addRemainingTime(tooltip, isPlayerSneaking);
        }

        return true;
    }

    public void addRemainingTime(List<Component> tooltip, boolean isPlayerSneaking) {
        if(Mth.equal(getSpeed(), 0)) return;

        double rate = Math.abs((progress - prevProgress) * capacity);
        double left = isGenerating ? stored : capacity - stored;
        double ticks = left / rate;

        int totalSeconds = (int) Math.floor(ticks / 20);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if(totalSeconds != 0){
            MutableComponent remainingTime = Component.literal("");

            if(hours != 0) remainingTime.append(Component.literal(hours + "h ").withStyle(ChatFormatting.AQUA));
            if(minutes != 0) remainingTime.append(Component.literal(minutes + "m ").withStyle(ChatFormatting.AQUA));
            remainingTime.append(Component.literal(seconds + "s ").withStyle(ChatFormatting.AQUA));

            CreateLang.text(" ").add(
                            CreateLang.translate("createsprings.time_left").style(ChatFormatting.DARK_GRAY).add(remainingTime))
                    .forGoggles(tooltip);
        }
    }

    public static float springAnimation(int phase) {
        if (phase == 0) {return 1.0f;}
        if (phase == ModConfigs.common().SPRING_SPLASH_DURATION.get()){return 0f;}

        float decay = (float) Math.exp(-0.15 * phase);

        float frequency = (float) (Math.PI * 0.4);

        float oscillation = (float) Math.cos((frequency * phase + Math.PI)/2);

        return decay * oscillation * 2f;
    }

    public float getProgress(float pt) {
        return (float) Mth.lerp(pt, prevProgress, progress);
    }

    @Override
    public float getGeneratedSpeed() {
        if (isGenerating && !splashMode && stored > 0){
            if (level != null) {
                int signal = level.getBestNeighborSignal(worldPosition);
                return 8 + signal * 8;
            }
        }
        return 0;
    }

    public void setGenerating(boolean generating) {
        if (phase > 0) return;

        boolean wasGenerating = isGenerating;
        isGenerating = generating;

        updateGeneratedRotation();

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

        if(Sable.HELPER.getContaining(this) instanceof ServerSubLevel serverSubLevel){
            vector = new Vec3(1, 1, 1).scale(vector.length());
            Vec3 vectorFrom = serverSubLevel.logicalPose().transformPosition(worldPosition.getCenter());
            Vec3 vectorTo = serverSubLevel.logicalPose().transformPosition(getFront().getCenter());
            vector = vectorTo.subtract(vectorFrom).multiply(vector);
        }

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

    private class SpringValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 4, 15.5f);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Vec3 location = getSouthLocation();

            location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
            location = VecHelper.rotateCentered(location, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);

            Direction springDirection = state.getValue(FACING);
            Direction.Axis sideAxis = getSide().getAxis();
            float angle = 180;
            if(springDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE) angle += 180;
            if(springDirection.getAxis() == Direction.Axis.X) angle -= 90;
            if(springDirection.getAxis() == Direction.Axis.Z) angle += 90;

            if(springDirection.getAxis() == Direction.Axis.Z && getSide() == Direction.UP) angle += 90;
            if(springDirection.getAxis() == Direction.Axis.Z && getSide() == Direction.DOWN) angle -= 90;

            location = VecHelper.rotateCentered(location, angle, sideAxis);

            return location;
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

    //sable compat

    public Vec3 getVectorSpeedForLaunch() {
        Direction facing = getBlockState().getValue(FACING).getOpposite();

        Vec3 direction = new Vec3(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );

        double energyRatio = (double) stored / ModConfigs.common().SPRING_CAPACITY.get();
        double speedModifier = Math.sqrt(Math.max(0, energyRatio));
        return direction.scale(ModConfigs.common().KNOCKBACK_COEF.get())
                .scale(speedModifier);
    }
}