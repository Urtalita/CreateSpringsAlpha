package net.Portality.createsprings.utill;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.piston.PistonContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair; // Правильный импорт

public class LargeSpringContraptionHelper {
    public static PistonContraption create3x3EarthContraption(Direction direction, Level levl) {
        PistonContraption contraption = new PistonContraption();
        BlockState dirt = Blocks.DIRT.defaultBlockState();

        // Генерация 3x3 блоков в плоскости, перпендикулярной направлению
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                BlockPos relativePos = calculateRelativePos(direction, a, b);
                StructureTemplate.StructureBlockInfo blockInfo = new StructureTemplate.StructureBlockInfo(relativePos, dirt, null);
                contraption.addBlock(levl, relativePos, Pair.of(blockInfo, (BlockEntity) null));
            }
        }

        return contraption;
    }

    private static BlockPos calculateRelativePos(Direction dir, int a, int b) {
        return switch (dir) {
            case UP -> new BlockPos(a, 1, b);    // Плоскость XZ над контракцией
            case DOWN -> new BlockPos(a, -1, b);  // Плоскость XZ под контракцией
            case NORTH -> new BlockPos(a, b, -1); // Плоскость XY к северу
            case SOUTH -> new BlockPos(a, b, 1);  // Плоскость XY к югу
            case WEST -> new BlockPos(-1, b, a);  // Плоскость YZ к западу
            case EAST -> new BlockPos(1, b, a);   // Плоскость YZ к востоку
        };
    }

    public static void activateContraption(ServerLevel level, BlockPos anchorPos, Direction facing) throws AssemblyException {
        Contraption contraption = create3x3EarthContraption(facing, level);

        // Создаём трансформацию для позиционирования
        StructureTransform transform = new StructureTransform(anchorPos, facing.getAxis(), Rotation.NONE, Mirror.NONE);
        // Добавляем блоки в мир
        if (contraption.assemble(level, anchorPos)) { // Собираем контракцию
            contraption.addBlocksToWorld(level, transform);
        }
    }
}
