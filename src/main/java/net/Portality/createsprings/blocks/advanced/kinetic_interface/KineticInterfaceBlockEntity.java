package net.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KineticInterfaceBlockEntity extends GeneratingKineticBlockEntity {
    public static final int ANIMATION = 4;
    protected int transferTimer;
    protected float distance;
    protected LerpedFloat connectionAnimation;
    protected boolean powered;
    protected Entity connectedEntity;
    public List<ConnectedToPSKIInfo> connectedSprings;
    public boolean isGenerating;
    float stressImpact = 0;

    public int keepAlive = 0;

    public KineticInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        transferTimer = 0;
        connectionAnimation = LerpedFloat.linear()
                .startWithValue(0);
        powered = false;
    }

    public boolean isPowered() {
        return powered;
    }

    float getConnectionDistance() {
        return distance;
    }

    boolean isConnected() {
        int timeUnit = getTransferTimeout();
        return transferTimer >= ANIMATION && transferTimer <= timeUnit + ANIMATION;
    }

    public void startTransferringTo(Contraption contraption, float distance) {
        if (connectedEntity == contraption.entity)
            return;
        this.distance = Math.min(2, distance);
        connectedEntity = contraption.entity;

        getContraptionSprings(contraption);
        startConnecting();
        notifyUpdate();
        updateGeneratedRotation();
        sendData();
    }

    public void getContraptionSprings(Contraption contraption) {
        connectedSprings = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo blockInfo : contraption.getBlocks().values()) {
            if (blockInfo.state().getBlock() == ModBlocks.LARGE_SPRING.get()) {
                // 1. Создаём BE через наш зарегистрированный тип
                LargeSpringBlockEntity be = ModBlockEntities.LARGE_SPRING.get().create(
                        BlockPos.ZERO,
                        blockInfo.state()
                );

                if (be != null && blockInfo.nbt() != null) {
                    // 2. Загружаем данные напрямую в нашу BE
                    be.load(blockInfo.nbt());

                    // 3. Инициализируем необходимые зависимости
                    be.setLevel(level); // Передаём контекст уровня!

                    connectedSprings.add(new ConnectedToPSKIInfo(
                            blockInfo.pos(),
                            be,
                            (IConnectableToPSKI) be
                    ));
                }
            }
        }
    }

    public void updateContraptionBlockEntity(Contraption contraption, BlockPos localPos, BlockEntity updatedEntity) {
        Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks = contraption.getBlocks();
        StructureTemplate.StructureBlockInfo info = blocks.get(localPos);

        if (info != null) {

            CompoundTag newNbt = updatedEntity.saveWithoutMetadata();
            blocks.put(localPos, new StructureTemplate.StructureBlockInfo(
                    info.pos(),
                    info.state(),
                    newNbt
            ));
        }
    }

    public boolean canTransfer() {
        if (connectedEntity != null && !connectedEntity.isAlive())
            stopTransferring();
        return connectedEntity != null && isConnected();
    }

    @Override
    public void initialize() {
        super.initialize();
        powered = level.hasNeighborSignal(worldPosition);
        if (!powered)
            notifyContraptions();
    }

    public void neighbourChanged() {
        boolean isBlockPowered = level.hasNeighborSignal(worldPosition);
        if (isBlockPowered == powered) return;

        powered = isBlockPowered;
        isGenerating = powered; // Синхронизируем состояния!
        notifyContraptions();
        updateGeneratedRotation(); // Важно для пересчета скорости
        sendData();
    }

    @Override
    public float getGeneratedSpeed() {
        if(isGenerating && connectedSprings != null && !connectedSprings.isEmpty()) {
            for (ConnectedToPSKIInfo info : connectedSprings) {
                if (info.connectedEntity.getStored() > 0) {
                    return 256; // Только если есть что отдавать
                }
            }
        }
        return 0;
    }

    protected Integer getTransferTimeout() {
        return AllConfigs.server().logistics.psiTimeout.get();
    }

    @Override
    public float calculateStressApplied() {
        this.lastStressApplied = stressImpact;
        return stressImpact;
    }

    private void notifyContraptions() {
        level.getEntitiesOfClass(AbstractContraptionEntity.class, new AABB(worldPosition).inflate(3))
                .forEach(AbstractContraptionEntity::refreshPSIs);
    }

    protected void stopTransferring() {
        connectedEntity = null;
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());

        updateGeneratedRotation();
        sendData();
    }

    @Override
    public void tick() {
        super.tick();
        boolean wasConnected = isConnected();
        int timeUnit = getTransferTimeout();
        int animation = ANIMATION;

        if (keepAlive > 0) {
            keepAlive--;
            if (keepAlive == 0 && !level.isClientSide) {
                stopTransferring();
                transferTimer = ANIMATION - 1;
                sendData();
                return;
            }
        }

        transferTimer = Math.min(transferTimer, ANIMATION * 2 + timeUnit);

        boolean timerCanDecrement = transferTimer > ANIMATION || transferTimer > 0 && keepAlive == 0
                && (isVirtual() || !level.isClientSide || transferTimer != ANIMATION);

        if (timerCanDecrement && (!isVirtual() || transferTimer != ANIMATION)) {
            transferTimer--;
            if (transferTimer == ANIMATION - 1)
                sendData();
            if (transferTimer <= 0 || powered)
                stopTransferring();
        }

        boolean isConnected = isConnected();
        if (wasConnected != isConnected && !level.isClientSide)
            setChanged();

        float progress = 0;
        if (isConnected)
            progress = 1;
        else if (transferTimer >= timeUnit + animation)
            progress = Mth.lerp((transferTimer - timeUnit - animation) / (float) animation, 1, 0);
        else if (transferTimer < animation)
            progress = Mth.lerp(transferTimer / (float) animation, 0, 1);
        connectionAnimation.setValue(progress);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        transferTimer = compound.getInt("Timer");
        distance = compound.getFloat("Distance");
        boolean poweredPreviously = powered;
        powered = compound.getBoolean("Powered");
        isGenerating = compound.getBoolean("isGenerating");
        if (clientPacket && powered != poweredPreviously && !powered)
            notifyContraptions();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Timer", transferTimer);
        compound.putFloat("Distance", distance);
        compound.putBoolean("Powered", powered);
        compound.putBoolean("isGenerating", isGenerating);
    }

    public void startConnecting() {
        transferTimer = 6 + ANIMATION * 2;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2);
    }

    public boolean isTransferring() {
        return transferTimer > ANIMATION;
    }

    float getExtensionDistance(float partialTicks) {
        return (float) (Math.pow(connectionAnimation.getValue(partialTicks), 2) * distance / 2);
    }

    public void onContentTransferred() {
        int timeUnit = getTransferTimeout();
        transferTimer = timeUnit + ANIMATION;
        award(AllAdvancements.PSI);
        sendData();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.PSI);
    }

    public void setGenerating(boolean hasSignal) {
        isGenerating = hasSignal;
        updateGeneratedRotation();
        sendData();
    }
}
