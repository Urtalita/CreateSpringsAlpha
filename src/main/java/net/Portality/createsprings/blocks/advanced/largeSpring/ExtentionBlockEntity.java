package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.blocks.ModBlocks;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlockEntity.DEFAULT_HARDNESS;
import static net.Portality.createsprings.utill.Helpers.CspringsMath.calcPosM;
import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class ExtentionBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private BlockPos controlerPos;
    public ScrollValueBehaviour targetHardness;

    public ExtentionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        Integer max = AllConfigs.server().kinetics.maxRotationSpeed.get() * 2;

        targetHardness = new ScrollValueBehaviour(Component.translatable("spring.hardness"),
                this, new ExtentionBlockEntity.ExtentionValueBoxTransform());
        targetHardness.between(1, max);
        targetHardness.value = (int) DEFAULT_HARDNESS;
        targetHardness.withCallback(this::updateHardness);

        behaviours.add(targetHardness);
    }

    private void updateHardness(int value){
        BlockPos controller = getBePos(worldPosition, getBlockState().getValue(FACING), level);
        if(!(level.getBlockEntity(controller) instanceof LargeSpringBlockEntity controllerBe)){return;}
        controllerBe.setHardness(value);

        for(int y = 0; y <= controllerBe.getLen() + 1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockPos pos1 = LargeSpringBlockEntity.calcPos(i, y, j, controller, getBlockState().getValue(FACING));
                        if(level.getBlockEntity(pos1) instanceof ExtentionBlockEntity extentionBlockEntity){
                            if(pos1 != worldPosition){
                                extentionBlockEntity.targetHardness.setValue(value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if(controlerPos == null){
            controlerPos = getBePos(worldPosition, getBlockState().getValue(FACING), level);
            return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
        BlockEntity entity = level.getBlockEntity(controlerPos);
        if(entity instanceof LargeSpringBlockEntity springEntity){
            if (springEntity.getStoppedPos() != null){
                CreateLang.translate("spring.stopped").style(ChatFormatting.YELLOW).forGoggles(tooltip);
                return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
            }

            CreateLang.translate("spring.saved").style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.text(" ").add(
                            CreateLang.number(springEntity.stored).style(ChatFormatting.AQUA).space()
                    ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                            .add(CreateLang.number(springEntity.capacity).style(ChatFormatting.AQUA).space()
                                    .add(CreateLang.translate("spring.su").style(ChatFormatting.DARK_GRAY))))
                    .forGoggles(tooltip);

            CreateLang.translate("spring.len").style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.text(" ").add(
                            CreateLang.number(Math.round(springEntity.getPlatePos())).style(ChatFormatting.AQUA).space()
                    ).add(CreateLang.text("/").space().style(ChatFormatting.GRAY)
                            .add(CreateLang.number(springEntity.getLen()).style(ChatFormatting.AQUA).space()))
                    .forGoggles(tooltip);
        }
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if(tag.contains("controllerX")){
            controlerPos = new BlockPos(
                    tag.getInt("controllerX"),
                    tag.getInt("controllerY"),
                    tag.getInt("controllerZ")
            );
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        if(controlerPos != null){
            tag.putInt("controllerX", controlerPos.getX());
            tag.putInt("controllerY", controlerPos.getY());
            tag.putInt("controllerZ", controlerPos.getZ());
        }
        super.write(tag, clientPacket);
    }

    private BlockPos getBePos(BlockPos pos, Direction facing, Level level){
        facing = facing.getOpposite();
        for(int y = 0; y < Config.spring_len + 1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockEntity be = level.getBlockEntity(calcPosM(i, y, j, pos, facing));
                        if(be instanceof LargeSpringBlockEntity){
                            return calcPosM(i, y, j, pos, facing);
                        }
                    }
                }
            }
        }
        return null;
    }

    private class ExtentionValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5f);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() != getBlockState().getValue(FACING).getAxis();
        }

        @Override
        public float getScale() {
            return 0.5f;
        }

    }
}
