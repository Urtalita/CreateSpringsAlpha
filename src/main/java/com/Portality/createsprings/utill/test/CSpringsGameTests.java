package com.Portality.createsprings.utill.test;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlockEntity;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.ArrayList;
import java.util.List;

import static com.Portality.createsprings.compat.SableCompatHandler.getAreaForDetection;

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

    @GameTest(template = "push_og_small", timeoutTicks = 60)
    public static void spring_phys_push_og(final GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer(level);
        if (plotContainer == null) {
            throw new IllegalStateException("Plot container not found in level");
        }

        final SubLevelPhysicsSystem physicsSystem = plotContainer.physicsSystem();
        if (physicsSystem == null) {
            throw new IllegalStateException("Plot container does not have physics");
        }

        final BlockPos stonePos = helper.absolutePos(new BlockPos(1, 2, 1));
        final BlockPos redstoneBlockPos = helper.absolutePos(new BlockPos(0, 2, 0));

        final BoundingBox3i bounds = new BoundingBox3i(
                stonePos.getX(), stonePos.getY(), stonePos.getZ(),
                stonePos.getX(), stonePos.getY(), stonePos.getZ()
        );

        final List<BlockPos> expectedStates = new ArrayList<>();
        expectedStates.add(stonePos);
        final ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks(level, stonePos, expectedStates, bounds);
        helper.setBlock(stonePos, Blocks.AIR.defaultBlockState());

        helper.pullLever(redstoneBlockPos);
        helper.pullLever(new BlockPos(0, 2, 2));

        helper.succeedWhen(() -> {
            SpringBlockEntity springBlockEntity = (SpringBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
            springBlockEntity.splashMode = true;

            helper.assertTrue(springBlockEntity != null, "Spring was missing!");
            boolean hasSubLevel = false;

            for (SubLevel subLevelIn : Sable.HELPER.getAllIntersecting(springBlockEntity.getLevel(), getAreaForDetection(springBlockEntity))) {
                hasSubLevel = true;
            }

            helper.assertTrue(hasSubLevel && !springBlockEntity.splashMode, "Spring can't push");
        });
    }
}
