package com.Portality.createsprings.client.ponders;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.blocks.advanced.AnalogToggleLatch.AnalogLatchBe;
import com.Portality.createsprings.blocks.advanced.AnalogToggleLatch.AnalogLatchBlock;
import com.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlockEntity;
import com.Portality.createsprings.blocks.advanced.SpringCoil.SpringCoilBlock;
import com.Portality.createsprings.blocks.advanced.friction_welder.WelderBlockEntity;
import com.Portality.createsprings.blocks.advanced.kinetic_interface.KineticInterfaceBlockEntity;
import com.Portality.createsprings.blocks.advanced.largeSpring.LargeSpringBlock;
import com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.instruction.EmitParticlesInstruction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

public class CSpringsScenes {

    public static class SpringPonders {
        public static void spring(SceneBuilder builder, SceneBuildingUtil util) {
            CreateSceneBuilder scene = new CreateSceneBuilder(builder);
            scene.title("spring", "Storing rotational force using Springs");
            scene.configureBasePlate(0, 0, 3);
            scene.world().showSection(util.select().layer(0), Direction.UP);
            scene.showBasePlate();

            Selection leverSelection = util.select().position(1, 1, 0);
            Selection springSelection = util.select().position(1, 1, 1);
            Selection cog = util.select().position(3, 0, 0);

            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 64f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));

