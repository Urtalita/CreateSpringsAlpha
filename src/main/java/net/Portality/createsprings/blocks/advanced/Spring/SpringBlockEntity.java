package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
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
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.blocks.advanced.kinetic_interface.IConnectableToPSKI;
import net.Portality.createsprings.sounds.CSpringsSounds;
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
import net.minecraft.world.level.Explosion;
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
import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlock.getSpringChargeCoefficient;

public class SpringBlockEntity extends GeneratingKineticBlockEntity implements ThresholdSwitchObservable, IConnectableToPSKI, ISpringBE {

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
    // public SpringPressingBehaviour pressingBehaviour;

    public SpringBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        capacity = ModConfigs.common().SPRING_CAPACITY.get();
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


        //pressingBehaviour = new SpringPressingBehaviour(this);
        //behaviours.add(pressingBehaviour);
    }

    public void onBlockExploded(BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.getPosition();
        Vec3 BlPos = pos.getCenter();

        Vec3 distVector = BlPos.subtract(ExpPos);
        float distance = (float) distVector.length();
        float power = 4;
        Direction facing = getBlockState().getValue(FACING);
        float coef = getSpringChargeCoefficient(facing, pos, ExpPos);

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

    @Override
    public void tick() {
        super.tick();

        if(isGenerating && splashMode && stored != 0){
            prevProgress = progress;
            progress = springAnimation(phase) * (stored / capacity);

            if(phase == 1){
                launchEntitiesInFront();
                breakBlocksInFront();
                CSpringsSounds.playBweum(level, worldPosition);
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

        float CurSpeed = Math.abs(getSpeed());

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
        //stored

        CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(" ").add(
                        CreateLang.number(Math.round(stored)).style(ChatFormatting.AQUA).space()
                ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                        .add(CreateLang.number(ModConfigs.common().SPRING_CAPACITY.get()).style(ChatFormatting.AQUA).space()
                                .add(CreateLang.translate("spring.su").style(ChatFormatting.DARK_GRAY))))
                .forGoggles(tooltip);

        boolean added = false;
        if(!isGenerating){
            if (!IRotate.StressImpact.isEnabled())
                return added;
            float stressAtBase = calculateStressApplied();
            if (Mth.equal(stressAtBase, 0))
                return added;

            CreateLang.translate("gui.goggles.kinetic_stats")
                    .forGoggles(tooltip);

            addStressImpactStats(tooltip, stressAtBase);
        } else {
            //generator

            if (!IRotate.StressImpact.isEnabled())
                return added;

            float stressBase = calculateAddedStressCapacity();
            if (Mth.equal(stressBase, 0))
                return added;

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
        }

        return true;
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

            entity.addDeltaMovement(direction.scale(ModConfigs.common().KNOCKBACK_COEF.get()).scale(stored / ModConfigs.common().SPRING_CAPACITY.get()));
            entity.hurtMarked = true;
        }
    }

    private void breakBlocksInFront(){
        BlockPos pos = worldPosition.relative(getBlockState().getValue(FACING).getOpposite());
        BlockState breakState = level.getBlockState(pos);

        if(breakState.getBlock() instanceof PressurePlateBlock){return;}
        if(breakState.getBlock() instanceof KineticBlock){return;}
        if(breakState.getBlock() == AllBlocks.DEPOT.get()){return;}

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

    //PressingBehaviorSpecifics

    /*
    @Override
    public boolean tryProcessInBasin(boolean simulate) {return false;}

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<PressingRecipe> recipe = getRecipe(input.stack);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;
        pressingBehaviour.particleItems.add(input.stack);
        List<ItemStack> outputs = RecipeApplier.applyRecipeOn(level,
                canProcessInBulk() ? input.stack : ItemHandlerHelper.copyStackWithSize(input.stack, 1), recipe.get());

        for (ItemStack created : outputs) {
            if (!created.isEmpty()) {
                onItemPressed(created);
                break;
            }
        }

        outputList.addAll(outputs);
        return true;
    }

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        ItemStack item = itemEntity.getItem();
        Optional<PressingRecipe> recipe = getRecipe(item);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;

        ItemStack itemCreated = ItemStack.EMPTY;
        pressingBehaviour.particleItems.add(item);
        if (canProcessInBulk() || item.getCount() == 1) {
            RecipeApplier.applyRecipeOn(itemEntity, recipe.get());
            itemCreated = itemEntity.getItem()
                    .copy();
        } else {
            for (ItemStack result : RecipeApplier.applyRecipeOn(level, ItemHandlerHelper.copyStackWithSize(item, 1),
                    recipe.get())) {
                if (itemCreated.isEmpty())
                    itemCreated = result.copy();
                ItemEntity created =
                        new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                created.setDefaultPickUpDelay();
                created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
                level.addFreshEntity(created);
            }
            item.shrink(1);
        }

        if (!itemCreated.isEmpty())
            onItemPressed(itemCreated);
        return true;
    }

    @Override
    public boolean canProcessInBulk() {
        return AllConfigs.server().recipes.bulkPressing.get();
    }

    @Override
    public void onPressingCompleted() {}

    @Override
    public int getParticleAmount() {
        return 15;
    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }

    private static final RecipeWrapper pressingInv = new RecipeWrapper(new ItemStackHandler(1));

    public Optional<PressingRecipe> getRecipe(ItemStack item) {
        Optional<PressingRecipe> assemblyRecipe =
                SequencedAssemblyRecipe.getRecipe(level, item, AllRecipeTypes.PRESSING.getType(), PressingRecipe.class);
        if (assemblyRecipe.isPresent())
            return assemblyRecipe;

        pressingInv.setItem(0, item);
        return AllRecipeTypes.PRESSING.find(pressingInv, level);
    }

    public void onItemPressed(ItemStack result) {}

     */

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