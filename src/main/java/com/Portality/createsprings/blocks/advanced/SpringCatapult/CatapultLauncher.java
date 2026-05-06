package com.Portality.createsprings.blocks.advanced.SpringCatapult;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;

public class CatapultLauncher {
    double horizontalDistance;
    double verticalDistance;
    int totalFlightTicks;
    double rollFactor = 0.5;
    int parseStepsBetweenPoints = 4;

    double Vy0;
    double Vx0;

    Vector2d[] trajectory;

    private static final double g = -0.08d;

    public CatapultLauncher(BlockPos targetPos, BlockPos sourcePos, int totalFlightTicks){
        recalculateTrajectory(targetPos, sourcePos, totalFlightTicks);
    }

    public double getXInterpolated(int t, float pt){
        return Mth.lerp(pt, trajectory[t].x, trajectory[t + 1].x);
    }

    public double getYInterpolated(int t, float pt){
        return Mth.lerp(pt, trajectory[t].y, trajectory[t + 1].y);
    }

    public float getShootingAngle(){
        return (float) Math.toDegrees(Math.atan2(Vy0, Vx0));
    }

    public Vec3 getSpeedForEntity(float yAngle){
        float sin = Mth.sin(yAngle);
        float cos = Mth.cos(yAngle);

        float finalVx = (float) (Vx0 * sin);
        float finalVz = (float) (Vx0 * cos);

        return new Vec3(finalVx, Vy0, finalVz);
    }

    public BlockPos parseTrajectory(Level level, int phase, float yAngle, BlockPos worldPosition){
        Vector2d trajectoryPoint = trajectory[phase];
        BlockPos parsePos = new BlockPos((int) Math.round((worldPosition.getX() + Math.sin(Math.toRadians(yAngle)) * trajectoryPoint.x)),
                                         (int) Math.round((worldPosition.getY() + trajectoryPoint.y)),
                                         (int) Math.round((worldPosition.getZ() + Math.cos(Math.toRadians(yAngle)) * trajectoryPoint.x)));
        BlockState state = level.getBlockState(parsePos);
        if(state.isAir() || state.getBlock() == Blocks.WATER){
            return null;
        }
        return parsePos;
    }

    public float getRotationAngle(int t, float pt) {
        double interpolatedTime = t + pt;

        double angularVelocity = -rollFactor;
        double rotationAngle = angularVelocity * interpolatedTime;

        return (float) Math.toDegrees(rotationAngle % 360);
    }

    public void recalculateTrajectory(BlockPos targetPos, BlockPos sourcePos, int totalFlightTicks){
        if(targetPos == null){return;}
        if(sourcePos == null){return;}

        this.totalFlightTicks = totalFlightTicks;
        trajectory = new Vector2d[totalFlightTicks];

        double dX = targetPos.getX() - sourcePos.getX();
        double dY = targetPos.getY() - sourcePos.getY();
        double dZ = targetPos.getZ() - sourcePos.getZ();

        horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
        verticalDistance = dY;

        Vx0 = getVx0();
        Vy0 = getVy0();

        for(int t = 0; t < totalFlightTicks; t++){
            double x = xEquation(Vx0, t);
            double y = yEquation(Vy0, t);
            Vector2d vector = new Vector2d(x, y);
            trajectory[t] = vector;
        }
    }

    private double getVx0(){
        return horizontalDistance / totalFlightTicks;
    }

    private double getVy0(){
        return (verticalDistance - (g * totalFlightTicks * totalFlightTicks / 2d)) / totalFlightTicks;
    }

    private double xEquation(double Vx0, int t){
        return Vx0 * t;
    }

    private double yEquation(double Vy0, int t){
        return Vy0 * t + (g * t * t / 2d);
    }
}
