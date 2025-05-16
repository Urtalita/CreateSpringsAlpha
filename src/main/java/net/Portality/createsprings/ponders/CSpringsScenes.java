package net.Portality.createsprings.ponders;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
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
        scene.effects().rotationDirectionIndicator(leverPos.south().south(-1));

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

        scene.idle(200);
        scene.markAsFinished();
    }
}
