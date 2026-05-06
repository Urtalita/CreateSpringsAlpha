package com.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KineticInterfaceMovement extends PortableStorageInterfaceMovement implements MovementBehaviour {
    static final String _workingPos_ = "WorkingPos";
    static final String _clientPrevPos_ = "ClientPrevPos";
    private PSKActorVisual visual;

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf(context.state.getValue(KineticInterfaceBlock.FACING)
                        .getNormal())
                .scale(1.85f);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }


    @Nullable
    @Override
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld,
                                    MovementContext movementContext) {
        visual = new PSKActorVisual(visualizationContext, simulationWorld, movementContext);
        return visual;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!VisualizationManager.supportsVisualization(context.world))
            KineticInterfaceRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        boolean onCarriage = context.contraption instanceof CarriageContraption;
        if (onCarriage && context.motion.length() > 1 / 4f)
            return;
        if (!findInterface(context, pos))
            context.data.remove(_workingPos_);
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide)
            getAnimation(context).tickChaser();

        boolean onCarriage = context.contraption instanceof CarriageContraption;
        if (onCarriage && context.motion.length() > 1 / 4f)
            return;

        if (context.world.isClientSide) {
            BlockPos pos = BlockPos.containing(context.position);
            if (!findInterface(context, pos))
                reset(context);
            return;
        }

        if (!context.data.contains(_workingPos_)) {
            if (context.stall)
                cancelStall(context);
            return;
        }

        Optional<BlockPos> pos = NbtUtils.readBlockPos(context.data, _workingPos_);
        Vec3 target = VecHelper.getCenterOf(pos.orElse(BlockPos.ZERO));

        if (!context.stall && !onCarriage
                && context.position.closerThan(target, target.distanceTo(context.position.add(context.motion))))
            context.stall = true;

        Optional<Direction> currentFacingIfValid = getCurrentFacingIfValid(context);
        if (!currentFacingIfValid.isPresent())
            return;

        KineticInterfaceBlockEntity stationaryInterface =
                getStationaryInterfaceAt(context.world, pos.orElse(null), context.state, currentFacingIfValid.get());
        if (stationaryInterface == null) {
            reset(context);
            return;
        }

        if (stationaryInterface.connectedEntity == null) {
            stationaryInterface.startTransferringTo(context.contraption, stationaryInterface.distance);
        }


        boolean timerBelow = stationaryInterface.transferTimer <= KineticInterfaceBlockEntity.ANIMATION;
        stationaryInterface.keepAlive = 2;
        if (context.stall && timerBelow) {
            context.stall = false;
        }

        if(visual != null){visual.updateSpeed(stationaryInterface.getSpeed());}

        float collectedImpact = 0;

        if (!stationaryInterface.isGenerating) {
            if (stationaryInterface.isOverStressed()) {
                return;
            }

            float speed = Math.abs(stationaryInterface.getSpeed());
            if (speed < 0.1f) {
                return;
            }
        }

        float collectedCapacity = 0;
        stationaryInterface.storedSum = 0;

        for(int i = 0; i < stationaryInterface.connectedSprings.size(); i++) {
            ConnectedToPSKIInfo info = stationaryInterface.connectedSprings.get(i);

            IConnectableToPSKI connectedEntity = info.connectedEntity;

            collectedCapacity += connectedEntity.getCapacity();

            float stress;
            stress = connectedEntity.getHardness() * connectedEntity.getImpactCof();

            if(stationaryInterface.isGenerating){
                float speed = Math.abs(stationaryInterface.getGeneratedSpeed());
                if (info.connectedEntity.getStored() < stress) {
                    connectedEntity.setStored(0);
                    stationaryInterface.updateContraptionBlockEntity(context.contraption, info.pos, info.entity);
                    continue;
                }

                collectedImpact += stress * -1;
                connectedEntity.setStored(Math.max(0, connectedEntity.getStored() - stress * speed / SpringBlockEntity.DEFAULT_HARDNESS / 2));
            } else {
                float speed = Math.abs(stationaryInterface.getSpeed());

                if (stress + connectedEntity.getStored() > connectedEntity.getCapacity()) {
                    connectedEntity.setStored(connectedEntity.getCapacity());
                    stationaryInterface.updateContraptionBlockEntity(context.contraption, info.pos, info.entity);
                    continue;
                }

                connectedEntity.setStored(Math.min(connectedEntity.getCapacity(), connectedEntity.getStored() + stress * speed / SpringBlockEntity.DEFAULT_HARDNESS / 2));
                collectedImpact += stress;
            }

            stationaryInterface.updateContraptionBlockEntity(context.contraption, info.pos, info.entity);
        }

        stationaryInterface.capacitySum = collectedCapacity;
        stationaryInterface.sendData();

        if(collectedImpact != 0){
            stationaryInterface.keepAlive += 1;
            stationaryInterface.transferTimer += 1;
        }

        if (stationaryInterface.stressImpact != collectedImpact) {
            stationaryInterface.stressImpact = collectedImpact;

            // Важно: пересчитать нагрузку сети
            if (stationaryInterface.getOrCreateNetwork() != null){
                stationaryInterface.getOrCreateNetwork().updateStress();
            }
            stationaryInterface.updateGeneratedRotation();
            // Обновляем клиент
            stationaryInterface.sendData();
        }
    }

    protected boolean findInterface(MovementContext context, BlockPos pos) {
        if (context.contraption instanceof CarriageContraption cc && !cc.notInPortal())
            return false;
        Optional<Direction> currentFacingIfValid = getCurrentFacingIfValid(context);
        if (!currentFacingIfValid.isPresent())
            return false;

        Direction currentFacing = currentFacingIfValid.get();
        KineticInterfaceBlockEntity psi =
                findStationaryInterface(context.world, pos, context.state, currentFacing);

        if (psi == null)
            return false;

        context.data.put(_workingPos_, NbtUtils.writeBlockPos(psi.getBlockPos()));
        if (!context.world.isClientSide) {
            Vec3 diff = VecHelper.getCenterOf(psi.getBlockPos())
                    .subtract(context.position);
            diff = VecHelper.project(diff, Vec3.atLowerCornerOf(currentFacing.getNormal()));
            float distance = (float) (diff.length() + 1.85f - 1);
            psi.startTransferringTo(context.contraption, distance);
        } else {
            context.data.put(_clientPrevPos_, NbtUtils.writeBlockPos(pos));
            if (context.contraption instanceof CarriageContraption || context.contraption.entity.isStalled()
                    || context.motion.lengthSqr() == 0)
                getAnimation(context).chase(psi.getConnectionDistance() / 2, 0.25f, LerpedFloat.Chaser.LINEAR);
        }

        return true;
    }

    @Override
    public void stopMoving(MovementContext context) {
//		reset(context);
    }

    @Override
    public void cancelStall(MovementContext context) {
        reset(context);
    }

    public void reset(MovementContext context) {
        context.data.remove(_clientPrevPos_);
        context.data.remove(_workingPos_);
        context.stall = false;
        getAnimation(context).chase(0, 0.25f, LerpedFloat.Chaser.LINEAR);
    }

    public static KineticInterfaceBlockEntity findStationaryInterface(Level world, BlockPos pos, BlockState state,
                                                                        Direction facing) {
        for (int i = 0; i < 2; i++) {
            KineticInterfaceBlockEntity interfaceAt =
                    getStationaryInterfaceAt(world, pos.relative(facing, i), state, facing);
            if (interfaceAt == null)
                continue;
            return interfaceAt;
        }
        return null;
    }

    public static KineticInterfaceBlockEntity getStationaryInterfaceAt(Level world, BlockPos pos, BlockState state,
                                                                         Direction facing) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof KineticInterfaceBlockEntity psi))
            return null;
         BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() != state.getBlock())
            return null;
        if (blockState.getValue(KineticInterfaceBlock.FACING) != facing.getOpposite())
            return null;
        return psi;
    }

    private Optional<Direction> getCurrentFacingIfValid(MovementContext context) {
        Vec3 directionVec = Vec3.atLowerCornerOf(context.state.getValue(KineticInterfaceBlock.FACING)
                .getNormal());
        directionVec = context.rotation.apply(directionVec);
        Direction facingFromVector = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z);
        if (directionVec.distanceTo(Vec3.atLowerCornerOf(facingFromVector.getNormal())) > 1 / 2f)
            return Optional.empty();
        return Optional.of(facingFromVector);
    }

    public static LerpedFloat getAnimation(MovementContext context) {
        if (!(context.temporaryData instanceof LerpedFloat lf)) {
            LerpedFloat nlf = LerpedFloat.linear();
            context.temporaryData = nlf;
            return nlf;
        }
        return lf;
    }
}
