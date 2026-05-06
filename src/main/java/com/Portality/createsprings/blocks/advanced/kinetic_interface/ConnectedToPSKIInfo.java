package com.Portality.createsprings.blocks.advanced.kinetic_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConnectedToPSKIInfo {
    public final BlockPos pos;
    public final BlockEntity entity;
    public final IConnectableToPSKI connectedEntity;

    public ConnectedToPSKIInfo(BlockPos pos, BlockEntity entity, IConnectableToPSKI connectedEntity) {
        this.pos = pos;
        this.entity = entity;
        this.connectedEntity = connectedEntity;
    }
}
