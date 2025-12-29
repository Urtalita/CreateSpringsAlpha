package vs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipPhysicsListener;
import org.valkyrienskies.core.api.world.PhysLevel;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public final class SpringForceAttachment implements ShipPhysicsListener {

    public Map<BlockPos, ICreateSpringsForceApplyer> appliers = new ConcurrentHashMap<>();

    public SpringForceAttachment() {}

    @Override
    public void physTick(@NotNull PhysShip ship, @NotNull PhysLevel physLevel) {
        appliers.forEach((pos, applier) -> {
            applier.applyForces(pos, ship, physLevel);
            if(applier instanceof SpringForceApplier springForceApplier){
                if(springForceApplier.getData().getTime() == 0){
                    removeApplier(pos);
                }
            }
        });
    }

    public void addApplier(BlockPos pos, ICreateSpringsForceApplyer applier) {
        appliers.put(pos, applier);
    }

    public void removeApplier(BlockPos pos) {
        appliers.remove(pos);
    }

    @Nullable
    public ICreateSpringsForceApplyer getApplierAtPos(BlockPos pos) {
        return appliers.get(pos);
    }

    public void addSpringThruster(BlockPos pos, SpringForceData data) {
        addApplier(pos, new SpringForceApplier(data));
    }

    public void removeThruster(BlockPos pos) {
        if (getThrusterAtPos(pos) != null){
            removeApplier(pos);
        }
    }

    @Nullable
    public SpringForceData getThrusterAtPos(BlockPos pos) {
        ICreateSpringsForceApplyer applier = getApplierAtPos(pos);
        if (applier instanceof SpringForceApplier springThruster) {
            return springThruster.getData();
        } else {
            return null;
        }
    }

    public static SpringForceAttachment get(LoadedServerShip ship) {
        SpringForceAttachment attachment = ship.getAttachment(SpringForceAttachment.class);
        if (attachment == null) {
            attachment = new SpringForceAttachment();
            ship.setAttachment(attachment);
        }
        return attachment;
    }

    public static SpringForceAttachment get(ServerLevel level, Vector3d pos) {
        LoadedServerShip ship = VSGameUtilsKt.getLoadedShipManagingPos(level, (Vector3dc) pos);
        return ship != null ? get(ship) : null;
    }
}
