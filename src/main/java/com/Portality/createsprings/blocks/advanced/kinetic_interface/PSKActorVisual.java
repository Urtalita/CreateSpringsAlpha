package com.Portality.createsprings.blocks.advanced.kinetic_interface;

import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.phys.Vec3;

import java.util.function.UnaryOperator;

public class PSKActorVisual extends ActorVisual {
    private final PSKInstance instance;
    //private final TransformedInstance connection;

    public PSKActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        super(visualizationContext, world, context);

        instance = new PSKInstance(visualizationContext.instancerProvider(), context.state, context.localPos, false);

        instance.rotatingPulley.light(localBlockLight(), 0);
        instance.top.light(localBlockLight(), 0);

        //connection = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(CSpringsPartalModels.CENTERED_SHAFT))
                //.createInstance();
    }

    public void updateSpeed(float speed){
        instance.setActorRotation(speed);
    }

    @Override
    public void beginFrame() {
        LerpedFloat lf = PortableStorageInterfaceMovement.getAnimation(context);
        //instance.top.setVisible(false); // debug
        //instance.rotatingPulley.setVisible(false); // debug

        instance.tick(lf.settled());
        instance.beginFrame(lf.getValue(AnimationTickHolder.getPartialTicks()));
        if(lf.getValue(AnimationTickHolder.getPartialTicks()) == 0){
            instance.setActorRotation(0);
        }

        //connection.setVisible(false);
        /*
        //if(lf.getValue(AnimationTickHolder.getPartialTicks()) == 0){return;}
        connection.setVisible(false);

        Direction facing = instance.facing;
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 shaftEndOffset = Vec3.atLowerCornerOf(facing.getOpposite().getNormal()).scale(23/16f/ 1.5f);
        Vec3 centerInstancePos = context.contraption.anchor.above(4).getCenter();

        BlockPos instancePos = instance.instancePos;
        Vec3 worldPos = context.position;
        BlockPos anchor = context.contraption.anchor;
        Vec3 posFromWorld = anchor.getCenter().subtract(worldPos);

        Vec3 blockOffset = context.localPos.getCenter();

        // Применяем преобразования к секции

        Vec3 to = transformWorldToContraptionLocal(new Vec3(0, -51, 0), context);
        Vec3 from = context.position.add(shaftEndOffset);

        float scaleFactor = 2/8f;
        scaleConnection(scaleFactor);

        To.position(
                (float)(to.x - blockOffset.x),
                (float)(to.y - blockOffset.y),
                (float)(to.z - blockOffset.z)
        );
        From.position(from).setChanged();
         */
    }
/*
    private void scaleConnection(float scaleFactor){
        float lenShaft = 8/16f;
        float lenWithShaft = 19/16f;
        float compensation = lenShaft * (1 - scaleFactor) / 2;

        connection.setIdentityTransform()
                //.translate(instance.instancePos)
                .center()
               // .rotateYDegrees(instance.angleY)
                //.rotateXDegrees(instance.angleX - 90)
                .translate(0, 0, compensation + lenWithShaft - (lenShaft - scaleFactor/2f))
                .scaleZ(scaleFactor)
                .uncenter();

        connection.setChanged();
    }

 */

    public Vec3 transformWorldToContraptionLocal(Vec3 worldPos, MovementContext context) {
        Vec3 diff = worldPos.subtract(context.position);

        // 2. Получаем повернутые базисные векторы
        UnaryOperator<Vec3> rotation = context.rotation;
        Vec3 basisX = rotation.apply(new Vec3(1, 0, 0));
        Vec3 basisY = rotation.apply(new Vec3(0, 1, 0));
        Vec3 basisZ = rotation.apply(new Vec3(0, 0, 1));

        // 3. Проекция на повернутые оси (ортогональное преобразование)
        return new Vec3(
                diff.dot(basisX),
                diff.dot(basisY),
                diff.dot(basisZ)
        );
    }

    @Override
    protected void _delete() {
        instance.remove();
        //connection.delete();
    }
}
