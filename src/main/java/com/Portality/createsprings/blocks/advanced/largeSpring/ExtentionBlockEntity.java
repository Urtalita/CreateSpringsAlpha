package com.Portality.createsprings.blocks.advanced.largeSpring;

import com.Portality.createsprings.blocks.advanced.spring.ISpringBE;
import com.Portality.createsprings.config.ModConfigs;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.Portality.createsprings.blocks.advanced.spring.SpringBlock.getSpringChargeCoefficient;
import static com.Portality.createsprings.blocks.advanced.spring.SpringBlockEntity.DEFAULT_HARDNESS;
import static com.Portality.createsprings.utill.Helpers.CspringsMath.calcPosM;
import static net.minecraft.world.level.block.DirectionalBlock.FACING;

public class ExtentionBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, ISpringBE, ThresholdSwitchObservable {
    public ScrollValueBehaviour targetHardness;
    BlockPos be = null;

    public ExtentionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        Integer max = 256;

        targetHardness = new ScrollValueBehaviour(Component.translatable("spring.hardness"),
                this, new ExtentionValueBoxTransform());
        targetHardness.between(1, max);
        targetHardness.value = (int) DEFAULT_HARDNESS;
        targetHardness.withCallback(this::updateHardness);

        behaviours.add(targetHardness);
    }

    private void updateHardness(int value){
        BlockPos controller = getBePos(worldPosition, getBlockState().getValue(FACING), level);
        if(!(level.getBlockEntity(controller) instanceof LargeSpringBlockEntity controllerBe)){return;}
        controllerBe.setHardness(value * 2);

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

    public void onBlockExploded(BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.center();
        Vec3 BlPos = pos.getCenter();

        Vec3 distVector = BlPos.subtract(ExpPos);
        float distance = (float) distVector.length();
        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING).getOpposite();
        float coef = getSpringChargeCoefficient(facing, pos, ExpPos);

        if(coef < 0.30f){
            return;
        }

        BlockPos pos1 = getBePos(worldPosition, getBlockState().getValue(FACING), level);
        if(pos1 == null){return;}

        if(level.getBlockEntity(pos1) instanceof LargeSpringBlockEntity largeSpringBlockEntity){
            largeSpringBlockEntity.onExploded(distance, 4, pos);
        }
    }

    private BlockPos getBePos(BlockPos pos, Direction facing, Level level){
        facing = facing.getOpposite();
        for(int y = 0; y < ModConfigs.common().SPRING_LEN.get() + 1; y++){
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

    public LargeSpringBlockEntity getOrFindBe(){
        if(be == null){
            be = getBePos(worldPosition, getBlockState().getValue(FACING), level);
            if(be == null){
                be = new BlockPos(Integer.MAX_VALUE, 0, 0);
            } else {
                if(level.getBlockEntity(be) instanceof LargeSpringBlockEntity largeSpringBlockEntity){
                    return largeSpringBlockEntity;
                }
            }
        }
        if(be.equals(new BlockPos(Integer.MAX_VALUE, 0, 0))){
            return null;
        }

        if(level.getBlockEntity(be) instanceof LargeSpringBlockEntity largeSpringBlockEntity){
            return largeSpringBlockEntity;
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

    //ThresholdSwitchObservable

    @Override
    public int getMaxValue() {
        LargeSpringBlockEntity b = getOrFindBe();
        if(b == null){return 0;}
        return (int) (b.capacity / 1e6f);
    }

    @Override
    public int getMinValue() {
        return 0;
    }

    @Override
    public int getCurrentValue() {
        LargeSpringBlockEntity b = getOrFindBe();
        if(b == null){return 0;}
        return (int) (b.progress * b.capacity / 1e6);
    }

    @Override
    public MutableComponent format(int value) {
        return CreateLang.number(value)
                .add(Component.literal(" "))
                .add(CreateLang.translate("large_spring.switch.su"))
                .component();
    }
}