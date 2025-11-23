package net.Portality.createsprings.Entities.Visual;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.Portality.createsprings.Entities.Packages.HatPackageEntity;
import net.Portality.createsprings.client.CSpringsPartalModels;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class HatPackageVisual extends AbstractEntityVisual<HatPackageEntity> implements SimpleDynamicVisual {
    public final TransformedInstance instance;
    public final TransformedInstance instance2;
    public final TransformedInstance instance3;

    public int red = 255;
    public int green = 255;
    public int blue = 255;

    public int red1 = 255;
    public int green1 = 255;
    public int blue1 = 255;

    public HatPackageVisual(VisualizationContext ctx, HatPackageEntity entity, float partialTick) {
        super(ctx, entity, partialTick);

        PartialModel model = CSpringsPartalModels.HAT;
        PartialModel model2 = CSpringsPartalModels.HAT3;
        PartialModel model3 = CSpringsPartalModels.HAT4;

        instance = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(model))
                .createInstance();

        instance2 = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(model2))
                .createInstance();

        instance3 = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(model3))
                .createInstance();

        CompoundTag tag = entity.box.getOrCreateTag();
        if(!tag.contains("red")){
            animate(partialTick);
            return;
        }
        red = tag.getInt("red");
        green = tag.getInt("green");
        blue = tag.getInt("blue");

        red1 = tag.getInt("red1");
        green1 = tag.getInt("green1");
        blue1 = tag.getInt("blue1");

        animate(partialTick);
    }

    @Override
    public void beginFrame(Context ctx) {
        animate(ctx.partialTick());
    }

    private void animate(float partialTick) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

        Vec3 pos = HatPackageVisual.this.entity.position();
        var renderOrigin = renderOrigin();
        var x = (float) (Mth.lerp(partialTick, this.entity.xo, pos.x) - renderOrigin.getX());
        var y = (float) (Mth.lerp(partialTick, this.entity.yo, pos.y) - renderOrigin.getY());
        var z = (float) (Mth.lerp(partialTick, this.entity.zo, pos.z) - renderOrigin.getZ());

        long randomBits = (long) entity.getId() * 31L * 493286711L;
        randomBits = randomBits * randomBits * 4392167121L + randomBits * 98761L;
        float xNudge = (((float) (randomBits >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float yNudge = (((float) (randomBits >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float zNudge = (((float) (randomBits >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

        instance.setIdentityTransform()
                .translate(x - 0.5 + xNudge, y + yNudge, z - 0.5 + zNudge)
                .rotateYCenteredDegrees(-yaw - 90)
                .light(computePackedLight(partialTick))
                .setChanged();

        instance2.setIdentityTransform()
                .translate(x - 0.5 + xNudge, y + yNudge, z - 0.5 + zNudge)
                .rotateYCenteredDegrees(-yaw - 90)
                .light(computePackedLight(partialTick))
                .color(red1, green1, blue1)
                .setChanged();

        instance3.setIdentityTransform()
                .translate(x - 0.5 + xNudge, y + yNudge, z - 0.5 + zNudge)
                .rotateYCenteredDegrees(-yaw - 90)
                .light(computePackedLight(partialTick))
                .color(red, green, blue)
                .setChanged();
    }

    @Override
    protected void _delete() {
        instance.delete();
        instance2.delete();
        instance3.delete();
    }
}

