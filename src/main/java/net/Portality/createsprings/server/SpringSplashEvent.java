package net.Portality.createsprings.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public class SpringSplashEvent extends Event {
    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public float getSuSecSplashed() {
        return suSecSplashed;
    }

    public boolean isLargeSpring() {
        return isLargeSpring;
    }

    public Vector3d getDir() {
        return dir;
    }

    public Direction getDirection() {
        return direction;
    }

    private final Level level;
    private final BlockPos pos;
    private final float suSecSplashed;
    private final boolean isLargeSpring;
    private final Vector3d dir;
    private final Direction direction;

    public SpringSplashEvent(Level level, BlockPos pos, float suSecSplashed, boolean isLargeSpring, Direction dir) {
        this.level = level;
        this.pos = pos;
        this.suSecSplashed = suSecSplashed;
        this.isLargeSpring = isLargeSpring;
        Vec3i normal = dir.getNormal();
        this.dir = new Vector3d(normal.getX(), normal.getY(), normal.getZ());
        this.direction = dir;
    }
}
