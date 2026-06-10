package com.Portality.createsprings.utill.test;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;


@GameTestHolder(CreateSprings.MODID)
public class CSpringsGameTests {
    @GameTest(template = "spring_charge", timeoutTicks = 200)
    public static void springChargeTest(GameTestHelper helper) {
        helper.succeedWhen(() ->{
            SpringBlockEntity springBlockEntity = (SpringBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 1));

            helper.assertTrue(springBlockEntity != null, "Spring was missing!");

            boolean isFullyCharged = springBlockEntity.getProgress(0) == 1;

            helper.assertTrue(isFullyCharged, "Spring is not fully charged");
        });
    }

    @GameTest(template = "ls_assembly", timeoutTicks = 20)
    public static void largeSpringAssemblyTest(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, CSpringsBlocks.LARGE_SPRING_COIL.get().defaultBlockState().setValue(SpringCoilBlock.FACING, Direction.UP));

        helper.succeedWhen(() ->{
            LargeSpringBlockEntity springBlockEntity = (LargeSpringBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 1));
            helper.assertTrue(springBlockEntity != null, "Large spring is missing!");
        });
    }
}
