package net.Portality.createsprings.blocks.advanced.friction_welder;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.Portality.createsprings.recipe.ModRecipes;
import net.Portality.createsprings.recipe.Welding.WelderRecipe;
import net.Portality.createsprings.recipe.Welding.WelderRecipeSpeed;
import net.Portality.createsprings.utill.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class WelderBlockEntity extends MechanicalBearingBlockEntity implements IHaveGoggleInformation {
    public static int SearchLimit = 128;
    protected boolean running_;
    protected boolean assembleNextTick_;
    public boolean isMainBlock = true;
    private int distanceToWelder = 0;
    public boolean WeldingMode = true;
    public float HeadMove = 0;
    private final Vec3i movementDirection;
    private boolean velding = false;
    private int tickcount = 0;
    private WelderRecipeSpeed recipeSpeed = WelderRecipeSpeed.NORMAL;

    private BlockState CraftState1;
    private BlockState CraftState2;
    private ItemStack CraftResult;


    public WelderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        movementDirection = getBlockState().getValue(FACING).getOpposite().getNormal();
    }

    @Override
    public void tick() {
        super.tick();
        tickcount++;

        running_ = this.running;
        assembleNextTick_ = this.assembleNextTick;

        if(WeldingMode){
            if (tickcount % 5 == 0) {
                if(CanStartWelding()){
                    velding = true;
                } else {
                    velding = false;
                }
            }

            if (HeadMove > 0 && !velding){
                HeadMove -= 10;
                if(HeadMove < 0){
                    HeadMove = 0;
                } else if (movedContraption != null){
                    movedContraption.moveTo(MoveWithoutVectors(1 + getHeadMove()));
                }
            }

            if(velding){
                Welding();
            }
        }
    }

    private void Welding(){
        float combinedSpeed = (Math.abs(this.speed) / 256f) * recipeSpeed.getSpeedValue();

        if(HeadMove < 500){
            HeadMove += combinedSpeed;;
            renderParticles();

            if (movedContraption != null){
                movedContraption.moveTo(MoveWithoutVectors(1 + getHeadMove()));
            }

            if(HeadMove > 450){
                velding = false;
                ActivateResipe();
            }
        }
    }

    public void renderParticles() {
        Vec3 PartPos = MoveWithoutVectors(2f);

        ItemStack stackInSlot;
        ItemParticleOption data;
        int amout = (int) ((Math.abs(this.speed) / 256f) * 5f);

        if (movedContraption == null){return;}
        for (StructureTemplate.StructureBlockInfo block : movedContraption.getContraption().getBlocks().values()) {
            stackInSlot = block.state().getBlock().asItem().getDefaultInstance();
            data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);

            ParticleHelper.spawnParticles(PartPos, data, amout, level);
        }

        if(CraftResult == null){return;}

        if (CraftResult != null && !CraftResult.isEmpty()) {
            ItemParticleOption resultData = new ItemParticleOption(ParticleTypes.ITEM, CraftResult);
            ParticleHelper.spawnParticles(PartPos, resultData, amout / 2, level);
        }
    }

    private Vec3 MoveWithoutVectors(float Moving){
        float offset = 1 - Moving - 0.5f;
        BlockPos pos = worldPosition;
        return new Vec3(
                (pos.getX() + movementDirection.getX() * offset),
                (pos.getY() + movementDirection.getY() * offset),
                (pos.getZ() + movementDirection.getZ() * offset)
        );
    }

    private boolean CanStartWelding(){
        Optional<WelderBlockEntity> OweldBe = FindWelderWelding();

        if(OweldBe.isPresent()) {
            WelderBlockEntity SecondBe = OweldBe.get();
            if (this.movedContraption == null){
                return false;
            }
            if (SecondBe.movedContraption == null){
                return false;
            }
            if(this.speed + SecondBe.speed == 0){
                return true;
            }
        }
        return false;
    }

    public float getHeadMove(){
        return (HeadMove / 1000f * (recipeSpeed.getSpeedValue() / 2f) + 0.5f);
    }



    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff,
                                     boolean connectedViaAxes, boolean connectedViaCogs) {
        if(isMainBlock && !WeldingMode){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        if (isMainBlock && !WeldingMode){
            BlockPos OtherBlock = FindWelderPos();
            if(OtherBlock != null){
                neighbours.add(OtherBlock);
            }
        }
        return super.addPropagationLocations(block, state, neighbours);
    }

    @Override
    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        if (isMainBlock && !WeldingMode){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        if(isMainBlock || WeldingMode){
            return super.getInterpolatedAngle(partialTicks);
        } else {
            Optional<WelderBlockEntity> OweldBe = FindWelder();
            if(OweldBe.isPresent()) {
                WelderBlockEntity SecondBe = OweldBe.get();
                return SecondBe.getInterpolatedAngle(partialTicks);
            }
            return super.getInterpolatedAngle(partialTicks);
        }
    }


    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        recipeSpeed = WelderRecipeSpeed.valueOf(compound.getString("RecipeSpeed"));
        WeldingMode = compound.getBoolean("WeldingMode");
        isMainBlock = compound.getBoolean("isMain");
        velding = compound.getBoolean("welding");
        HeadMove = compound.getFloat("HeadMove");
        if (compound.contains("CraftResult")) {
            CraftResult = ItemStack.of(compound.getCompound("CraftResult"));
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("WeldingMode", WeldingMode);
        compound.putBoolean("isMain", isMainBlock);
        compound.putBoolean("welding", velding);
        compound.putString("RecipeSpeed", recipeSpeed.name());
        compound.putFloat("HeadMove", HeadMove);

        super.write(compound, clientPacket);

        if (CraftResult != null) {
            compound.put("CraftResult", CraftResult.save(new CompoundTag()));
        }
    }

    @Override
    public void assemble() {

        if (isMainBlock && !WeldingMode){
            Optional<WelderBlockEntity> OweldBe = FindWelder();

            if(OweldBe.isPresent()){
                WelderBlockEntity SecondBe = OweldBe.get();
                SecondBe.isMainBlock = false;
                SecondBe.running = true;

                setChanged();
                SecondBe.setChanged();
            }

            assembleNextTick_ = true;
        } else if(WeldingMode){

            Direction facing = getBlockState().getValue(FACING);

            BlockPos block1Pos = worldPosition.relative(facing, 1);
            BlockPos block2Pos = worldPosition.relative(facing, 2);

            CraftState1 = level.getBlockState(block1Pos);
            CraftState2 = level.getBlockState(block2Pos);

            assembleNextTick_ = true;

            if (CraftState1 == null || CraftState2 == null) return;
            // Поиск подходящего рецепта
            Optional<WelderRecipe> recipe = level.getRecipeManager()
                    .getAllRecipesFor(ModRecipes.WELDER_TYPE.get())
                    .stream()
                    .filter(r -> r.matches(CraftState1, CraftState2))
                    .findFirst();

            if (recipe.isPresent()) {
                recipeSpeed = recipe.get().speed;
                CraftResult = recipe.get().result.copy();

                Optional<WelderBlockEntity> OweldBe = FindWelderWelding();
                if (OweldBe.isPresent()) {
                    OweldBe.get().recipeSpeed = this.recipeSpeed;
                    OweldBe.get().CraftResult = CraftResult.copy();
                }
            }
        }
        if(assembleNextTick_){
            super.assemble();
        }
    }

    @Override
    public void disassemble() {
        super.disassemble();
        if (!WeldingMode){
            Optional<WelderBlockEntity> OweldBe = FindWelder();

            if(OweldBe.isPresent()) {
                WelderBlockEntity SecondBe = OweldBe.get();
                SecondBe.running = false;
                SecondBe.isMainBlock = true;
                setChanged();
                SecondBe.setChanged();
                CraftResult = null;
                SecondBe.CraftResult = null;
            }
        } else {
            velding = false;
        }
    }

    private BlockPos FindWelderPos() {
        Direction facing = getBlockState().getValue(FACING);

        for (int i = 1; i < SearchLimit; i++){
            BlockEntity welderBE = level.getBlockEntity(worldPosition.relative(facing, i));
            BlockState Block = level.getBlockState(worldPosition.relative(facing, i));
            if(Block.isAir()){
                return null;
            }
            if ((welderBE instanceof WelderBlockEntity))
                if(welderBE.getBlockState().getValue(FACING) == facing.getOpposite()){
                    distanceToWelder = i;
                    return worldPosition.relative(facing, i);
                }
        }
        return null;
    }

    private Optional<WelderBlockEntity> FindWelderWelding(){
        if (level == null)
            return Optional.empty();
        Direction facing = getBlockState().getValue(FACING);

        BlockEntity welderBE = level.getBlockEntity(worldPosition.relative(facing, 3));

        if ((welderBE instanceof WelderBlockEntity))
            if(welderBE.getBlockState().getValue(FACING) == facing.getOpposite()){
                return Optional.of((WelderBlockEntity) welderBE);
            }
        return Optional.empty();
    }

    private Optional<WelderBlockEntity> FindWelder() {
        if (level == null)
            return Optional.empty();
        Direction facing = getBlockState().getValue(FACING);

        for (int i = 1; i < SearchLimit; i++){
            BlockEntity welderBE = level.getBlockEntity(worldPosition.relative(facing, i));
            BlockState Block = level.getBlockState(worldPosition.relative(facing, i));
            if(Block.isAir()){
                Optional.empty();
            }
            if ((welderBE instanceof WelderBlockEntity))
                if(welderBE.getBlockState().getValue(FACING) == facing.getOpposite()){
                    distanceToWelder = i;
                    return Optional.of((WelderBlockEntity) welderBE);
                }
        }
        return Optional.empty();
    }

    public void SetAssemble(){
        this.assembleNextTick = true;
    }

    public void ActivateResipe() {
        if (CraftState1 == null || CraftState2 == null) return;
        // Поиск подходящего рецепта
        Optional<WelderRecipe> recipe = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.WELDER_TYPE.get())
                .stream()
                .filter(r -> r.matches(CraftState1, CraftState2))
                .findFirst();

        if (recipe.isPresent()) {

            Optional<WelderBlockEntity> OweldBe = FindWelderWelding();

            if(OweldBe.isPresent()){
                this.movedContraption.kill();
                this.disassemble();
                OweldBe.get().movedContraption.kill();
                OweldBe.get().disassemble();
            }

            ItemStack result = recipe.get().getResultItem(level.registryAccess());
            Vec3 itemPos = MoveWithoutVectors(1 + 1);

            ItemEntity itemEntity = new ItemEntity(
                    level,
                    itemPos.x + 0.5,
                    itemPos.y + 0.5,
                    itemPos.z + 0.5,
                    result
            );
            level.addFreshEntity(itemEntity);
        }
    }
}
