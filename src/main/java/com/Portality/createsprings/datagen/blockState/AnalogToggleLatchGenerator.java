package com.Portality.createsprings.datagen.blockState;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.advanced.AnalogToggleLatch.AnalogLatchBlock;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.content.redstone.diodes.ToggleLatchBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.ArrayList;
import java.util.List;

public class AnalogToggleLatchGenerator extends SpecialBlockStateGen {
    private List<ModelFile> models;

    @Override
    protected final int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected final int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(AbstractDiodeBlock.FACING).getOpposite());
    }

    protected <T extends Block> List<ModelFile> createModels(DataGenContext<Block, T> ctx,
                                                             BlockModelProvider prov) {
        List<ModelFile> models = new ArrayList<>(2);
        String name = ctx.getName();
        ResourceLocation off = existing("analog_toggle_latch");
        ResourceLocation on = existing("analog_toggle_latch");
        ResourceLocation offInverse = existing("analog_toggle_latch");
        ResourceLocation onInverse = existing("analog_toggle_latch");

        models.add(prov.withExistingParent(name, off));
        models.add(prov.withExistingParent(name + "_powered", on));
        models.add(prov.withExistingParent(name + "_inverce", offInverse));
        models.add(prov.withExistingParent(name + "_powered" + "_inverce", onInverse));

        return models;
    }

    protected ResourceLocation existing(String name) {
        return CreateSprings.asResource("block/" + name);
    }

    protected int getModelIndex(BlockState state) {
        boolean inverse = state.getValue(ToggleLatchBlock.POWERING);
        boolean powered = state.getValue(ToggleLatchBlock.POWERED);

        if(inverse){
            if (powered) return 3;
            return 2;
        }
        if(powered) return 1;
        return 0;
    }

    @Override
    public final <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                      BlockState state) {
        if (models == null)
            models = createModels(ctx, prov.models());
        return models.get(getModelIndex(state));
    }
}
