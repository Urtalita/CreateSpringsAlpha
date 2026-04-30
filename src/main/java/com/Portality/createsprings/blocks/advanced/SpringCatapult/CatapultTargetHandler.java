package com.Portality.createsprings.blocks.advanced.SpringCatapult;

import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.client.CSpringsLang;
import com.Portality.createsprings.server.packets.CatapultPlacementPacket;
import com.simibubi.create.content.logistics.depot.EjectorPlacementPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class CatapultTargetHandler {
    static BlockPos currentSelection;
    static BlockPos secondSelection;
    static ItemStack currentItem;
    private static final float MaxRadius = 100;

    @SubscribeEvent
    public static void rightClickingBlocksSelectsThem(PlayerInteractEvent.RightClickBlock event) {
        if (currentItem == null)
            return;
        BlockPos pos = event.getPos();
        Level world = event.getLevel();
        if (!world.isClientSide)
            return;
        Player player = event.getEntity();
        if (player == null || player.isSpectator() || !player.isShiftKeyDown())
            return;

        String key = "spring_catapult.target_set";
        String key2 = "spring_catapult.target2_set";
        String key0 = "spring_catapult.target_cleared";

        ChatFormatting colour = ChatFormatting.GOLD;
        ChatFormatting colour2 = ChatFormatting.AQUA;
        ChatFormatting colour0 = ChatFormatting.GREEN;

        if(currentSelection != null && secondSelection != null){
            secondSelection = null;
            currentSelection = null;

            player.displayClientMessage(CSpringsLang.translateDirect(key0)
                    .withStyle(colour0), true);
        }

        if(currentSelection != null && !currentSelection.equals(pos)){
            secondSelection = pos;
            player.displayClientMessage(CSpringsLang.translateDirect(key2)
                    .withStyle(colour2), true);
        } else {
            currentSelection = pos;
            player.displayClientMessage(CSpringsLang.translateDirect(key)
                    .withStyle(colour), true);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void leftClickingBlocksDeselectsThem(PlayerInteractEvent.LeftClickBlock event) {
        if (currentItem == null)
            return;
        if (!event.getLevel().isClientSide)
            return;
        if (!event.getEntity()
                .isShiftKeyDown())
            return;
        BlockPos pos = event.getPos();

        if (pos.equals(currentSelection)) {
            currentSelection = null;
            event.setCanceled(true);
        }

        if (pos.equals(secondSelection)) {
            secondSelection = null;
            event.setCanceled(true);
        }
    }

    public static void flushSettings(BlockPos pos) {
        LocalPlayer player = Minecraft.getInstance().player;
        String key;
        ChatFormatting colour = ChatFormatting.WHITE;

        if (currentSelection == null){
            key = "weighted_ejector.no_target";
            player.displayClientMessage(CreateLang.translateDirect(key)
                    .withStyle(colour), true);
            currentItem = null;
            currentSelection = null;
            secondSelection = null;
            return;
        }

        if(!isValidState(pos)){
            key = "weighted_ejector.target_not_valid";
            player.displayClientMessage(CreateLang.translateDirect(key)
                    .withStyle(colour), true);
            currentItem = null;
            currentSelection = null;
            secondSelection = null;
            return;
        }

        key = "weighted_ejector.targeting";
        colour = ChatFormatting.GREEN;

        player.displayClientMessage(
                CreateLang.translateDirect(key, currentSelection.getX(), currentSelection.getY(), currentSelection.getZ())
                        .withStyle(colour),
                true);

        CatapultPlacementPacket packet;
        if (secondSelection != null && !secondSelection.equals(currentSelection)) {
            // currentSelection - основная цель, secondSelection - цель в очереди
            packet = new CatapultPlacementPacket(pos, currentSelection, secondSelection);
        } else {
            packet = new CatapultPlacementPacket(pos, currentSelection, null);
        }

        CatnipServices.NETWORK.sendToServer(packet);

        secondSelection = null;
        currentSelection = null;
        currentItem = null;
    }

    public static void tick() {
        Player player = Minecraft.getInstance().player;

        if (player == null)
            return;

        ItemStack heldItemMainhand = player.getMainHandItem();
        if (!ModBlocks.SPRING_CATAPULT.isIn(heldItemMainhand)) {
            currentItem = null;
        } else {
            if (heldItemMainhand != currentItem) {
                currentSelection = null;
                currentItem = heldItemMainhand;
            }
            drawOutline(currentSelection);
            drawSecondOutline(secondSelection);
        }

        draw();
    }

    protected static void draw() {
        Minecraft mc = Minecraft.getInstance();
        if (currentSelection == null)
            return;
        if (currentItem == null)
            return;

        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof BlockHitResult blockRayTraceResult))
            return;
        if (blockRayTraceResult.getType() == HitResult.Type.MISS)
            return;

        BlockPos pos = blockRayTraceResult.getBlockPos();

        int greenColor = 0x9ede73;
        int redColor = 0xff7171;

        Direction direction = blockRayTraceResult.getDirection();
        pos = pos.relative(direction);

        int color = (isValidState(pos)) ? greenColor : redColor;

        AABB bb = new AABB(0, 0, 0, 1, 0, 1).move(pos.getX(), pos.getY(), pos.getZ());
        Outliner.getInstance().chaseAABB("valid", bb)
                .colored(color)
                .lineWidth(1 / 16f);
    }

    private static boolean isValidState(BlockPos pos){
        if(currentSelection != null){
            double distance = Vec3.atCenterOf(pos).distanceTo(Vec3.atCenterOf(currentSelection));
            if(distance > MaxRadius){
                return false;
            }
        }

        if(secondSelection != null){
            double secondDistance = Vec3.atCenterOf(pos).distanceTo(Vec3.atCenterOf(secondSelection));
            if(secondDistance > MaxRadius){
                return false;
            }
        }
        return true;
    }

    public static void drawOutline(BlockPos selection) {
        Level world = Minecraft.getInstance().level;
        if (selection == null)
            return;


        BlockPos pos = selection;
        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getShape(world, pos);
        AABB boundingBox = shape.isEmpty() ? new AABB(BlockPos.ZERO) : shape.bounds();
        Outliner.getInstance().showAABB("target", boundingBox.move(pos))
                .colored(0xffcb74)
                .lineWidth(1 / 16f);
    }

    public static void drawSecondOutline(BlockPos selection) {
        Level world = Minecraft.getInstance().level;
        if (selection == null)
            return;

        BlockPos pos = selection;
        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getShape(world, pos);
        AABB boundingBox = shape.isEmpty() ? new AABB(BlockPos.ZERO) : shape.bounds();
        Outliner.getInstance().showAABB("secondTarget", boundingBox.move(pos))
                .colored(0x7FCDE0)
                .lineWidth(1 / 16f);
    }
}
