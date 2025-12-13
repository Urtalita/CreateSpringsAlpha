package net.Portality.createsprings.blocks.advanced.SpringAlloyCasing;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class SpringAlloyEncasedShaftBlock extends EncasedShaftBlock {
    public SpringAlloyEncasedShaftBlock(Properties properties, Supplier<Block> casing) {
        super(properties, casing);
    }

    /*
    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ENCASED_SHAFT.get();
    }

     */
}