            scene.idle(10);
            scene.world().showSection(util.select().position(3, 1, 1), Direction.DOWN);
            scene.idle(10);
            scene.world().showSection(util.select().position(2, 1, 1), Direction.DOWN);
            scene.idle(10);
            scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
            scene.idle(10);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("Springs are able to store rotational force")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 1.5));
            scene.idle(90);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("The direction of rotation does not matter")
                    .pointAt(util.vector().of(2.5, 1.5, 1.5));
            scene.idle(90);

            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 64f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));
            scene.effects().rotationDirectionIndicator(new BlockPos(2, 1, 1));
            scene.idle(30);
            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> -64f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));
            scene.effects().rotationDirectionIndicator(new BlockPos(2, 1, 1));
            scene.idle(30);

            scene.world().showSection(util.select().position(1, 1, 0), Direction.DOWN);
            scene.idle(20);
            scene.world().showSection(util.select().position(0, 1, 1), Direction.DOWN);
            scene.idle(20);

            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 0f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("When powered, the spring will slowly release stored energy")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 2.5));
            scene.idle(90);

            scene.world().modifyBlockEntityNBT(leverSelection, AnalogLeverBlockEntity.class, nbt ->
                    nbt.putInt("State", 1));
            scene.world().toggleRedstonePower(springSelection);
            scene.effects().indicateRedstone(BlockPos.containing(leverSelection.getCenter()));

            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 16f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));

            idleUncompressing(scene, 20, springSelection, 16);
            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("The speed of generated rotation depends on signal strength")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 0.5));
            idleUncompressing(scene, 90, springSelection, 16);

            scene.world().modifyBlockEntityNBT(leverSelection, AnalogLeverBlockEntity.class, nbt ->
                    nbt.putInt("State", 15));
            scene.effects().indicateRedstone(BlockPos.containing(leverSelection.getCenter()));

            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 128f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));

            idleUncompressing(scene, 90, springSelection, 128);
            scene.world().modifyBlockEntityNBT(leverSelection, AnalogLeverBlockEntity.class, nbt ->
                    nbt.putInt("State", 0));
            scene.effects().indicateRedstone(BlockPos.containing(leverSelection.getCenter()));

            scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 16f);
            scene.world().modifyKineticSpeed(cog, f -> -(f / 2));

            scene.overlay().showFilterSlotInput(util.vector().of(1.5, 1.5, 1), Direction.NORTH, 90);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("The charging rate can be changed by configuring spring stiffness")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 0.5));
            scene.idle(90);

            idleUncompressing(scene, 40, springSelection, -160);
            scene.world().modifyBlockEntityNBT(leverSelection, AnalogLeverBlockEntity.class, nbt ->
                    nbt.putInt("State", 1));
            scene.effects().indicateRedstone(BlockPos.containing(leverSelection.getCenter()));
            idleUncompressing(scene, 40, springSelection, 160);

            scene.markAsFinished();
        }

        public static void idleUncompressing(CreateSceneBuilder scene, int ticks, Selection spring, float kineticSpeed) {
            for(int i = 0; i < ticks; i++){
                scene.idle(1);
                scene.world().modifyBlockEntityNBT(spring, SpringBlockEntity.class, nbt ->
                        nbt.putFloat("Stored", nbt.getFloat("Stored") - 0.25f * nbt.getFloat("hardness") * kineticSpeed));
            }
        }

        public static void springSplash(SceneBuilder builder, SceneBuildingUtil util) {
            CreateSceneBuilder scene = new CreateSceneBuilder(builder);
            scene.title("springsplash", "Using rotational force stored in springs");
            scene.configureBasePlate(0, 0, 5);
            scene.world().showSection(util.select().layer(0), Direction.UP);
            scene.showBasePlate();

            scene.idle(15);
            scene.world().showSection(util.select().position(4, 1, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(4, 1, 1), Direction.DOWN);
            scene.idle(20);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("The spring mode can be switched by clicking on block with Tripwire Hook")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(3.5, 1.5, 2.5));

            scene.idle(75);

            ItemStack dirt = new ItemStack(Blocks.TRIPWIRE_HOOK);
            scene.overlay()
                    .showControls(util.vector().of(4.5, 2.5, 2.5), Pointing.DOWN, 40).withItem(dirt);
            scene.idle(40);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("By clicking on spring with empty hand mode switches back")
                    .pointAt(util.vector().of(3.5, 1.5, 2.5));

            scene.idle(80);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("In splash mode spring instantly releases stored energy on activation")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(3.5, 1.5, 2.5));

            scene.idle(80);

            BlockPos buttonPos = new BlockPos(4, 1, 1);
            scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
            scene.effects().indicateRedstone(buttonPos);
            scene.world().modifyBlockEntityNBT(util.select().position(4, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true), false);

            scene.idle(40);

            scene.world().showSection(util.select().position(3, 1, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(3, 1, 1), Direction.DOWN);

            scene.idle(20);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("Springs are able to push entities...")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(2.5, 1.5, 2.5));

            scene.idle(80);

            ElementLink<ParrotElement> flappyBirb = scene.special().createBirb(util.vector().topOf(3, 1, 2), ParrotPose.FlappyPose::new);

            scene.idle(15);
            buttonPos = new BlockPos(3, 1, 1);
            scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
            scene.effects().indicateRedstone(buttonPos);
            scene.world().modifyBlockEntityNBT(util.select().position(3, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true), false);
            scene.idle(1);
            scene.special().moveParrot(flappyBirb, util.vector().of(0, 30, 0), 30);
            scene.idle(20);

            scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(2, 2, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(2, 1, 1), Direction.UP);

            scene.idle(20);

            scene.overlay().showText(50)
                    .placeNearTarget()
                    .text("...and break blocks")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 2.5));

            scene.idle(60);

            buttonPos = new BlockPos(2, 1, 1);
            scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
            scene.effects().indicateRedstone(buttonPos);
            scene.world().modifyBlockEntityNBT(util.select().position(2, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true), false);
            scene.idle(1);
            scene.world().destroyBlock(new BlockPos(2, 2, 2));

            scene.idle(20);

            scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(1, 2, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(1, 1, 1), Direction.UP);

            scene.idle(20);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("to break block spring needs enough charge")
                    .pointAt(util.vector().of(0.5, 1.5, 2.5));

            scene.idle(80);

            buttonPos = new BlockPos(1, 1, 1);
            scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
            scene.effects().indicateRedstone(buttonPos);
            scene.world().modifyBlockEntityNBT(util.select().position(1, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true), false);
            scene.idle(1);

            scene.idle(20);

            scene.world().showSection(util.select().position(0, 1, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(0, 2, 2), Direction.DOWN);
            scene.world().showSection(util.select().position(0, 1, 1), Direction.UP);

            scene.idle(20);

            scene.overlay().showText(50)
                    .placeNearTarget()
                    .text("springs do not break mechanisms and pressure plates")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(-0.5, 1.5, 2.5));

            scene.idle(60);

            buttonPos = new BlockPos(0, 1, 1);
            scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
            scene.effects().indicateRedstone(buttonPos);
            scene.world().modifyBlockEntityNBT(util.select().position(0, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true), false);
            scene.idle(1);

            scene.idle(40);
        }

        public static void explosions(SceneBuilder builder, SceneBuildingUtil util) {
            CreateSceneBuilder scene = new CreateSceneBuilder(builder);
            scene.title("explosion", "Using explosions to charge springs");
            scene.configureBasePlate(0, 0, 3);
            scene.world().showSection(util.select().layer(0), Direction.UP);
            scene.showBasePlate();

            scene.idle(10);
            scene.world().showSection(util.select().position(1, 1, 2), Direction.UP);
            scene.idle(10);
            scene.world().showSection(util.select().position(1, 1, 1), Direction.UP);
            scene.idle(10);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("When an explosion occurs near the spring, spring receives some charge")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 1.5));
            scene.idle(80);

            scene.idle(20);
            explode(scene, new Vec3(1.5, 1.5,1.5));
            scene.world().modifyBlockEntityNBT(util.select().position(1, 1, 2), SpringBlockEntity.class, nbt ->
                    nbt.putFloat("Stored", (float) ModConfigs.common().SPRING_CAPACITY.get() / 2));
            scene.world().hideSection(util.select().position(1, 1, 1), Direction.DOWN);

            scene.idle(20);
            scene.world().hideSection(util.select().position(1, 1, 2), Direction.UP);
            scene.idle(10);
            scene.world().showSection(util.select().position(1, 1, 0), Direction.UP);
            scene.idle(10);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("if spring doesn't face the explosion, it explodes")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 0.5));
            scene.idle(80);

            scene.world().showSection(util.select().position(1, 1, 1), Direction.UP);
            scene.idle(10);

            scene.idle(20);
            explode(scene, new Vec3(1.5, 1.5,1.5));
            scene.world().setBlock(new BlockPos(1, 1, 0), Blocks.AIR.defaultBlockState(), true);
            scene.world().setBlock(new BlockPos(1, 1, 1), Blocks.AIR.defaultBlockState(), false);
            scene.idle(20);

            scene.idle(10);
            scene.world().showSection(util.select().position(1, 1, 2), Direction.UP);
            scene.idle(10);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .text("spring can be charged even by explosions in water")
                    .attachKeyFrame()
                    .pointAt(util.vector().of(1.5, 1.5, 2.5));
            scene.idle(110);
            scene.markAsFinished();
        }
    }

    public static void explode(PonderSceneBuilder builder, Vec3 position){
        ParticleEmitter smoke = builder.effects().simpleParticleEmitter(ParticleTypes.EXPLOSION_EMITTER, position);
        builder.effects().emitParticles(position, smoke, 1, 1);
    }

    public static class SpringCatapultPonders {
            public static void catapult(SceneBuilder builder, SceneBuildingUtil util) {
                CreateSceneBuilder scene = new CreateSceneBuilder(builder);
                scene.title("catapult", "Using spring catapult");
                scene.configureBasePlate(0, 0, 5);
                scene.world().showSection(util.select().layer(0), Direction.UP);
                scene.showBasePlate();

                BlockPos depo = new BlockPos(4, 1, 0);
                BlockPos catapult = new BlockPos(0, 2, 4);

                Selection selectedDepo = util.select().position(depo);
                Selection selectedCatapult = util.select().position(catapult);

                scene.idle(5);
                scene.world().showSection(selectedDepo, Direction.DOWN);
                scene.idle(5);
                scene.world().showSection(util.select().position(catapult.below()), Direction.DOWN);
                scene.idle(5);
                scene.world().showSection(selectedCatapult, Direction.DOWN);

                scene.world().modifyBlockEntityNBT(selectedCatapult, SpringCatapultBlockEntity.class, (nbt)
                        -> nbt.putFloat("stored", 0));

                scene.world().modifyBlockEntityNBT(selectedCatapult, SpringCatapultBlockEntity.class, (nbt)
                        -> nbt.putFloat("speed", 0));

                Object slot = new Object();
                scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, slot, new AABB(depo), 160);

                scene.overlay().showText(70)
                        .attachKeyFrame()
                        .colored(PonderPalette.OUTPUT)
                        .text("Sneak and Right-Click holding an Spring Catapult to select its target location")
                        .pointAt(util.vector().blockSurface(depo, Direction.WEST))
                        .placeNearTarget();
                scene.idle(80);

                scene.world().modifyBlockEntityNBT(selectedCatapult, SpringCatapultBlockEntity.class, (nbt)
                        -> SpringCatapultBlockEntity.encodeTarget("target1_", nbt, depo));

                scene.overlay().showText(70)
                        .colored(PonderPalette.OUTPUT)
                        .text("The placed Catapult will now launch objects to the marked location")
                        .pointAt(util.vector().blockSurface(catapult, Direction.WEST))
                        .placeNearTarget();
                scene.idle(80);

                scene.overlay().showText(70)
                        .attachKeyFrame()
                        .colored(PonderPalette.GREEN)
                        .text("A valid target can be anywhere with in range of 100 blocks")
                        .pointAt(util.vector().blockSurface(catapult, Direction.UP))
                        .placeNearTarget();
                scene.idle(80);

                scene.overlay().showOutlineWithText(util.select().position(catapult.above()), 50)
                        .colored(PonderPalette.OUTPUT)
                        .text("If no Target was selected, it will do nothing")
                        .placeNearTarget();
                scene.idle(80);

                scene.overlay().showText(70)
                        .text("Items placed in the catapult cause it to trigger")
                        .pointAt(util.vector().topOf(catapult))
                        .placeNearTarget();
                scene.idle(80);

                scene.overlay().showText(70)
                        .attachKeyFrame()
                        .text("Supply Rotational Force in order to charge it")
                        .pointAt(util.vector().topOf(catapult))
                        .placeNearTarget();
                scene.idle(80);

                scene.world().modifyBlockEntityNBT(selectedCatapult, SpringCatapultBlockEntity.class, (nbt)
                        -> nbt.putFloat("speed", 256));

                addToCatapult(catapult, new ItemStack(Items.GOLD_INGOT.asItem()), scene);

                scene.idle(100);
            }

            public static void catapultSecondTarget(SceneBuilder builder, SceneBuildingUtil util) {
                CreateSceneBuilder scene = new CreateSceneBuilder(builder);
                scene.title("catapult_second_target", "Using spring catapults");
                scene.configureBasePlate(0, 0, 10);
                scene.world().showSection(util.select().layer(0), Direction.UP);
                scene.showBasePlate();

                BlockPos mainCatapult = new BlockPos(5, 2, 1);
                BlockPos mainTarget = new BlockPos(0, 2, 9);
                BlockPos secondTarget = new BlockPos(9, 2, 9);

                scene.idle(10);
                scene.world().showSection(util.select().layer(1), Direction.DOWN);
                scene.idle(10);
                scene.world().showSection(util.select().position(mainCatapult), Direction.DOWN);
                scene.idle(10);
                scene.world().showSection(util.select().position(mainTarget), Direction.DOWN);

                scene.idle(10);

                Object slot = new Object();
                scene.overlay().chaseBoundingBoxOutline(PonderPalette.OUTPUT, slot, new AABB(mainTarget), 90 + 100);
                scene.overlay().showText(70)
                        .attachKeyFrame()
                        .colored(PonderPalette.OUTPUT)
                        .text("Sneak and Right-Click holding an Spring Catapult to select its main target")
                        .pointAt(util.vector().blockSurface(mainTarget, Direction.WEST))
                        .placeNearTarget();
                scene.idle(80);

                scene.world().modifyBlockEntityNBT(util.select().position(mainCatapult), SpringCatapultBlockEntity.class, (nbt) ->
                        SpringCatapultBlockEntity.encodeTarget("target1_", nbt, mainTarget));

                scene.world().modifyBlockEntityNBT(util.select().position(mainTarget), SpringCatapultBlockEntity.class, (nbt) ->
                        SpringCatapultBlockEntity.encodeTarget("target1_", nbt, mainCatapult));

                scene.idle(10);
                scene.world().showSection(util.select().position(secondTarget), Direction.DOWN);

                slot = new Object();
                scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, slot, new AABB(secondTarget), 100);
                scene.overlay().showText(70)
                        .attachKeyFrame()
                        .colored(PonderPalette.INPUT)
                        .text("Sneak and Right-Click Two times to select its second target")
                        .pointAt(util.vector().blockSurface(secondTarget, Direction.WEST))
                        .placeNearTarget();
                scene.idle(80);

                scene.world().modifyBlockEntityNBT(util.select().position(mainCatapult), SpringCatapultBlockEntity.class, (nbt) ->
                        SpringCatapultBlockEntity.encodeTarget("target2_", nbt, secondTarget));

                scene.world().modifyBlockEntityNBT(util.select().position(secondTarget), SpringCatapultBlockEntity.class, (nbt) ->
                        SpringCatapultBlockEntity.encodeTarget("target1_", nbt, mainCatapult));

                scene.overlay().showText(70)
                        .attachKeyFrame()
                        .colored(PonderPalette.OUTPUT)
                        .text("After receiving or shooting items catapult will switch targets")
                        .pointAt(util.vector().blockSurface(mainTarget, Direction.WEST))
                        .placeNearTarget();
                scene.idle(80);

                addToCatapult(mainTarget, new ItemStack(Items.GOLD_INGOT, 64), scene);

                scene.idle(100);
            }

            public static void addToCatapult(BlockPos pos, ItemStack stack, CreateSceneBuilder builder) {
                builder.addInstruction(ponderScene -> {
                    PonderLevel level = ponderScene.getWorld();
                    if (level == null) return;
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof SpringCatapultBlockEntity springCatapultBlockEntity) {
                        IItemHandler handler = springCatapultBlockEntity.returnHandler();
                        handler.insertItem(0, stack, false);
                    }
                });
            }
        }

    public static class LargeSpringPonders {
            public static void largeSpring(SceneBuilder builder, SceneBuildingUtil util) {
                CreateSceneBuilder scene = new CreateSceneBuilder(builder);
                scene.title("large_spring", "Assembling large springs");
                scene.configureBasePlate(0, 0, 7);
                scene.world().showSection(util.select().layer(0), Direction.UP);
                scene.showBasePlate();

                BlockPos missingCoil = new BlockPos(5, 2, 2);
                BlockPos center = missingCoil.south();

                scene.idle(10);

                scene.world().showSection(util.select().fromTo(missingCoil.below(), missingCoil.above().south(2)), Direction.DOWN);

                scene.overlay().showText(70)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("To construct large spring place a 3*3 square of Large Spring Coils")
                        .pointAt(util.vector().of(5.5, 2, 3.5));

                scene.idle(80);

                scene.overlay().showText(70)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("All placed coils should face the same direction")
                        .pointAt(util.vector().of(5.5, 2, 3.5));

                scene.idle(80);

                for (int i = 1; i < 5; i++) {
                    scene.world().showSection(util.select().fromTo(missingCoil.below().west(i), missingCoil.above().south(2).west(i)), Direction.EAST);
                    scene.idle(10);
                }

                scene.overlay().showText(70)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("Add hollow rings of coils to make spring longer")
                        .pointAt(util.vector().of(3.5, 2, 3.5));

                scene.idle(80);

                scene.overlay().showText(70)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("Finish first layer")
                        .pointAt(util.vector().of(5.5, 2, 3.5));

                scene.idle(80);

                scene.world().setBlock(missingCoil, CSpringsBlocks.LARGE_SPRING_COIL.getDefaultState().setValue(SpringCoilBlock.FACING, Direction.WEST), true);
                scene.world().showSection(util.select().position(missingCoil), Direction.SOUTH);

                scene.idle(5);

                scene.world().replaceBlocks(util.select().layer(1), Blocks.AIR.defaultBlockState(), false);
                scene.world().replaceBlocks(util.select().layer(2), Blocks.AIR.defaultBlockState(), false);
                scene.world().replaceBlocks(util.select().layer(3), Blocks.AIR.defaultBlockState(), false);

                BlockState spring = CSpringsBlocks.LARGE_SPRING.getDefaultState().setValue(LargeSpringBlock.FACING, Direction.WEST).setValue(LargeSpringBlock.LEN, 5);
                scene.world().setBlock(center, spring, true);

                scene.idle(20);
            }

            public static void largeSpringSpeed(SceneBuilder builder, SceneBuildingUtil util) {
                CreateSceneBuilder scene = new CreateSceneBuilder(builder);
                scene.title("large_spring_speed", "Using large springs");
                scene.configureBasePlate(0, 0, 5);
                scene.world().showSection(util.select().layer(0), Direction.UP);
                scene.showBasePlate();

                BlockPos spring = new BlockPos(2, 2, 1);
                BlockPos speedometer = spring.north();
                BlockPos lever = new BlockPos(0, 2, 1);

                scene.idle(10);

                scene.world().showSection(util.select().position(spring), Direction.DOWN);
                scene.idle(10);
                scene.world().showSection(util.select().position(speedometer), Direction.SOUTH);
                scene.idle(10);
                scene.world().showSection(util.select().position(lever), Direction.EAST);
                scene.idle(10);

                scene.overlay().showText(90)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("When central block of first layer is powered large spring will start releasing stored energy")
                        .pointAt(util.vector().of(2.5, 2.5, 2.5));

                scene.idle(100);

                scene.world().toggleRedstonePower(util.select().everywhere());
                scene.world().modifyBlockEntityNBT(util.select().position(spring), LecternBlockEntity.class, (nbt) -> nbt.putBoolean("Generating", true));
                scene.world().modifyKineticSpeed(util.select().everywhere(), (f) -> 256f);

                scene.idle(20);

                scene.overlay().showText(70)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("Spring speed depends on the signal strength")
                        .pointAt(util.vector().of(2.5, 2.5, 2.5));
                scene.idle(80);

                scene.overlay().showText(70)
                        .placeNearTarget()
                        .attachKeyFrame()
                        .text("Large springs also support Splash mode")
                        .pointAt(util.vector().of(2.5, 2.5, 2.5));
                scene.idle(80);

                scene.world().modifyBlockEntityNBT(util.select().position(spring), LecternBlockEntity.class, (nbt) -> nbt.putBoolean("Generating", true));
                scene.world().modifyBlockEntityNBT(util.select().position(spring), LecternBlockEntity.class, (nbt) -> nbt.putBoolean("splashMode", true));

                scene.idle(80);
            }
        }

    public static void welding(SceneBuilder builder, SceneBuildingUtil util) {
            CreateSceneBuilder scene = new CreateSceneBuilder(builder);
            scene.title("welding", "Using welders");
            scene.configureBasePlate(0, 0, 6);
            scene.world().showSection(util.select().layer(0), Direction.UP);
            scene.showBasePlate();

            BlockPos firstWelder = new BlockPos(1, 1, 3);
            BlockPos firstCog = new BlockPos(0, 1, 3);
            BlockPos secondWelder = new BlockPos(4, 1, 3);
            BlockPos secondCog = new BlockPos(5, 1, 3);

            BlockPos block1 = new BlockPos(2, 1, 3);
            BlockPos block2 = new BlockPos(3, 1, 3);

            scene.world().showSection(util.select().position(firstCog), Direction.DOWN);
            scene.world().showSection(util.select().position(secondCog), Direction.DOWN);

            scene.idle(5);

            scene.world().showSection(util.select().position(firstWelder), Direction.DOWN);
            scene.idle(5);
            scene.world().showSection(util.select().position(secondWelder), Direction.DOWN);
            scene.idle(5);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("Friction Welding is an another way to process blocks")
                    .pointAt(util.vector().of(2.5, 2, 2.5));

            scene.idle(80);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("The rotation of both welders must be the same, but opposite by direction")
                    .pointAt(util.vector().of(2.5, 2, 2.5));

            scene.idle(80);

            scene.effects().rotationDirectionIndicator(firstCog);
            scene.effects().rotationDirectionIndicator(secondCog);

            scene.idle(30);

            ElementLink<WorldSectionElement> structure1 = scene.world().showIndependentSection(util.select().position(block1), Direction.SOUTH);
            ElementLink<WorldSectionElement> structure2 = scene.world().showIndependentSection(util.select().position(block2), Direction.SOUTH);

            scene.world().rotateSection(structure1, 0, 0, 0, 0);
            scene.world().rotateSection(structure2, 0, 0, 0, 0);

            scene.overlay().showText(70)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("when there is some blocks between welders welding will be started")
                    .pointAt(util.vector().of(2.5, 2, 2.5));
            scene.idle(80);

            scene.world().modifyBlockEntityNBT(util.select().everywhere(), WelderBlockEntity.class, (nbt) -> nbt.putBoolean("welding", true));

            int duration = 100;

            float angle = duration * 256f / 3;

            scene.world().rotateBearing(firstWelder, angle, duration);
            scene.world().rotateBearing(secondWelder, angle * -1f, duration);

            scene.world().rotateSection(structure1, angle, 0, 0, duration);
            scene.world().rotateSection(structure2, -angle, 0, 0, duration);

            scene.idle(duration);
        }

    public static void PSKI(SceneBuilder builder, SceneBuildingUtil util) {

            CreateSceneBuilder scene = new CreateSceneBuilder(builder);
            scene.title("kinetic_interface", "Using welders");
            scene.configureBasePlate(0, 0, 5);
            scene.world().showSection(util.select().layer(0), Direction.UP);
            scene.showBasePlate();

            BlockPos staticInterface = new BlockPos(3, 3, 2);
            BlockPos spring = new BlockPos(0, 3, 2);
            BlockPos movingInterface = spring.east();
            BlockPos flywheel = staticInterface.east();
            BlockPos bearing = spring.below();

            scene.idle(15);
            scene.world().showSection(util.select().layer(1), Direction.DOWN);
            scene.idle(15);
            scene.world().showSection(util.select().layer(2), Direction.DOWN);
            scene.idle(15);
            //scene.world().showSection(util.select().fromTo(movingInterface, spring), Direction.DOWN);
            ElementLink<WorldSectionElement> structure = scene.world().showIndependentSection(util.select().fromTo(movingInterface, spring.west()), Direction.SOUTH);

            int angle = 360 * 2;
            int duration = 150;

            scene.world().rotateSection(structure, 0, angle, 0, duration);
            scene.world().rotateBearing(bearing, angle, duration);

            scene.idle(20);

            scene.overlay().showText(60)
                    .pointAt(util.vector().topOf(bearing.above(1)))
                    .colored(PonderPalette.RED)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("Moving springs can be tricky to access");
            scene.idle(70);

            scene.world().showSection(util.select().fromTo(flywheel, staticInterface), Direction.DOWN);

            scene.overlay().showText(70)
                    .pointAt(util.vector().topOf(staticInterface))
                    .colored(PonderPalette.GREEN)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("This component can interact with springs without the need to stop the contraption");
            scene.idle(80);

            scene.overlay().showOutlineWithText(util.select().position(staticInterface.west()), 50)
                    .colored(PonderPalette.RED)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("Place a second one with a gap of 1 block inbetween");
            scene.idle(70);

            Selection both = util.select().everywhere();
            Class<KineticInterfaceBlockEntity> psiClass = KineticInterfaceBlockEntity.class;

            scene.world().modifyBlockEntityNBT(both, psiClass, nbt -> {
                nbt.putFloat("Distance", 1);
                nbt.putFloat("Timer", 12);
            });

            scene.overlay().showOutlineWithText(util.select().position(movingInterface), 90)
                    .placeNearTarget()
                    .colored(PonderPalette.GREEN)
                    .attachKeyFrame()
                    .text("While engaged, the stationary interface will represent ALL springs and large springs on the contraption");

            scene.idle(100);

            scene.world().modifyKineticSpeed(util.select().layer(3), (f) -> 64F);

            scene.overlay().showOutlineWithText(util.select().position(movingInterface), 80)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("if stationary interface is rotating, springs on contraption will charge");

            scene.idle(90);

            scene.world().setBlock(staticInterface.north(), Blocks.REDSTONE_BLOCK.defaultBlockState(), true);
            scene.world().showSection(util.select().position(staticInterface.north()), Direction.EAST);
            scene.world().modifyKineticSpeed(util.select().layer(3), (f) -> -64F);

            scene.overlay().showOutlineWithText(util.select().position(staticInterface.north()), 90)
                    .placeNearTarget()
                    .attachKeyFrame()
                    .text("if stationary interface is powered it will generate energy using springs on the contraption");

            scene.idle(100);
    }

    public static void AnalogLatch(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("analog_latch", "Using analog latch");
        scene.configureBasePlate(0, 0, 3);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.showBasePlate();

        Selection latch = util.select().position(1, 1,1);
        BlockPos latchB = new BlockPos(1, 1,1);
        Selection lever = util.select().position(0, 1, 2);
        Selection indicator = util.select().position(2, 1, 0);

        scene.world().showSection(util.select().position(0, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(1, 1, 0), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(2, 1, 0), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Analog latch outputs selected redstone signal on activation")
                .pointAt(latchB.getCenter());
        scene.idle(90);

        Vec3 filterPos = new Vec3(1, 1, 1).add(0.5f, 4/16f, 14/16f);
        scene.overlay().showFilterSlotInput(filterPos, Direction.UP, 90);

        scene.overlay().showText(80)
                .placeNearTarget()
                .attachKeyFrame()
                .text("Output signal can be configured using value panel")
                .pointAt(filterPos.add(-2/16f, 0, 2/16f));
        scene.idle(90);

        scene.world().modifyBlockEntityNBT(latch, AnalogLatchBe.class, nbt ->
                nbt.putInt("ScrollValue", 4));

        scene.idle(20);

        scene.world().modifyBlock(new BlockPos(1, 1, 2), s -> s.setValue(RedStoneWireBlock.POWER, 15), false);
        scene.world().modifyBlock(new BlockPos(1, 1, 0), s -> s.setValue(RedStoneWireBlock.POWER, 4), false);
        scene.world().modifyBlockEntityNBT(indicator, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 4));
        scene.world().toggleRedstonePower(latch);
        scene.effects().indicateRedstone(BlockPos.containing(lever.getCenter()));

        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .text("signal strength: 15")
                .pointAt(new BlockPos(1, 1, 2).getCenter().add(0, -0.5f, 0));
        scene.idle(10);

        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .text("signal strength: 4")
                .pointAt(new BlockPos(1, 1, 0).getCenter().add(0, -0.5f, 0));
        scene.idle(70);
        scene.idle(20);

        scene.world().toggleRedstonePower(util.select().everywhere());
        scene.world().modifyBlockEntityNBT(indicator, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 0));
        scene.idle(10);

        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .text("By clicking on the block, analog latch switches to the reverse mode")
                .pointAt(latchB.getCenter());
        scene.idle(70);

        scene.overlay()
                .showControls(latch.getCenter(), Pointing.DOWN, 40).rightClick();
        scene.idle(40);

        scene.world().modifyBlock(new BlockPos(1, 1, 1), s -> s.setValue(AnalogLatchBlock.POWERING, true), false);
        scene.world().modifyBlock(new BlockPos(1, 1, 0), s -> s.setValue(RedStoneWireBlock.POWER, 4), false);
        scene.world().modifyBlockEntityNBT(indicator, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 4));

        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .text("signal strength: 0")
                .pointAt(new BlockPos(1, 1, 2).getCenter().add(0, -0.5f, 0));
        scene.idle(10);

        scene.overlay().showText(60)
                .placeNearTarget()
                .attachKeyFrame()
                .text("signal strength: 4")
                .pointAt(new BlockPos(1, 1, 0).getCenter().add(0, -0.5f, 0));
        scene.idle(70);

        scene.world().toggleRedstonePower(util.select().everywhere());
        scene.effects().indicateRedstone(BlockPos.containing(lever.getCenter()));
        scene.world().modifyBlockEntityNBT(indicator, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 0));

        scene.idle(30);
        scene.markAsFinished();
    }
}
