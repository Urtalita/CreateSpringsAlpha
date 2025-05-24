package net.Portality.createsprings.blocks.advanced.largeSpring;

import net.Portality.createsprings.blocks.ModBlocks;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlock.getClosestDirection;
import static net.Portality.createsprings.blocks.advanced.Spring.SpringBlock.getExplosionPower;
import static net.Portality.createsprings.utill.Helpers.CspringsMath.calcPos;

public class LargeSpringExstentionBlock extends DirectionalBlock {
    public static final IntegerProperty COMPRESSION = IntegerProperty.create("compression", 0, 16);

    public LargeSpringExstentionBlock(Properties p_52591_) {
        super(p_52591_.dynamicShape());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(COMPRESSION, 0)
        );
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        Vec3 ExpPos = explosion.getPosition();
        Vec3 BlPos = pos.getCenter();

        Vec3 distVector = BlPos.subtract(ExpPos);
        float distance = (float) distVector.length();
        float power = getExplosionPower(explosion);
        Direction facing = state.getValue(FACING);
        Direction expDier = getClosestDirection(ExpPos, BlPos);
        if(facing != expDier){return;}


        LargeSpringBlockEntity be = getBe(pos, state.getValue(FACING).getOpposite(), level);
        be.onExploded(distance, power, pos);
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
        if(pNewState != Blocks.COBBLESTONE.defaultBlockState()){
            goDeeper(pPos, pState.getValue(FACING).getOpposite(), pLevel);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    private void goDeeper(BlockPos pos, Direction facing, Level level){
        for(int y = 0; y < 128; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockEntity be = level.getBlockEntity(calcPos(i, y, j, pos, facing));
                        if(be instanceof LargeSpringBlockEntity){
                            level.setBlock(calcPos(i, y, j, pos, facing),
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
        for(int y = 0; y < 128; y++){
            for (int i = -1; i < 2; i++){
                for (int j = -1; j < 2; j++){
                    if(!(i == 0 && j == 0)){
                        BlockEntity be = level.getBlockEntity(calcPos(i, y, j, pos, facing));
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
}
