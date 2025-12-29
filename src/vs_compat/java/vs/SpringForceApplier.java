package vs;

import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.world.PhysLevel;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class SpringForceApplier implements ICreateSpringsForceApplyer{
    private final SpringForceData data;

    public SpringForceApplier(SpringForceData data) {
        this.data = data;
    }

    public SpringForceData getData(){
        return data;
    }

    public void applyForces(BlockPos pos, PhysShip ship, PhysLevel physLevel) {
        Vector3d dir = data.getDir();
        double thrust = data.getForce();

        if (thrust == 0) {
            return;
        }

        ShipTransform transform = ship.getTransform();

        Vector3d posD = VectorConversionsMCKt.toJOMLD(pos)
                .add(0.5, 0.5, 0.5, new Vector3d())
                .sub(transform.getPositionInShip());

        Vector3d tForce = transform.getShipToWorld().transformDirection(dir.div(transform.getShipToWorldScaling(), dir));
        tForce.mul(thrust);

        ship.applyWorldForceToBodyPos(tForce, posD);
        data.physTick();
    }
}