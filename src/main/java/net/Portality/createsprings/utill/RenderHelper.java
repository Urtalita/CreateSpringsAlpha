package net.Portality.createsprings.utill;

import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Quaternionf;

public class RenderHelper {

    public static void applyBaseTransformations(OrientedInstance instance, Direction facing) {
        double yRot = AngleHelper.horizontalAngle(facing);
        double xRot = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
        instance.rotateYDegrees((float) yRot)
                .rotateXDegrees((float) xRot);
    }

    public static Axis getRotationAxis(Direction facing){
        if(facing == Direction.UP || facing == Direction.DOWN){
            return Axis.YN;
        } else if (facing == Direction.EAST || facing == Direction.WEST){
            return Axis.XP;
        }
        return Axis.ZN;
    }

    public static void MoveToPos(float StartPos, float EndPos, OrientedInstance orientedInstance, float Progress, Vec3i movementDirection, BlockPos pos){
        MoveWithoutVectors(StartPos + (EndPos - StartPos) * Progress ,orientedInstance, movementDirection, pos);
    }

    public static void MoveWithoutVectors(float Moving, OrientedInstance instance, Vec3i movementDirection, BlockPos pos){
        float offset = 1 - Moving - 0.5f;
        instance.position(
                (pos.getX() + movementDirection.getX() * offset),
                (pos.getY() + movementDirection.getY() * offset),
                (pos.getZ() + movementDirection.getZ() * offset)
        ).setChanged();
    }

    public static Quaternionf getBlockStateOrientation(Direction facing) {
        Quaternionf orientation;

        if (facing.getAxis().isHorizontal()) {
            orientation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.getOpposite()));
        } else {
            orientation = new Quaternionf();
        }

        orientation.mul(Axis.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)));
        return orientation;
    }
}
