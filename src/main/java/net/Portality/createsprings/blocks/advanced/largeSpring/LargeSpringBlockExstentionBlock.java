package net.Portality.createsprings.blocks.advanced.largeSpring;

import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.foundation.block.IBE;
import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.blocks.advanced.ModBlockEntities;
import net.Portality.createsprings.blocks.advanced.Spring.ISpringBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlock.getSpringChargeCoefficient;
import static net.Portality.createsprings.utill.Helpers.CspringsMath.calcPosM;

public class LargeSpringBlockExstentionBlock extends DirectionalBlock implements IBE<ExtentionBlockEntity>, IProxyHoveringInformation, ISpringBlock {
    public static final IntegerProperty COMPRESSION = IntegerProperty.create("compression", 0, 16);
    public LargeSpringBlockExstentionBlock(Properties p_52591_) {
        super(p_52591_.dynamicShape());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(COMPRESSION, 0)
        );
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.getPosition();
        float coef = getSpringChargeCoefficient(state.getValue(FACING).getOpposite(), pos, ExpPos);

        if(coef < 0.30f){
            super.onBlockExploded(state, level, pos, explosion);
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, COMPRESSION);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onPlace(BlockState state, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(state, p_60567_, p_60568_, p_60569_, p_60570_);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pIsMoving) {
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            return;
        }

        if(pNewState != Blocks.COBBLESTONE.defaultBlockState()){
            if(pNewState.getBlock() != ModBlocks.LARGE_SPRING_EXTENTION.get()){
                if(pNewState.getBlock() != ModBlocks.LARGE_SPRING_COIL.get()){
                    goDeeper(pPos, pState.getValue(FACING).getOpposite(), pLevel);
                }
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    private void goDeeper(BlockPos pos, Direction facing, Level level){
        for(int y = 0; y < ModConfigs.common().SPRING_LEN.get() + 1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockEntity be = level.getBlockEntity(calcPosM(i, y, j, pos, facing));
                        if(be instanceof LargeSpringBlockEntity largeSpringBlockEntity){
                            if(!largeSpringBlockEntity.canDisassemble(largeSpringBlockEntity.getFacing())){
                                level.setBlock(pos,
                                        ModBlocks.LARGE_SPRING_EXTENTION.getDefaultState().setValue(FACING, largeSpringBlockEntity.getFacing()),
                                        3);

                                return;
                            }

                            level.setBlock(calcPosM(i, y, j, pos, facing),
                                    Blocks.AIR.defaultBlockState(),
                                    Block.UPDATE_ALL);
                            return;
                        }
                    }
                }
            }
        }
    }

    private LargeSpringBlockEntity getBe(BlockPos pos, Direction facing, Level level){
        facing = facing.getOpposite();
        for(int y = 0; y < ModConfigs.common().SPRING_LEN.get() + 1; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockEntity be = level.getBlockEntity(calcPosM(i, y, j, pos, facing));
                        if(be instanceof LargeSpringBlockEntity blockEntity){
                            return blockEntity;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int compression = state.getValue(COMPRESSION);

        return Block.box(
                0, 0, 0,
                16, 16 - compression, 16
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, world, pos, context);
    }

    @Override
    public Class<ExtentionBlockEntity> getBlockEntityClass() {
        return ExtentionBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ExtentionBlockEntity> getBlockEntityType() {
        return ModBlockEntities.EXTENTION_BLOCK_ENTITY.get();
    }

    @Override
    public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
        LargeSpringBlockEntity largeSpringBlockEntity = getBe(pos, state.getValue(FACING), level);
        return largeSpringBlockEntity.getBlockPosition();
    }

    @Override
    public boolean canStickTo(BlockState state, BlockState other) {
        if (other.getBlock() == Blocks.SLIME_BLOCK) return true;
        if (other.getBlock() == Blocks.HONEY_BLOCK) return true;
        if (other.getBlock() == ModBlocks.LARGE_SPRING.get()) return true;
        if (other.getBlock() == ModBlocks.LARGE_SPRING_EXTENTION.get()) return true;
        return false;
    }
}
