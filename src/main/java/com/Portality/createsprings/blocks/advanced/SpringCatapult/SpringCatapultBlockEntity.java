package com.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.Portality.createsprings.blocks.advanced.AndesiteMold.MoldBlockEntity;
import com.Portality.createsprings.client.sounds.CSpringsSounds;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.schematics.cannon.SchematicannonInventory;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity.springAnimation;

public class SpringCatapultBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    public float xAngle = 0;
    public float yAngle = 0;
    public float stored = 0;
    public float capacity = ModConfigs.common().SPRING_CAPACITY.get() / 16f;
    float progress = stored / capacity;
    private int phase = 0;
    private float hardness = DEFAULT_HARDNESS;
    public ItemStack heldStack = ItemStack.EMPTY;
    //protected ControlledContraptionEntity movedContraption;

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

    public SpringCatapultBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        recalculateTrajectory();
        recalculateAngles(true);
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

        prevyAngle = yAngle;
        prevxAngle = xAngle;

        if(mode == CatapultMode.RAVE){
            rave();
            return;
        }

        //if(movedContraption != null){
            //Vec3 move = worldPosition.above(2).getCenter().add(-0.5, -2 + 4/16f, 0);
            //movedContraption.moveTo(move);
        //}

        yAngle = handleAngling(yAngle, targetYAngle);
        xAngle = handleAngling(xAngle, targetXAngle);

        float CurSpeed = Math.abs(getSpeed());
        stored = Mth.clamp(stored + CurSpeed / DEFAULT_HARDNESS * hardness, 0, capacity);

        prevProgress = progress;

        if(Mth.floor(progress * 15f) != Mth.floor(prevProgress * 15f)){
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }

        progress = stored / capacity;

        if(mode == null){return;}

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

    public void processEntitiesAbove(){
        if(level.getGameTime() % 2 == 1){return;}
        if(!heldStack.isEmpty() && heldStack.getCount() >= 64){return;}

        BlockPos offset = (!isUpsideDown()) ?
                worldPosition.offset(0, 1, 0) :
                worldPosition.offset(0, -1, 0);

        List<Entity> entities = this.level.getEntitiesOfClass(Entity.class,
                (new AABB(offset)).inflate((double)-0.0625F, (double)0.0F, (double)-0.0625F));

        for(Entity entity : entities) {
            if(entity instanceof ItemEntity item){
                ItemStack stack = item.getItem();
                ItemStack left = insertItem(stack);

                if(left.isEmpty()){
                    item.remove(Entity.RemovalReason.DISCARDED);
                    continue;
                }

                item.setItem(left);
            }
        }
    }

    public void shootEntitiesAbove(){
        BlockPos offset = (!isUpsideDown()) ?
                worldPosition.offset(0, 1, 0) :
                worldPosition.offset(0, -1, 0);

        List<Entity> entities = this.level.getEntitiesOfClass(Entity.class,
                (new AABB(offset)).inflate((double)-0.0625F, (double)0.0F, (double)-0.0625F));

        Vec3 speed = launcher.getSpeedForEntity(normalizeAngle(yAngle));
        for(Entity entity : entities) {
            if(entity instanceof LivingEntity livingEntity){
                livingEntity.setDeltaMovement(speed);
                mode = CatapultMode.SHOOTING;
            }
        }
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
        processEntitiesAbove();

        if(isEqualAngles(xAngle, targetXAngle) &&
                isEqualAngles(yAngle, targetYAngle) &&
                progress == 1 && !heldStack.isEmpty() && getSelectedTarget() != null){
            shootEntitiesAbove();

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
                if(checkForFreeInventory(getSelectedTarget())){
                    mode = CatapultMode.SHOOTING;
                    recalculateTrajectory();
                    return;
                }

                if(checkForFreeInventory(getUnselectedTarget())){
                    nextTarget();
                }
            }
            return;
        }

        if(isEqualAngles(xAngle, targetXAngle) &&
                isEqualAngles(yAngle, targetYAngle) &&
                progress == 1 && getSelectedTarget() != null){
            shootEntitiesAbove();
            return;
        }
    }

    public boolean checkForFreeInventory(BlockPos target){
        DirectBeltInputBehaviour targetOpenInv = BlockEntityBehaviour.get(level, target, DirectBeltInputBehaviour.TYPE);
        if (targetOpenInv != null && heldStack != null
                && targetOpenInv.handleInsertion(heldStack, Direction.UP, true)
                .getCount() == heldStack.getCount())
            return false;
        return true;
    }

    public DirectBeltInputBehaviour getTargetOpenInv() {
        BlockPos targetPos = getSelectedTarget();
        return BlockEntityBehaviour.get(level, targetPos, DirectBeltInputBehaviour.TYPE);
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
        } else {
            launcher = new CatapultLauncher(worldPosition.above(startOffset), worldPosition.above(startOffset), ModConfigs.common().SPRING_SPLASH_DURATION.get());
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
                    IItemHandler handler = springCatapultBlockEntity.itemHandler;

                    heldStack = handler.insertItem(0, heldStack, false);
                    springCatapultBlockEntity.nextTarget(); // -
                    springCatapultBlockEntity.mode = CatapultMode.WAITING;
                    if(!targetingOriginal){
                        nextTarget(); // -
                    }
                } else {
                    int oldCount = heldStack.getCount();
                    heldStack = ItemHandlerHelper.insertItem(getHandler(), heldStack, false);
                    heldStack = insertToJukebox(heldStack, false);
                    if(heldStack != ItemStack.EMPTY && oldCount == heldStack.getCount()){
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

        if(launcher == null){
            recalculateTrajectory();
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
        BlockPos pos = getSelectedTarget();
        if (stack.get(DataComponents.JUKEBOX_PLAYABLE) == null)
            return stack;
        if (level.getBlockState(pos).getOptionalValue(JukeboxBlock.HAS_RECORD).orElse(true))
            return stack;
        if (!(level.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBE))
            return stack;
        if (!jukeboxBE.getTheItem().isEmpty())
            return stack;
        ItemStack remainder = stack.copy();
        ItemStack toInsert = remainder.split(1);
        if (!simulate)
            jukeboxBE.setTheItem(toInsert);
        return remainder;
    }

    @Nullable
    protected IItemHandler getHandler() {
        BlockPos targetPos = getSelectedTarget();
        if (targetPos == null || level == null)
            return null;

        return level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, Direction.UP);
    }

    public IItemHandler returnHandler(){
        return itemHandler;
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
                targetXAngle = -launcher.getShootingAngle();
            }
        }
    }

    private static boolean isFree(SpringCatapultBlockEntity catapult, ItemStack heldStack) {
        ItemStack remainder = catapult.itemHandler.insertItem(0, heldStack, true);

        return remainder.getCount() < heldStack.getCount();

    }

    public float handleAngling(float currentAngle, float targetAngle) {
        float difference = targetAngle - currentAngle;
        final float EPSILON = 0.1f; // Допустимая погрешность

        // Нормализуем разницу в диапазон [-180, 180]
        if (difference > 180) difference -= 360;
        if (difference < -180) difference += 360;

        // Если разница уже в пределах погрешности - считаем цель достигнутой
        if (Math.abs(difference) <= EPSILON) {
            if (difference > 180) return targetAngle -= 360;
            if (difference < -180) return targetAngle += 360;
        }

        float step = Mth.sqrt(Mth.abs(getSpeed()));

        // Плавный поворот с постоянной скоростью
        return currentAngle + Math.signum(difference) * Math.min(Math.abs(difference), step);
    }

    private static float normalizeAngle(float angle) {
        angle %= 360;
        return Math.abs(angle + (angle < 0 ? 360 : 0));
    }

    private static boolean isEqualAngles(float angle1, float angle2){
        float diff = normalizeAngle(angle1) - normalizeAngle(angle2);
        return Mth.abs(diff) < 1;
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

        double verticalAngleRad = Math.atan2(dY, horizontalDistance);
        double verticalAngleDeg = Math.toDegrees(verticalAngleRad);

        return (float) -verticalAngleDeg;
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
        ItemEntity entity = new ItemEntity(level, pos.above().getX(), pos.above().getY(), pos.above().getZ(), heldStack.copyAndClear());
        level.addFreshEntity(entity);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        xAngle = compound.getFloat("xAngle");
        yAngle = compound.getFloat("yAngle");

        targetYAngle = compound.getFloat("targetY");
        targetXAngle = compound.getFloat("targetX");

        stored = compound.getFloat("stored");
        capacity = compound.getFloat("capacity");
        phase = compound.getInt("phase");
        progress = compound.getFloat("progress");
        targetingCatapult = compound.getBoolean("targetingCatapult");
        if (compound.contains("stack", Tag.TAG_COMPOUND)) {
            heldStack = ItemStack.parseOptional(registries, compound.getCompound("stack"));
        } else {
            heldStack = ItemStack.EMPTY;
        }
        targetingOriginal = compound.getBoolean("targetingOriginal");
        shootingContraption = compound.getBoolean("shootingContraption");

        mode = CatapultMode.deSerialize(compound, "mode");

        target = decodeTarget("target1_", compound);
        targetInQueue = decodeTarget("target2_", compound);

        recalculateAngles(true);
    }

    public static void encodeTarget(String prefix, CompoundTag compoundTag, BlockPos pos){
        if(pos == null){return;}

        compoundTag.putInt(prefix + "x", pos.getX());
        compoundTag.putInt(prefix + "y", pos.getY());
        compoundTag.putInt(prefix + "z", pos.getZ());
    }

    private BlockPos decodeTarget(String prefix, CompoundTag compoundTag){
        if (!compoundTag.contains(prefix + "x")) {
            return null;
        }

        int x = compoundTag.getInt(prefix + "x");
        int y = compoundTag.getInt(prefix + "y");
        int z = compoundTag.getInt(prefix + "z");
        return new BlockPos(x, y, z);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("xAngle", xAngle);
        compound.putFloat("yAngle", yAngle);

        compound.putFloat("targetY", targetYAngle);
        compound.putFloat("targetX", targetXAngle);

        compound.putFloat("stored", stored);
        compound.putFloat("capacity", capacity);
        compound.putInt("phase", phase);
        compound.putFloat("progress", progress);
        compound.putBoolean("targetingCatapult", targetingCatapult);

        if(!heldStack.isEmpty()) compound.put("stack", heldStack.save(registries));

        compound.putBoolean("targetingOriginal", targetingOriginal);
        compound.putBoolean("shootingContraption", shootingContraption);

        if(mode != null){
            compound.putString("mode", mode.name);
        }

        encodeTarget("target1_", compound, target);
        encodeTarget("target2_", compound, targetInQueue);
    }

    private final IItemHandler itemHandler = new IItemHandler() {
        @Override
        public int getSlots() { return 1; }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot == 0 ? heldStack : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot != 0 || stack.isEmpty()) return stack;

            // В 1.21.1 для сравнения предметов и компонентов:
            if (heldStack.isEmpty()) {
                if (!simulate) {
                    heldStack = stack.copy();
                    setChanged();
                }
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(heldStack, stack)) {
                int maxStackSize = Math.min(getSlotLimit(0), heldStack.getMaxStackSize());
                int spaceAvailable = maxStackSize - heldStack.getCount();
                if (spaceAvailable <= 0) return stack;

                int toAdd = Math.min(spaceAvailable, stack.getCount());
                if (!simulate) {
                    heldStack.grow(toAdd);
                    setChanged();
                }
                return stack.copyWithCount(stack.getCount() - toAdd);
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || amount <= 0 || heldStack.isEmpty()) return ItemStack.EMPTY;

            int toExtract = Math.min(amount, heldStack.getCount());
            ItemStack extracted = heldStack.copyWithCount(toExtract);

            if (!simulate) {
                heldStack.shrink(toExtract);
                setChanged();
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) { return 64; }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) { return slot == 0; }
    };

    // Метод-хелпер, который вы использовали, можно упростить
    public @NotNull ItemStack insertItem(@NotNull ItemStack stack) {
        return itemHandler.insertItem(0, stack, false);
    }

    //IControlContraption

    /*
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

     */
}