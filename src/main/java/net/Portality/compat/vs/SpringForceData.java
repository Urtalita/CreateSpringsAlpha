package net.Portality.compat.vs;

import net.minecraft.core.BlockPos;
import org.joml.Vector3d;

public class SpringForceData {
    public Vector3d getDir() {
        return dir;
    }

    public Vector3d getPos() {
        return pos;
    }

    public float getForce() {
        return force;
    }

    public int getTime() {
        return time;
    }

    private final Vector3d dir;
    private final Vector3d pos;
    private float force;
    private int time;

    public SpringForceData(Vector3d dir, BlockPos pos, float force, int time) {
        this.dir = dir;
        this.pos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        this.force = force;
        this.time = time;
    }

    public SpringForceData(Vector3d dir, Vector3d pos, float force, int time) {
        this.dir = dir;
        this.pos = pos;
        this.force = force;
        this.time = time;
    }

    public void physTick(){
        if(force == 0){return;}
        time -= 1;
        if(time == 0){force = 0;}
    }
}
