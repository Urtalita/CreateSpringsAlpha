package com.Portality.createsprings.blocks.advanced.friction_welder;

import com.Portality.createsprings.recipe.ModRecipes;
import com.Portality.createsprings.recipe.Welding.WelderRecipe;
import com.Portality.createsprings.recipe.Welding.WelderRecipeSpeed;
import com.Portality.createsprings.recipe.Welding.WelderRecipeWrapper;
import com.Portality.createsprings.utill.Helpers.ParticleHelper;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public class WelderBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    public float HeadMove = 0;
    private float prevHeadMove = 0;
    public final Vec3i movementDirection;
    private boolean velding = false;
    private int cooldown = 0;
    private WelderRecipeSpeed recipeSpeed = WelderRecipeSpeed.NORMAL;
    public boolean stopped = false;
    public boolean running = false;

    private BlockState CraftState1;
    private BlockState CraftState2;
    private ItemStack CraftResult;

    protected float angle;
    private float prevAngle;


    public WelderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        movementDirection = getBlockState().getValue(FACING).getOpposite().getNormal();
    }

    public BlockState getCraftState(){
        return CraftState1;
    }

    public float getInterpolatedAngle(float partialTicks) {
        if (isVirtual())
            return Mth.lerp(partialTicks + .5f, prevAngle, angle);
        if (!running)
            return 0f;
        float angularSpeed = getAngularSpeed();
        return Mth.lerp(partialTicks, angle, angle + angularSpeed);
    }

    public float getAngularSpeed() {
        float speed = convertToAngular(getSpeed());
        if (getSpeed() == 0)
            speed = 0;
        if (level.isClientSide) {
            speed *= ServerSpeedProvider.get();
        }
        return speed;
    }

    public void assemble() {
        if(cooldown != 0){return;}

        Direction facing = getBlockState().getValue(FACING);

        BlockPos block1Pos = worldPosition.relative(facing, 1);
        BlockPos block2Pos = worldPosition.relative(facing, 2);

        CraftState1 = level.getBlockState(block1Pos);
        CraftState2 = level.getBlockState(block2Pos);

        if (CraftState1 == null || CraftState2 == null) return;
        // Поиск подходящего рецепта
        Optional<RecipeHolder<WelderRecipe>> recipe = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.WELDER_TYPE.get())
                .stream()
                .filter(r -> r.value().matches(new WelderRecipeWrapper(new ItemStack(CraftState1.getBlock()), new ItemStack(CraftState2.getBlock())), level))
                .findFirst();

        if (recipe.isPresent()) {
            recipeSpeed = recipe.get().value().getSpeed();
            CraftResult = recipe.get().value().getResult().copy();

            Optional<WelderBlockEntity> OweldBe = FindWelderWelding();

            if (OweldBe.isPresent()) {
                OweldBe.get().recipeSpeed = this.recipeSpeed;
                OweldBe.get().CraftResult = CraftResult.copy();
            }
        } else {return;}

        level.setBlock(worldPosition.relative(getBlockState().getValue(FACING)), Blocks.AIR.defaultBlockState(), 3);
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

        running = true;
        stopped = false;
        angle = 0;
        sendData();
    }

    @Override
    public void tick() {
        super.tick();

        prevAngle = angle;
        prevHeadMove = HeadMove;

        if(!running && !stopped && getSpeed() != 0){
                BlockPos pos = worldPosition.relative(getBlockState().getValue(FACING));
                BlockPos pos2 = pos.relative(getBlockState().getValue(FACING));
                BlockState state = level.getBlockState(pos);
                BlockState state2 = level.getBlockState(pos2);
                if(!state.isAir() && !state2.isAir()){
                    Optional<WelderBlockEntity> OweldBe = FindWelderWelding();
                    if(OweldBe.isPresent()){
                        if(OweldBe.get().getSpeed() != 0){
                            if(!OweldBe.get().running){
                                OweldBe.get().assemble();
                                if(OweldBe.get().running){
                                    assemble();
                                }
                            }
                        }
                    }
                } else {
                    Optional<WelderBlockEntity> OweldBe = FindWelderWelding();
                    if(OweldBe.isPresent()){
                        if(OweldBe.get().running){
                            assemble();
                            CraftState2 = OweldBe.get().CraftState1;
                        }
                    }
                }
        }

        if (level.getGameTime() % 5 == 0) {
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
                prevHeadMove = 0;
            }
        }

        if(velding){
            Welding();
        }

        if(cooldown > 0){cooldown--;}

        if(running){
            float angularSpeed = getAngularSpeed();
            float newAngle = angle + angularSpeed;
            angle = (float) (newAngle % 360);
        }
    }

    private void Welding(){
        float combinedSpeed = (Math.abs(this.speed) / 256f) * recipeSpeed.getSpeedValue();

        if(HeadMove < 500){
            prevHeadMove = HeadMove;
            HeadMove += combinedSpeed;
            renderParticles();

            if(getHeadMove(1) >= 1f){
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

        if (CraftState1 != null && CraftState2 != null) {
            stackInSlot = CraftState1.getBlock().asItem().getDefaultInstance();
            data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
            ParticleHelper.spawnParticles(PartPos, data, amout, level);
        }

        if(CraftResult == null){return;}

        if (CraftResult != null && !CraftResult.isEmpty()) {
            ItemParticleOption resultData = new ItemParticleOption(ParticleTypes.ITEM, CraftResult);
            ParticleHelper.spawnParticles(PartPos, resultData, amout / 2, level);
        }
    }

    public Vec3 MoveWithoutVectors(float Moving){
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
            if (!running){
                return false;
            }
            if (!SecondBe.running){
                return false;
            }
            if(this.speed + SecondBe.speed == 0){
                return true;
            }
        }
        return false;
    }

    public float getHeadMove(float pt){
        return (Mth.lerp(pt, prevHeadMove, HeadMove) / 1000f * (recipeSpeed.getSpeedValue() / 2f) + 0.5f);
    }
    public void disassemble() {
        this.running = false;
        velding = false;
        CraftResult = null;
        cooldown = 5;
        angle = 0;

        CraftState1 = null;
        CraftState2 = null;
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

    public boolean getRunning(){
        return running;
    }

    public void ActivateResipe() {
        if (CraftState1 == null || CraftState2 == null) return;
        // Поиск подходящего рецепта
        Optional<RecipeHolder<WelderRecipe>> recipe = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.WELDER_TYPE.get())
                .stream()
                .filter(r -> r.value().matches(new WelderRecipeWrapper(new ItemStack(CraftState1.getBlock()), new ItemStack(CraftState2.getBlock())), level))
                .findFirst();

        Optional<RecipeHolder<WelderRecipe>> recipe2 = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.WELDER_TYPE.get())
                .stream()
                .filter(r -> r.value().matches(new WelderRecipeWrapper(new ItemStack(CraftState2.getBlock()), new ItemStack(CraftState1.getBlock())), level))
                .findFirst();

        if (recipe.isPresent() || recipe2.isPresent()) {

            Optional<WelderBlockEntity> OweldBe = FindWelderWelding();

            if(OweldBe.isPresent()){
                this.disassemble();
                OweldBe.get().disassemble();
            }

            ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
            CraftResult = null;

            if(optimiseWelding(result, worldPosition.relative(getBlockState().getValue(FACING)))) return;
            if(optimiseWelding(result, worldPosition.relative(getBlockState().getValue(FACING), 2))) return;

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

    public boolean optimiseWelding(ItemStack stack, BlockPos pos) {
        DirectBeltInputBehaviour inv =
                BlockEntityBehaviour.get(level, pos.below(), DirectBeltInputBehaviour.TYPE);
        BlockEntity blockEntityBelow = level.getBlockEntity(pos.below());
        BlockEntity blockEntityAbove = level.getBlockEntity(pos.above());

        // Проверка условий (валидация целей для вставки)
        if (inv == null && !(blockEntityBelow instanceof HopperBlockEntity)
                && !(blockEntityAbove instanceof ChuteBlockEntity chute && chute.getItemMotion() > 0))
            return false;

        if (!(level instanceof ServerLevel))
            return false;

        // 1. Работа с конвейером Create (DirectBeltInputBehaviour)
        if (inv != null) {
            inv.handleInsertion(stack, Direction.UP, false);
            return true;
        }

        // 2. Работа с воронкой (Hopper) через NeoForge Capabilities
        else if (blockEntityBelow instanceof HopperBlockEntity) {
            // В 1.21.1 используем level.getCapability для получения обработчика предметов
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);

            if (handler != null) {
                ItemHandlerHelper.insertItemStacked(handler, stack, false);
                return true;
            }
        }

        // 3. Работа с желобом (Chute) из Create
        else if (blockEntityAbove instanceof ChuteBlockEntity chute && chute.getItemMotion() > 0) {
            if (chute.getItem().isEmpty()) {
                chute.setItem(stack, 0); // В Create 1.21.1 метод может называться setItem или иметь доп. параметры в зависимости от версии либы
                return true;
            }
        }

        return false;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        recipeSpeed = WelderRecipeSpeed.valueOf(compound.getString("RecipeSpeed"));
        velding = compound.getBoolean("welding");
        HeadMove = compound.getFloat("HeadMove");
        cooldown = compound.getInt("cooldown");
        stopped = compound.getBoolean("stopped");

        if (compound.contains("CraftResult")) {
            CraftResult = ItemStack.parse(registries, compound.getCompound("CraftResult"))
                    .orElse(ItemStack.EMPTY);
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("welding", velding);
        compound.putString("RecipeSpeed", recipeSpeed.name());
        compound.putFloat("HeadMove", HeadMove);
        compound.putInt("cooldown", cooldown);
        compound.putBoolean("stopped", stopped);

        super.write(compound, registries, clientPacket);

        if (CraftResult != null) {
            compound.put("CraftResult", CraftResult.save(registries));
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if(running && HeadMove > 0){
            AllSoundEvents.MIXING.playAt(level, worldPosition, 1.25f, 0.75f, true);
        }
    }
}