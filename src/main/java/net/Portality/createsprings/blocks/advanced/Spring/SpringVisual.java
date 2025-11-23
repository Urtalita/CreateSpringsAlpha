package net.Portality.createsprings.blocks.advanced.Spring;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Quaternionf;

import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

import static net.Portality.createsprings.utill.Helpers.RenderHelper.*;

public class SpringVisual extends ShaftVisual<SpringBlockEntity> implements SimpleDynamicVisual  {

    public static int SPRING_LEN = 12;
    private final OrientedInstance plate;
    private final List<OrientedInstance> rings = new ArrayList<>();
    private final List<Float> ringPos = new ArrayList<>();
    private final Vec3i movementDirection;
    final Direction facing;
    final Axis rotationAxis;
    final Quaternionf blockOrientation;

    public SpringVisual(VisualizationContext context, SpringBlockEntity blockEntity, float pt) {
        super(context, blockEntity, pt);

        facing = blockEntity.getBlockState().getValue(DirectionalKineticBlock.FACING);

        rotationAxis = getRotationAxis(facing);

        blockOrientation = getBlockStateOrientation(facing);

        movementDirection = facing.getOpposite().getNormal();

        plate = createInstance(CSpringsPartalModels.SPRING_PLATE, instancerProvider());
        for (int i = 0; i < SPRING_LEN; i++) {
            rings.add(createInstance(CSpringsPartalModels.SPRING_PIECE, instancerProvider()));
            ringPos.add((2f+i)/16f);
        }

        applyBaseTransformations(plate, facing);
        for(int i = 0; i< rings.size(); i++){
            applyBaseRotation(rings.get(i), i);
            applyBaseTransformations(rings.get(i), facing);
        }
        animate(pt);
    }

    private void applyBaseTransformations(OrientedInstance instance, Direction facing){
        RenderHelper.applyBaseTransformations(instance, facing);
        instance.rotateXDegrees(-90);
    }

    private void applyBaseRotation(OrientedInstance instance, int index) {
        Quaternionf rot = rotationAxis.rotationDegrees(45 + index * 90);
        instance.rotation(rot).setChanged();
    }

    public void beginFrame(DynamicVisual.Context context) {
        animate(context.partialTick());
    }

    private void animate(float partialTick){
        float progress = blockEntity.getProgress(partialTick);

        MoveToPos(1/16f, 8/16f, plate, progress, movementDirection, getVisualPosition());

        for (int i = 0; i < rings.size(); i++) {
            updateRingPosition(progress, rings.get(i), i);
        }
    }

    private void updateRingPosition(float progress, OrientedInstance ring, int ringIndex) {
        MoveToPos(ringPos.get(ringIndex) + 1/16f, (8f+0.5f*ringIndex - (ringIndex % 4)/2f + 2)/16f , ring, progress, movementDirection, getVisualPosition());
    }

    @Override
    protected void _delete() {
        super._delete();
        plate.delete();
        rings.forEach(OrientedInstance::delete);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(plate);
        rings.forEach(this::relight);
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(plate);
        rings.forEach(consumer);
    }
}
