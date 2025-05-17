package net.Portality.createsprings.ponders;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;

import static net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACE;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;
import static net.minecraft.world.level.block.LeverBlock.POWERED;

public class CSpringsScenes {
    public static void spring(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("spring", "Storing rotational force using Springs");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.showBasePlate();

        scene.idle(5);
        scene.world().showSection(util.select().position(5, 1, 2), Direction.DOWN);
        scene.idle(10);

        for (int i = 4; i >= 1; i--) {
            scene.idle(5);
            scene.world().showSection(util.select().position(i, 1, 2), Direction.DOWN);
        }
        scene.idle(10);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(1.5, 1.5, 2.5));

        scene.idle(30);
        scene.world().showSection(util.select().position(3, 1, 1), Direction.UP);
        scene.idle(20);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(3.5, 1.5, 2.5));
        scene.idle(35);

        BlockPos leverPos = new BlockPos(3, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(leverPos, leverPos.south()));
        scene.effects().indicateRedstone(leverPos);

        scene.world().modifyKineticSpeed(util.select().fromTo(0, 1, 2, 2, 2, 2), f -> -f);
        scene.effects().rotationDirectionIndicator(leverPos.south().west(1));

        scene.idle(40);

        scene.world().toggleRedstonePower(util.select().fromTo(leverPos, leverPos.south()));
        scene.effects().indicateRedstone(leverPos);

        scene.world().modifyKineticSpeed(util.select().fromTo(0, 1, 2, 2, 2, 2), f -> -f);
        scene.effects().rotationDirectionIndicator(leverPos.south().north());


        scene.idle(40);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.UP);
        scene.world().showSection(util.select().position(4, 1, 1), Direction.UP);
        scene.idle(20);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(1.5, 1.5, 2.5));
        scene.idle(35);

        leverPos = new BlockPos(4, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(leverPos, leverPos.south()));
        scene.effects().indicateRedstone(leverPos);
        scene.world().modifyKineticSpeed(util.select().fromTo(0, 1, 2, 4, 2, 2), f -> f * 0);

        scene.idle(25);

        leverPos = new BlockPos(1, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(leverPos, leverPos.south()));
        scene.effects().indicateRedstone(leverPos);
        scene.world().modifyKineticSpeed(util.select().fromTo(0, 1, 2, 4, 2, 2), f -> f + 16);

        scene.idle(15);
        scene.world().showSection(util.select().position(0, 1, 2), Direction.UP);


        scene.idle(20);
        scene.markAsFinished();
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

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(3.5, 1.5, 2.5));

        scene.idle(50);

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(3.5, 1.5, 2.5));

        scene.idle(50);

        scene.overlay().showText(40)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(3.5, 1.5, 2.5));

        scene.idle(50);

        BlockPos buttonPos = new BlockPos(4, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
        scene.effects().indicateRedstone(buttonPos);
        scene.world().modifyBlockEntityNBT(util.select().position(4, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true),false);

        scene.idle(40);

        scene.world().showSection(util.select().position(3, 1, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(3, 1, 1), Direction.DOWN);

        scene.idle(20);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(2.5, 1.5, 2.5));

        scene.idle(40);

        ElementLink<ParrotElement> flappyBirb = scene.special().createBirb(util.vector().topOf(3, 1, 2), ParrotPose.FlappyPose::new);

        scene.idle(15);
        buttonPos = new BlockPos(3, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
        scene.effects().indicateRedstone(buttonPos);
        scene.world().modifyBlockEntityNBT(util.select().position(3, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true),false);
        scene.idle(1);
        scene.special().moveParrot(flappyBirb, util.vector().of(0, 30, 0), 30);
        scene.idle(20);

        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(2, 2, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(2, 1, 1), Direction.UP);

        scene.idle(20);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(1.5, 1.5, 2.5));

        scene.idle(40);

        buttonPos = new BlockPos(2, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
        scene.effects().indicateRedstone(buttonPos);
        scene.world().modifyBlockEntityNBT(util.select().position(2, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true),false);
        scene.idle(1);
        scene.world().destroyBlock(new BlockPos(2, 2, 2));

        scene.idle(20);

        scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(1, 2, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.UP);

        scene.idle(20);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(0.5, 1.5, 2.5));

        scene.idle(20);

        buttonPos = new BlockPos(1, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
        scene.effects().indicateRedstone(buttonPos);
        scene.world().modifyBlockEntityNBT(util.select().position(1, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true),false);
        scene.idle(1);

        scene.idle(20);

        scene.world().showSection(util.select().position(0, 1, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(0, 2, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(0, 1, 1), Direction.UP);

        scene.idle(20);

        scene.overlay().showText(30)
                .placeNearTarget()
                .text("")
                .pointAt(util.vector().of(-0.5, 1.5, 2.5));

        scene.idle(20);

        buttonPos = new BlockPos(0, 1, 1);
        scene.world().toggleRedstonePower(util.select().fromTo(buttonPos, buttonPos.south()));
        scene.effects().indicateRedstone(buttonPos);
        scene.world().modifyBlockEntityNBT(util.select().position(0, 1, 2), SpringBlockEntity.class, nbt -> nbt.putBoolean("Generating", true),false);
        scene.idle(1);

        scene.idle(40);
    }
}
