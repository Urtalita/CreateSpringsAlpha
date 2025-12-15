package net.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.sounds.CSpringsSounds;
import net.createmod.catnip.math.BlockFace;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiscFragmentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.springAnimation;
import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class SpringCatapultBlockEntity extends KineticBlockEntity implements IControlContraption, IHaveGoggleInformation {
    public float xAngle = 0;
    public float yAngle = 0;
    public float stored = 0;
    public float capacity = ModConfigs.common().SPRING_CAPACITY.get() / 8f;
    private float progress = stored / capacity;
    private int phase = 0;
    private float hardness = DEFAULT_HARDNESS;
    public ItemStack heldStack = ItemStack.EMPTY;
    protected ControlledContraptionEntity movedContraption;

    private float prevProgress = progress;
    private float prevyAngle = xAngle;
    private float prevxAngle = yAngle;

    public float targetXAngle;
    public float targetYAngle;

    private boolean targetingCatapult = false;
    private boolean targetingOriginal = true;
    public boolean shootingContraption = true;

    public static final float DEFAULT_HARDNESS = 4;
    public BlockPos targetInQueue = null;
    public BlockPos target = null;

    CatapultLauncher launcher;
    CatapultMode mode = CatapultMode.NO_TARGET;

    public FilteringBehaviour filtering;

    public SpringCatapultBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        recalculateTrajectory();
        recalculateAngles(true);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        filtering = new FilteringBehaviour(this, new SpringCatapultFilterSlot());
        behaviours.add(filtering);

    }

    public float getProgress(float pt){
        return Mth.lerp(pt, prevProgress, progress);
    }

    public float getXAngle(float partialTick){
        return Mth.lerp(partialTick, prevxAngle, xAngle);
    }

    public float getYAngle(float partialTick){
        return Mth.lerp(partialTick, prevyAngle, yAngle) + 180;
    }

    public int getPhase(){
        return phase;
    }

    public void addTarget(BlockPos target, boolean isPrimary) {

        if (target == null) return;
        if (target.equals(this.worldPosition)) return;

        if(mode == CatapultMode.NO_TARGET){
            this.target = null;
        }

        mode = CatapultMode.WAITING;

        if (isPrimary || this.target == null) {
            this.target = target;
        } else {
            // Проверяем, не пытаемся ли установить ту же цель
            if (!target.equals(this.target)) {
                targetInQueue = target;
            }
        }

        if (level.getBlockEntity(target) instanceof SpringCatapultBlockEntity springCatapultBlockEntity) {
            targetingCatapult = true;
            if (springCatapultBlockEntity.target == null || !springCatapultBlockEntity.target.equals(worldPosition)) {
                springCatapultBlockEntity.addTarget(worldPosition, false); // false - как вторичная цель
            }
        }

        recalculateAngles(true);
        recalculateTrajectory();
    }

    public void setTargetInQueue(BlockPos queueTarget) {
        if (queueTarget == null || queueTarget.equals(this.worldPosition)) return;

        if (!queueTarget.equals(this.target)) {
            this.targetInQueue = queueTarget;

            mode = CatapultMode.WAITING;

            if (level.getBlockEntity(queueTarget) instanceof SpringCatapultBlockEntity springCatapultBlockEntity) {
                if (springCatapultBlockEntity.target == null || !springCatapultBlockEntity.target.equals(worldPosition)) {
                    springCatapultBlockEntity.addTarget(worldPosition, false);
                }
            }

            sendData();
            setChanged();
        }
        recalculateAngles(true);
        recalculateTrajectory();
    }

    public void nextTarget(){
        targetingOriginal = !targetingOriginal;
        recalculateAngles(true);
    }

    public boolean isUpsideDown(){
        return getBlockState().getValue(SpringCatapultBlock.CEILING);
    }

    @Override
    public void tick() {
        super.tick();

        if(heldStack.getItem() == Items.GOLD_INGOT.asItem()){
            int a = 0;
        }

        prevyAngle = yAngle;
        prevxAngle = xAngle;

        if(mode == CatapultMode.RAVE){
            rave();
            return;
        }

        if(movedContraption != null){
            Vec3 move = worldPosition.above(2).getCenter().add(-0.5, -2 + 4/16f, 0);
            movedContraption.moveTo(move);
        }

        yAngle = handleAngling(yAngle, targetYAngle);
        xAngle = handleAngling(xAngle, targetXAngle);

        float CurSpeed = Math.abs(getSpeed());
        stored = Mth.clamp(stored + CurSpeed / DEFAULT_HARDNESS * hardness, 0, capacity);

        prevProgress = progress;
        progress = stored / capacity;

        switch (mode){
            case WAITING, CANSHOOT -> {
                waiting();
                break;
            }
            case INPUTTING -> {
                input();
                break;
            }
            case SHOOTING -> {
                shoot();
                break;
            }
            case NO_TARGET -> {
                noTarget();
                break;
            }
        }

        sendData();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public void noTarget(){
        BlockPos min = worldPosition.above(5).east(5).north(5);
        BlockPos max = worldPosition.below(5).west(5).south(5);
        AABB area = new AABB(
                min.getX(), min.getY(), min.getZ(),
                max.getX() + 1.0, max.getY() + 1.0, max.getZ() + 1.0
        );

        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            BlockPos pos = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
            if(target != pos){
                target = pos;
                recalculateAngles(false);
            }
            return;
        }
    }

    public void waiting(){
        if(normalizeAngle(xAngle) == normalizeAngle(targetXAngle) &&
                normalizeAngle(yAngle) == normalizeAngle(targetYAngle) &&
                progress == 1 && !heldStack.isEmpty() && getSelectedTarget() != null){

            if(level.getBlockEntity(getSelectedTarget()) instanceof SpringCatapultBlockEntity catapult){
                if(catapult.mode == CatapultMode.WAITING || catapult.mode == CatapultMode.CANSHOOT || catapult.mode == CatapultMode.INPUTTING){
                    mode = CatapultMode.CANSHOOT;

                    if(catapult.getSelectedTarget() != null && catapult.getSelectedTarget().equals(worldPosition)){
                        if(normalizeAngle(catapult.yAngle) - normalizeAngle(catapult.targetYAngle) <= 2){
                            if(isFree(catapult, heldStack)){
                                mode = CatapultMode.SHOOTING;
                                catapult.mode = CatapultMode.INPUTTING;
                            } else {
                                if(level.getBlockEntity(getUnselectedTarget()) instanceof SpringCatapultBlockEntity nextCatapult){
                                    if(isFree(nextCatapult, heldStack)){
                                        nextTarget();
                                    }
                                }
                            }
                        }

                    } else if(catapult.getUnselectedTarget() != null && catapult.getUnselectedTarget().equals(worldPosition)){
                        if(isFree(catapult, heldStack)){
                            if(catapult.mode == CatapultMode.WAITING){
                                catapult.mode = CatapultMode.INPUTTING;
                            }
                        }
                    }
                }
            } else {
                mode = CatapultMode.SHOOTING;
                recalculateTrajectory();
            }
        }
    }

    private void recalculateTrajectory(){
        int startOffset = 2;
        int endOffset = 0;
        BlockPos selectedTarget = getSelectedTarget();
        if(isUpsideDown()){
            startOffset = -2;
        }
        if(level != null){
            if(level.getBlockEntity(selectedTarget) instanceof SpringCatapultBlockEntity springCatapultBlockEntity){
               endOffset = 2;
                if(springCatapultBlockEntity.isUpsideDown()){
                    endOffset = -2;
                }
            }
        }
        if(selectedTarget != null){
            if(launcher == null){
                launcher = new CatapultLauncher(getSelectedTarget().above(endOffset), worldPosition.above(startOffset), ModConfigs.common().SPRING_SPLASH_DURATION.get());
            }
            launcher.recalculateTrajectory(getSelectedTarget().above(endOffset), worldPosition.above(startOffset), ModConfigs.common().SPRING_SPLASH_DURATION.get());
        }
    }

    public void input() {
        if(!heldStack.isEmpty() && heldStack.getCount() >= heldStack.getMaxStackSize()) {
            mode = CatapultMode.WAITING;
        }

        if(level.getBlockEntity(getUnselectedTarget()) instanceof SpringCatapultBlockEntity catapult) {
            if(catapult.mode == CatapultMode.CANSHOOT && this.mode == CatapultMode.INPUTTING) {
                nextTarget(); // -
            }
            mode = CatapultMode.WAITING;
        }
    }

    public void shoot(){
        if(phase == 0){
            CSpringsSounds.playBweum(level, worldPosition);
        }

        progress = springAnimation(phase) * (stored / capacity);
        phase++;

        if(phase == ModConfigs.common().SPRING_SPLASH_DURATION.get()){
            phase = 0;
            stored = 0;
            mode = CatapultMode.WAITING;
            BlockPos selectedTarget = getSelectedTarget();

            if(selectedTarget != null){
                if(level.getBlockEntity(selectedTarget) instanceof SpringCatapultBlockEntity springCatapultBlockEntity){
                    LazyOptional<IItemHandler> lazyHandler = springCatapultBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
                    lazyHandler.ifPresent(handler -> {
                        heldStack = handler.insertItem(0, heldStack, false);
                        springCatapultBlockEntity.nextTarget(); // -
                        springCatapultBlockEntity.mode = CatapultMode.WAITING;
                    });
                    if(!targetingOriginal){
                        nextTarget(); // -
                    }
                } else {
                    heldStack = ItemHandlerHelper.insertItem(getHandler(), heldStack, false);
                    heldStack = insertToJukebox(heldStack, false);
                    if(heldStack != ItemStack.EMPTY){
                        dropContent(selectedTarget);
                    }

                    if(!targetingOriginal){
                        nextTarget();
                    }
                }
            }
            notifyUpdate();
            return;
        }

        BlockPos pos;
        if(isUpsideDown()){
            pos = launcher.parseTrajectory(level, phase, yAngle, worldPosition.above(-2));
        } else {
            pos = launcher.parseTrajectory(level, phase, yAngle, worldPosition.above(2));
        }

        if(pos != null){
            dropContent(pos);
            mode = CatapultMode.WAITING;
        }
    }

    public ItemStack insertToJukebox(ItemStack stack, boolean simulate) {
        if (!(stack.getItem() instanceof RecordItem))
            return stack;
        if (level.getBlockState(getSelectedTarget()).getOptionalValue(JukeboxBlock.HAS_RECORD).orElse(true))
            return stack;
        if (!(level.getBlockEntity(getSelectedTarget()) instanceof JukeboxBlockEntity jukeboxBE))
            return stack;
        if (!jukeboxBE.getFirstItem().isEmpty())
            return stack;
        ItemStack remainder = stack.copy();
        ItemStack toInsert = remainder.split(1);
        if (!simulate){
            jukeboxBE.setItem(0, toInsert);
            mode = CatapultMode.RAVE;
        }
        return remainder;
    }

    @Nullable
    protected IItemHandler getHandler() {
        LazyOptional<IItemHandler> cachedHandler = LazyOptional.empty();
        if (!cachedHandler.isPresent()) {
            BlockEntity be = level.getBlockEntity(getSelectedTarget());
            if (be == null)
                return null;
            cachedHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
        }
        return cachedHandler.orElse(null);
    }

    private void rave(){
        yAngle += 15;
        xAngle += 10;
    }

    public void recalculateAngles(boolean doLauncher){
        targetXAngle = 0;
        targetYAngle = 0;
        BlockPos selectedTarget = getSelectedTarget();

        if(selectedTarget != null){
            targetYAngle = getYawAngle(selectedTarget, worldPosition);


            if(targetingCatapult){
                targetXAngle = getPitchAngle(selectedTarget.above(2), worldPosition.above(2));
            } else {
                targetXAngle = getPitchAngle(selectedTarget, worldPosition.above(2));
            }

            if(doLauncher){
                recalculateTrajectory();
                targetXAngle = targetXAngle - launcher.getShootingAngle();
            }
        }
    }

    private static boolean isFree(SpringCatapultBlockEntity catapult, ItemStack heldStack){
        AtomicBoolean ret = new AtomicBoolean(false);
        LazyOptional<IItemHandler> lazyHandler = catapult.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
        lazyHandler.ifPresent(handler -> {
            ItemStack stack = handler.insertItem(0, heldStack, true);
            if(stack.getItem() != heldStack.getItem()){
                ret.set(true);
            }
        });
        return ret.get();
    }

    public float handleAngling(float currentAngle, float targetAngle) {
        float difference = targetAngle - currentAngle;
        final float EPSILON = 0.1f; // Допустимая погрешность

        // Нормализуем разницу в диапазон [-180, 180]
        while (difference > 180) difference -= 360;
        while (difference < -180) difference += 360;

        // Если разница уже в пределах погрешности - считаем цель достигнутой
        if (Math.abs(difference) <= EPSILON) {
            return targetAngle; // Возвращаем точное целевое значение
        }

        float step = Mth.sqrt(Mth.abs(getSpeed()));

        // Плавный поворот с постоянной скоростью
        return currentAngle + Math.signum(difference) * Math.min(Math.abs(difference), step);
    }

    private static float normalizeAngle(float angle) {
        angle %= 360;
        return Math.abs(angle + (angle < 0 ? 360 : 0));
    }

    public static float getYawAngle(BlockPos targetPos, BlockPos sourcePos) {
        double dX = targetPos.getX() - sourcePos.getX();
        double dZ = targetPos.getZ() - sourcePos.getZ();

        double distance = Math.sqrt(dX * dX + dZ * dZ);

        if (distance == 0) {
            return 0.0f;
        }

        double sin = dX / distance;
        double asin = Math.asin(sin);

        if(dZ < 0){
            return (float) (-Math.toDegrees(asin) + 180);
        }
        return (float) Math.toDegrees(asin);
    }

    public float getPitchAngle(BlockPos targetPos, BlockPos sourcePos) {
        double dX = targetPos.getX() - sourcePos.getX();
        double dY = targetPos.getY() - sourcePos.getY();
        double dZ = targetPos.getZ() - sourcePos.getZ();

        double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);

        if (horizontalDistance == 0) {
            return dY > 0 ? -90.0f : 90.0f;
        }

        double verticalDistance = Math.sqrt(dY * dY + horizontalDistance * horizontalDistance);

        double sin = dY / verticalDistance;
        double asin = Math.asin(sin);

        double angleDeg = -Math.toDegrees(asin);

        if(dY > 0){
            return (float) -angleDeg;
        }
        return (float) angleDeg;
    }

    public BlockPos getSelectedTarget(){
        if(targetInQueue == null) return target;
        if(target == null) return targetInQueue;
        return targetingOriginal ? target : targetInQueue;
    }

    public BlockPos getUnselectedTarget(){
        if(targetInQueue == null) return target;
        if(target == null) return targetInQueue;
        return targetingOriginal ? targetInQueue : target;
    }

    public void onRemove(){
        if(target != null){
            if(level.getBlockEntity(target) instanceof SpringCatapultBlockEntity springCatapultBlockEntity){
                if(springCatapultBlockEntity.target == worldPosition){springCatapultBlockEntity.target = null;}
                if(springCatapultBlockEntity.targetInQueue == worldPosition){springCatapultBlockEntity.targetInQueue = null;}
                springCatapultBlockEntity.nextTarget();
            }
        }
        if(targetInQueue != null){
            if(level.getBlockEntity(targetInQueue) instanceof SpringCatapultBlockEntity springCatapultBlockEntity){
                if(springCatapultBlockEntity.target == worldPosition){springCatapultBlockEntity.target = null;}
                if(springCatapultBlockEntity.targetInQueue == worldPosition){springCatapultBlockEntity.targetInQueue = null;}
                springCatapultBlockEntity.nextTarget();
            }
        }
        dropContent(worldPosition);
    }

    public void dropContent(BlockPos pos){
        if (heldStack.isEmpty()) return;

        ItemEntity entityIn = new ItemEntity(level, pos.getX(), pos.above(2).getY(), pos.getZ(), heldStack);
        heldStack = ItemStack.EMPTY;
        level.addFreshEntity(entityIn);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        xAngle = compound.getFloat("xAngle");
        yAngle = compound.getFloat("yAngle");

        targetYAngle = compound.getFloat("targetY");
        targetXAngle = compound.getFloat("targetX");

        stored = compound.getFloat("stored");
        capacity = compound.getFloat("capacity");
        phase = compound.getInt("phase");
        progress = compound.getFloat("progress");
        targetingCatapult = compound.getBoolean("targetingCatapult");
        heldStack = ItemStack.of(compound.getCompound("stack"));
        targetingOriginal = compound.getBoolean("targetingOriginal");
        shootingContraption = compound.getBoolean("shootingContraption");

        mode = CatapultMode.deSerialize(compound, "mode");

        CompoundTag targetTag = compound.getCompound("target");
        target = targetTag.isEmpty() ? null : NbtUtils.readBlockPos(targetTag);

        CompoundTag targetInQueueTag = compound.getCompound("target2");
        targetInQueue = targetInQueueTag.isEmpty() ? null : NbtUtils.readBlockPos(targetInQueueTag);

        recalculateAngles(true);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("xAngle", xAngle);
        compound.putFloat("yAngle", yAngle);

        compound.putFloat("targetY", targetYAngle);
        compound.putFloat("targetX", targetXAngle);

        compound.putFloat("stored", stored);
        compound.putFloat("capacity", capacity);
        compound.putInt("phase", phase);
        compound.putFloat("progress", progress);
        compound.putBoolean("targetingCatapult", targetingCatapult);
        compound.put("stack", heldStack.serializeNBT());
        compound.putBoolean("targetingOriginal", targetingOriginal);
        compound.putBoolean("shootingContraption", shootingContraption);

        if(mode != null){
            compound.putString("mode", mode.name);
        }

        if(target != null){compound.put("target", NbtUtils.writeBlockPos(target));}
        if(targetInQueue != null){compound.put("target2", NbtUtils.writeBlockPos(targetInQueue));}
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> new IItemHandler() {
                private final int OUTPUT_SLOT = 0;
                private final int SLOT_COUNT = 1;

                @Override
                public int getSlots() {
                    return SLOT_COUNT;
                }

                @Override
                public @NotNull ItemStack getStackInSlot(int slot) {
                    if (slot == OUTPUT_SLOT) {
                        return heldStack;
                    }
                    return ItemStack.EMPTY;
                }

                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                    if (slot != OUTPUT_SLOT) return stack;
                    if (!isItemValid(slot, stack)) return stack;

                    if(stack.getItem() != filtering.getFilter().getItem() && !filtering.getFilter().isEmpty()){return stack;}

                    if (heldStack.isEmpty()) {
                        if (!simulate) {
                            heldStack = stack.copy(); // Use copy for a new stack
                            setChanged(); // Mark data as dirty
                        }
                        return ItemStack.EMPTY;
                    } else if (ItemStack.isSameItemSameTags(heldStack, stack)) { // Check if items can be merged
                        int maxStackSize = Math.min(getSlotLimit(slot), heldStack.getMaxStackSize());
                        int spaceAvailable = maxStackSize - heldStack.getCount();

                        if (spaceAvailable <= 0) return stack;

                        int toAdd = Math.min(spaceAvailable, stack.getCount());
                        if (!simulate) {
                            heldStack.grow(toAdd); // Add to the existing stack
                            setChanged(); // Mark data as dirty
                        }

                        return stack.copyWithCount(stack.getCount() - toAdd);
                    }
                    return stack; // Cannot merge, return the original stack
                }

                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                    if (slot != OUTPUT_SLOT || amount <= 0 || heldStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    int extractedAmount = Math.min(amount, heldStack.getCount());
                    ItemStack extracted = heldStack.copyWithCount(extractedAmount);

                    if (!simulate) {
                        heldStack.shrink(extractedAmount);
                        if (heldStack.isEmpty()) heldStack = ItemStack.EMPTY;
                        setChanged(); // Сохраняем изменения в NBT
                    }

                    return extracted;
                }

                @Override
                public int getSlotLimit(int slot) {
                    return 64;
                }

                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    return slot == OUTPUT_SLOT;
                }
            }).cast();
        }
        return super.getCapability(cap, side);
    }

    public static class SpringCatapultFilterSlot extends ValueBoxTransform.Sided {
        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis()
                    .isHorizontal();
        }
    }

    //IControlContraption

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return movedContraption == contraption;
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
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