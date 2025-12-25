package net.Portality.compat.vs;

import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.world.PhysLevel;

public class SpringForceApplier implements ICreateSpringsForceApplyer{
    private final SpringForceData data;

    public SpringForceApplier(SpringForceData data) {
        this.data = data;
    }

    public SpringForceData getData(){
        return data;
    }

    @Override
    public void applyForces(BlockPos pos, PhysShip ship, PhysLevel physLevel) {
        Vector3d dir = data.getDir();
        double thrust = data.getForce();
        Vector3d posD = data.getPos();

        if (thrust == 0) {
            return;
        }

        // Transform force direction from ship relative to world relative
        ShipTransform transform = ship.getTransform();
        Vector3d tForce = transform.getShipToWorld().transformDirection(dir.div(transform.getShipToWorldScaling(), dir));
        tForce.mul(thrust);

        ship.applyInvariantForceToPos(tForce, posD);
        data.physTick();
    }
}
