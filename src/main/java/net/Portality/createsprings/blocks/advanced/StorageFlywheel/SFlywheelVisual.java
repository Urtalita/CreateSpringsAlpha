package net.Portality.createsprings.blocks.advanced.StorageFlywheel;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static net.Portality.createsprings.utill.Helpers.RenderHelper.createInstance;

public class SFlywheelVisual extends ShaftVisual<SFlywheelBE> implements SimpleDynamicVisual {
    private final TransformedInstance flywheel;
    private final Direction.Axis axis;
    protected float lastAngle = Float.NaN;

    protected final Matrix4f baseTransform = new Matrix4f();

    public SFlywheelVisual(VisualizationContext visualizationContext, SFlywheelBE sFlywheelBE, float v) {
        super(visualizationContext, sFlywheelBE, v);
        axis = sFlywheelBE.getBlockState().getValue(RotatedPillarKineticBlock.AXIS);

        flywheel = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.FLYWHEEL))
                .createInstance();

        Direction align = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

        flywheel.translate(getVisualPosition())
                .center()
                .rotate(new Quaternionf().rotateTo(0, 1, 0, align.getStepX(), align.getStepY(), align.getStepZ()));

        baseTransform.set(flywheel.pose);

        animate(v);
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        float partialTicks = context.partialTick();

        float speed = blockEntity.visualSpeed.getValue(partialTicks) * 3 / 10f;
        float angle = blockEntity.angle + speed * partialTicks;

        if (Math.abs(angle - lastAngle) < 0.001)
            return;

        animate(angle);

        lastAngle = angle;
    }

    private void animate(float angle) {
        flywheel.setTransform(baseTransform)
                .rotateY(AngleHelper.rad(angle))
                .uncenter()
                .setChanged();
    }

    @Override
    protected void _delete() {
        super._delete();
        flywheel.delete();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(flywheel);
    }
}
