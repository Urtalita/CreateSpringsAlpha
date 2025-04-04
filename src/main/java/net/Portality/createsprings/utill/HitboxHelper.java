package net.Portality.createsprings.utill;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HitboxHelper {
    public static VoxelShape calculateDierectionalVoxelShape(Direction facing, Vec3 dirBoxStart, Vec3 dirBoxEnd) {
        switch (facing) {
            case SOUTH -> {
                return Block.box(
                        16 - dirBoxEnd.x, dirBoxStart.y, 16 - dirBoxEnd.z,
                        16 - dirBoxStart.x, dirBoxEnd.y, 16 - dirBoxStart.z
                );
            }
            case EAST -> {
                return Block.box(
                        dirBoxStart.z, dirBoxStart.y, 16 - dirBoxEnd.x,
                        dirBoxEnd.z, dirBoxEnd.y, 16 - dirBoxStart.x
                );
            }
            case WEST -> {
                return Block.box(
                        16 - dirBoxEnd.z, dirBoxStart.y, dirBoxStart.x,
                        16 - dirBoxStart.z, dirBoxEnd.y, dirBoxEnd.x
                );
            }
            case UP -> {
                return Block.box(
                        dirBoxStart.x, dirBoxStart.z, dirBoxStart.y,
                        dirBoxEnd.x, dirBoxEnd.z, dirBoxEnd.y
                );
            }
            case DOWN -> {
                return Block.box(
                        dirBoxStart.x, 16 - dirBoxEnd.z, dirBoxStart.y,
                        dirBoxEnd.x, 16 - dirBoxStart.z, dirBoxEnd.y
                );
            }
            default -> {
                return Block.box(
                        dirBoxStart.x, dirBoxStart.y, dirBoxStart.z,
                        dirBoxEnd.x, dirBoxEnd.y, dirBoxEnd.z
                );
            }
        }
    }
}
