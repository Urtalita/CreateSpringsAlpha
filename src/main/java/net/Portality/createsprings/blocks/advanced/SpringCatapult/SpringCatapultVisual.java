package net.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.blocks.advanced.Spring.SpringInstance;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Direction;

import static net.Portality.createsprings.blocks.advanced.SpringCatapult.SpringCatapultBlock.CEILING;
import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class SpringCatapultVisual extends SingleAxisRotatingVisual<SpringCatapultBlockEntity> implements SimpleDynamicVisual {
    private final OrientedInstance connections;
    private final OrientedInstance springHolder;
    private final SpringInstance spring;
    private boolean upsideDown;

    public SpringCatapultVisual(VisualizationContext context, SpringCatapultBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(AllPartialModels.SHAFT_HALF));
        upsideDown = blockEntity.getBlockState().getValue(CEILING);

        rotatingModel.rotateToFace(Direction.SOUTH);

        connections = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(CSpringsPartalModels.SPRING_CATAPULT_CONNECTION)).createInstance();

        springHolder = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(CSpringsPartalModels.SPRING_CATAPULT_HOLDER)).createInstance();

        if(upsideDown){
            rotatingModel.rotateToFace(Direction.DOWN);
            spring = new SpringInstance(instancerProvider(), Direction.EAST, getVisualPosition().above().getCenter().add(-0.5, 4/16f - 3 - 9/16f, -0.5), false);
        } else {
            spring = new SpringInstance(instancerProvider(), Direction.EAST, getVisualPosition().above().getCenter().add(-0.5, 4/16f, -0.5), false);
        }

        connections.position(getVisualPosition().above());
        springHolder.position(getVisualPosition().above().above().getCenter().add(-0.5, -12/16f, -0.5));

        if(upsideDown){
            springHolder.translatePosition(0, -3 - 9/16f, 0);
            connections.translatePosition(0, -2, 0);
        }

        animate(partialTick);
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        animate(context.partialTick());
    }

    private void animate(float pt){
        float xAngle = blockEntity.getXAngle(pt);
        float yAngle = blockEntity.getYAngle(pt);

        spring.animate(blockEntity.getProgress(pt));
        spring.rotate(yAngle, xAngle + 180);

        springHolder.identityRotation();
        springHolder.rotateYDegrees(yAngle - 90);
        springHolder.rotateZDegrees(xAngle);
        springHolder.setChanged();

        connections.identityRotation();
        connections.rotateYDegrees(yAngle - 90);

        if(upsideDown){
            connections.rotateXDegrees(180);
        }

        connections.setChanged();

        if(blockEntity.mode == CatapultMode.RAVE){
            var ticks = AnimationTickHolder.getTicks(blockEntity.getLevel());
            int color = Color.rainbowColor(ticks * 100).getRGB();

            springHolder.colorRgb(color);
            connections.colorRgb(color);
        }
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(connections);
        relight(springHolder);
        relight(spring.plate);
        relight(spring.secondPlate);
        spring.rings.forEach(this::relight);
    }

    @Override
    protected void _delete() {
        super._delete();
        spring.deleteSpring();
        connections.delete();
        springHolder.delete();
    }
}
