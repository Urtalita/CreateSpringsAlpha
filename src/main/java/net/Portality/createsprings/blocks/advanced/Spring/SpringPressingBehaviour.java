package net.Portality.createsprings.blocks.advanced.Spring;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;

public class SpringPressingBehaviour extends PressingBehaviour {
    public <T extends SpringBlockEntity & PressingBehaviourSpecifics> SpringPressingBehaviour(T be) {
        super(be);
        entityScanCooldown = ENTITY_SCAN;
    }

    private int entityScanCooldown;

    @Override
    public void tick() {
        Level level = getWorld();
        BlockPos worldPosition = getPos();

        if (!running || level == null) {
            if (level != null && !level.isClientSide) {
                if (specifics.getKineticSpeed() == 0)
                    return;
                if (entityScanCooldown > 0)
                    entityScanCooldown--;
                if (entityScanCooldown <= 0) {
                    entityScanCooldown = ENTITY_SCAN;

                    if (BlockEntityBehaviour.get(level, worldPosition.below(),
                            TransportedItemStackHandlerBehaviour.TYPE) != null)
                        return;
                    if (BasinBlock.isBasin(level, worldPosition.below()))
                        return;

                    for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class,
                            new AABB(worldPosition.below()).deflate(.125f))) {
                        if (!itemEntity.isAlive() || !itemEntity.onGround())
                            continue;
                        if (!specifics.tryProcessInWorld(itemEntity, true))
                            continue;
                        start(Mode.WORLD);
                        return;
                    }
                }

            }
            return;
        }

        if (level.isClientSide && runningTicks == -CYCLE / 2) {
            prevRunningTicks = CYCLE / 2;
            return;
        }

        if (runningTicks == CYCLE / 2 && specifics.getKineticSpeed() != 0) {
            if (inWorld())
                applyInWorld();
            if (onBasin())
                applyOnBasin();

            if (level.getBlockState(worldPosition.below())
                    .getSoundType() == SoundType.WOOL)
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION_ON_BELT.playOnServer(level, worldPosition);
            else
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition, .5f,
                        .75f + (Math.abs(specifics.getKineticSpeed()) / 1024f));

            if (!level.isClientSide)
                blockEntity.sendData();
        }

        if (!level.isClientSide && runningTicks > CYCLE) {
            finished = true;
            running = false;
            particleItems.clear();
            specifics.onPressingCompleted();
            blockEntity.sendData();
            return;
        }

        prevRunningTicks = runningTicks;
        runningTicks += getRunningTickSpeed();
        if (prevRunningTicks < CYCLE / 2 && runningTicks >= CYCLE / 2) {
            runningTicks = CYCLE / 2;
            // Pause the ticks until a packet is received
            if (level.isClientSide && !blockEntity.isVirtual())
                runningTicks = -(CYCLE / 2);
        }
    }

    protected void applyInWorld() {
        Level level = getWorld();
        BlockPos worldPosition = getPos();
        AABB bb = new AABB(worldPosition.below(1));
        boolean bulk = specifics.canProcessInBulk();

        particleItems.clear();

        if (level.isClientSide)
            return;

        for (Entity entity : level.getEntities(null, bb)) {
            if (!(entity instanceof ItemEntity itemEntity))
                continue;
            if (!entity.isAlive() || !entity.onGround())
                continue;

            entityScanCooldown = 0;
            if (specifics.tryProcessInWorld(itemEntity, false))
                blockEntity.sendData();
            if (!bulk)
                break;
        }
    }

}
