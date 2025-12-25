package net.Portality.compat.vs;

import net.minecraft.core.BlockPos;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.world.PhysLevel;

public interface ICreateSpringsForceApplyer {
    void applyForces(BlockPos pos, PhysShip ship, PhysLevel physLevel);
}
