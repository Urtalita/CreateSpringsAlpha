package net.Portality.createsprings.Entities.Packages;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class SusPackageEntity extends PackageEntity {

    private boolean inWater = false;
    public float power = 0;

    public SusPackageEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public SusPackageEntity(Level worldIn, double x, double y, double z) {
        this(ModEntities.SUS_PACKAGE.get(), worldIn);
        this.setPos(x, y, z);
        this.refreshDimensions();
    }

    public static AbstractArrow createProjectile(Level level, LivingEntity shooter) {
        SpringProjectile projectile = new SpringProjectile(level, shooter);
        projectile.setBaseDamage(3.5); // Установка урона
        return projectile;
    }

    public static PackageEntity fromItemStack(Level world, Vec3 position, ItemStack itemstack) {
        PackageEntity packageEntity = ModEntities.SUS_PACKAGE
                .create(world);
        packageEntity.setPos(position);
        packageEntity.setBox(itemstack);
        return packageEntity;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        spawnSpring();
        this.remove(RemovalReason.KILLED);
        return true;
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<SusPackageEntity> boxBuilder = (EntityType.Builder<SusPackageEntity>) builder;
        return boxBuilder.setCustomClientFactory(SusPackageEntity::spawn)
                .sized(1, 1);
    }
    public static SusPackageEntity spawn(PlayMessages.SpawnEntity spawnEntity, Level world) {
        SusPackageEntity packageEntity =
                new SusPackageEntity(world, spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ());
        packageEntity.setDeltaMovement(spawnEntity.getVelX(), spawnEntity.getVelY(), spawnEntity.getVelZ());
        packageEntity.clientPosition = packageEntity.position();
        return packageEntity;
    }

    public static SusPackageEntity fromDroppedItem(Level world, Entity originalEntity, ItemStack itemstack) {
        SusPackageEntity packageEntity = ModEntities.SUS_PACKAGE.get()
                .create(world);

        Vec3 position = originalEntity.position();
        packageEntity.power = itemstack.getOrCreateTag().getFloat("Stored");
        packageEntity.setPos(position);
        packageEntity.setBox(itemstack);
        packageEntity.setDeltaMovement(originalEntity.getDeltaMovement()
                .scale(1.5f));

        if (world != null && !world.isClientSide)
            if (ChuteBlock.isChute(world.getBlockState(BlockPos.containing(position.x, position.y + .5f, position.z))))
                packageEntity.setYRot(((int) packageEntity.getYRot()) / 90 * 90);

        return packageEntity;
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (!pPlayer.getItemInHand(pHand)
                .isEmpty())
            return super.interact(pPlayer, pHand);
        if (pPlayer.level().isClientSide)
            return InteractionResult.SUCCESS;
        level().playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                .75f + level().random.nextFloat());
        spawnSpring();
        remove(RemovalReason.DISCARDED);
        return InteractionResult.SUCCESS;
    }

    public void spawnSpring(){
        spawnSpring(this, level(), power);
    }

    public static void spawnSpring(LivingEntity entity, Level level, float power){
        float progress = power / CreateSprings.SPRING_CAPACITY;
        SpringProjectile projectile = (SpringProjectile) createProjectile(level, entity);
        projectile.shootFromRotation(entity, -90, 0, 0.0F,  3 * progress, 1.0F);
        projectile.redirectProjectile(projectile , 10, null);
        projectile.setBox(true);
        level.addFreshEntity(projectile);
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        super.onInsideBlock(state);
        if (state.getBlock() == Blocks.WATER) {
            if(inWater){
               return;
            }
            spawnSpring();
            inWater = true;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        power = compound.getFloat("Stored");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Stored", power);
    }

    @Override
    public void tick() {
        if (firstTick) {
            verifyInitialEntity();
        }

        if (level() instanceof PonderLevel) {
            setDeltaMovement(getDeltaMovement().add(0, -0.06, 0));
            if (position().y < 0.125)
                discard();
        }

        insertionDelay = Math.min(insertionDelay + 1, 30);

    }
}
