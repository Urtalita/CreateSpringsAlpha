package com.Portality.createsprings.utill.Helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class CspringsMath {
    public static BlockPos blockPosSum(BlockPos pos1, BlockPos pos2){
        return new BlockPos(pos1.getX() + pos2.getX(),
                pos1.getY() + pos2.getY(),
                pos1.getZ() + pos2.getZ());
    }

    public static Vec3 MoveWithoutVectors(float Moving, BlockPos pos, Vec3i movementDirection){
        float offset = 1 - Moving - 0.5f;
        return new Vec3(
                (pos.getX() + movementDirection.getX() * offset),
                (pos.getY() + movementDirection.getY() * offset),
                (pos.getZ() + movementDirection.getZ() * offset)
        );
    }

    public static BlockPos calcPosM(int x, int y, int z, BlockPos pos, Direction facing){
        int dierectionFactor = 1;
        if(facing == Direction.DOWN || facing == Direction.WEST || facing == Direction.NORTH){
            dierectionFactor = -1;
        }
        if(facing == Direction.UP || facing == Direction.DOWN){
            return new BlockPos(
                    (pos.getX() + x),
                    (pos.getY() + y * dierectionFactor),
                    (pos.getZ() + z)
            );
        } else if(facing == Direction.EAST || facing == Direction.WEST){
            return new BlockPos(
                    (pos.getX() + y * dierectionFactor),
                    (pos.getY() + x),
                    (pos.getZ() + z)
            );
        }
        return new BlockPos(
                (pos.getX() + x),
                (pos.getY() + z),
                (pos.getZ() + y * dierectionFactor)
        );
    }
}
