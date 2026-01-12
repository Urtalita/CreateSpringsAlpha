package net.Portality.createsprings.utill.Helpers;

import com.mojang.math.Axis;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import javax.swing.plaf.PanelUI;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    public static Direction.Axis getPerpendicularAxis(Direction.Axis axis){
        switch (axis){
            case X: return Direction.Axis.Y;
            case Z: return Direction.Axis.X;
            default: return Direction.Axis.Z;
        }
    }

    public static Direction.Axis getSecondPerpendicularAxis(Direction.Axis axis){
        switch (axis){
            case X: return Direction.Axis.Z;
            case Z: return Direction.Axis.Y;
            default: return Direction.Axis.X;
        }
    }

    public static Direction.Axis getPerpendicularAxis(Direction axis){
        return getPerpendicularAxis(axis.getAxis());
    }

    public static Direction.Axis getSecondPerpendicularAxis(Direction axis){
        return getSecondPerpendicularAxis(axis.getAxis());
    }

    public static void MoveToPos(float StartPos, float EndPos, OrientedInstance orientedInstance, float Progress, Vec3i movementDirection, BlockPos pos){
        MoveWithoutVectors(StartPos + (EndPos - StartPos) * Progress ,orientedInstance, movementDirection, pos);
    }

    public static void MoveToPos(float StartPos, float EndPos, OrientedInstance orientedInstance, float Progress, Vec3i movementDirection, Vec3 pos){
        MoveWithoutVectors(StartPos + (EndPos - StartPos) * Progress ,orientedInstance, movementDirection, pos);
    }

    public static void MoveToPos(float StartPos, float EndPos, OrientedInstance orientedInstance, float Progress, Vec3 movementDirection, Vec3 pos){
        MoveWithoutVectors(StartPos + (EndPos - StartPos) * Progress ,orientedInstance, movementDirection, pos);
    }

    public static void MoveWithoutVectors(float Moving, OrientedInstance instance, Vec3 movementDirection, Vec3 pos){
        float offset = 1 - Moving - 0.5f;
        instance.position(
                (float) (pos.x + movementDirection.x * offset),
                (float) (pos.y + movementDirection.y * offset),
                (float) (pos.z + movementDirection.z * offset)
        ).setChanged();
    }

    public static void MoveWithoutVectors(float Moving, OrientedInstance instance, Vec3i movementDirection, BlockPos pos){
        float offset = 1 - Moving - 0.5f;
        instance.position(
                (pos.getX() + movementDirection.getX() * offset),
                (pos.getY() + movementDirection.getY() * offset),
                (pos.getZ() + movementDirection.getZ() * offset)
        ).setChanged();
    }

    public static void MoveWithoutVectors(float Moving, OrientedInstance instance, Vec3i movementDirection, Vec3 pos){
        float offset = 1 - Moving - 0.5f;
        instance.position(
                (float) (pos.x + movementDirection.getX() * offset),
                (float) (pos.y + movementDirection.getY() * offset),
                (float) (pos.z + movementDirection.getZ() * offset)
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

    public static OrientedInstance createInstance(PartialModel model, InstancerProvider provider) {
        return provider.instancer(InstanceTypes.ORIENTED, Models.partial(model)).createInstance();
    }

    public static OrientedInstance setupInstance(PartialModel model, InstancerProvider provider, BlockPos pos, Direction facing){
        OrientedInstance instance = createInstance(model, provider);
        applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
        return instance;
    }

    public static MutableComponent tooltipGold(String key){
        return Component.translatable(key).withStyle(ChatFormatting.GOLD);
    }

    public static MutableComponent tooltipYellow(String key){
        return Component.translatable(key).withStyle(ChatFormatting.YELLOW);
    }

    public static MutableComponent tooltipDarkGray(String key){
        return Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY);
    }

    public static boolean checkForDetails(List<Component> tooltip){
        if(!Screen.hasShiftDown()){
            tooltip.add(Component.translatable("tooltip.create.hold_for_details1").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("tooltip.create.hold_for_details2")));
        } else {
            tooltip.add(Component.translatable("tooltip.create.hold_for_details1").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.WHITE))
                    .append(Component.translatable("tooltip.create.hold_for_details2")));
            tooltip.add(Component.empty());
        }
        return !Screen.hasShiftDown();
    }

    public static Vec3 MoveToPos(float StartPos, float EndPos, float Progress, Vec3i movementDirection, BlockPos pos){
        return MoveWithoutVectors(StartPos + (EndPos - StartPos) * Progress, movementDirection, pos);
    }

    public static Vec3 MoveWithoutVectors(float Moving, Vec3i movementDirection, BlockPos pos){
        float offset = 1 - Moving - 0.5f;
        return new Vec3(
                (pos.getX() + movementDirection.getX() * offset),
                (pos.getY() + movementDirection.getY() * offset),
                (pos.getZ() + movementDirection.getZ() * offset));
    }
}
