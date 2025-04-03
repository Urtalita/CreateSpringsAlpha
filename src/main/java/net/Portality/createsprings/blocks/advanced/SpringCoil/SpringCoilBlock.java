package net.Portality.createsprings.blocks.advanced.SpringCoil;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class SpringCoilBlock extends ShaftBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    private static Vec3 dirBoxStart;
    private static Vec3 dirBoxEnd;

    public SpringCoilBlock(Properties properties, Vec3 dirBoxStart, Vec3 dirBoxEnd) {
        super(properties);
        this.dirBoxStart = dirBoxStart;
        this.dirBoxEnd = dirBoxEnd;
    }
}
