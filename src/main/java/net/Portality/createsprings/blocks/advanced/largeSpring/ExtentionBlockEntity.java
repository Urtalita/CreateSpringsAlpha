package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.Portality.createsprings.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.Portality.createsprings.utill.Helpers.CspringsMath.calcPosM;
import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class ExtentionBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private BlockPos controlerPos;
    public ExtentionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if(controlerPos == null){
            controlerPos = getBePos(worldPosition, getBlockState().getValue(FACING).getOpposite(), level);
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
        for(int y = 0; y < Config.spring_len + 1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockEntity be = level.getBlockEntity(calcPosM(i, y, j, pos, facing));
                        if(be instanceof LargeSpringBlockEntity blockEntity){
                            return calcPosM(i, y, j, pos, facing);
                        }
                    }
                }
            }
        }
        return null;
    }
}
