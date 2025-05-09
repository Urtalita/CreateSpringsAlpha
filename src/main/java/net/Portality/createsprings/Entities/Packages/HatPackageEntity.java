package net.Portality.createsprings.Entities.Packages;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.Projectile.SpringProjectile;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.hat.HatItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PlayMessages;

public class HatPackageEntity extends PackageEntity {

    public ItemStack contains;

    public int red = 255;
    public int green = 255;
    public int blue = 255;

    public int red1 = 255;
    public int green1 = 255;
    public int blue1 = 255;

    public HatPackageEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public HatPackageEntity(Level worldIn, double x, double y, double z) {
        this(ModEntities.HAT_PACKAGE.get(), worldIn);
        this.setPos(x, y, z);
        this.refreshDimensions();
    }

    public static PackageEntity fromItemStack(Level world, Vec3 position, ItemStack itemstack) {
        PackageEntity packageEntity = ModEntities.HAT_PACKAGE
                .create(world);
        packageEntity.setPos(position);
        packageEntity.setBox(itemstack);
        return packageEntity;
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<HatPackageEntity> boxBuilder = (EntityType.Builder<HatPackageEntity>) builder;
        return boxBuilder.setCustomClientFactory(HatPackageEntity::spawn)
                .sized(1, 1);
    }
    public static HatPackageEntity spawn(PlayMessages.SpawnEntity spawnEntity, Level world) {
        HatPackageEntity packageEntity =
                new HatPackageEntity(world, spawnEntity.getPosX(), spawnEntity.getPosY(), spawnEntity.getPosZ());
        packageEntity.setDeltaMovement(spawnEntity.getVelX(), spawnEntity.getVelY(), spawnEntity.getVelZ());
        packageEntity.clientPosition = packageEntity.position();
        return packageEntity;
    }

    public static HatPackageEntity fromDroppedItem(Level world, Entity originalEntity, ItemStack itemstack) {
        HatPackageEntity packageEntity = ModEntities.HAT_PACKAGE.get()
                .create(world);

        Vec3 position = originalEntity.position();
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
        pPlayer.setItemInHand(pHand, HatItem.writeStackToNBT(new ItemStack(ModItems.HAT.get()), contains));
        level().playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                .75f + level().random.nextFloat());
        remove(RemovalReason.DISCARDED);
        return InteractionResult.SUCCESS;
    }

    public void setContains(ItemStack contains){
        this.contains = contains;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Contains", contains.serializeNBT());

        compound.putInt("red", red);
        compound.putInt("green", green);
        compound.putInt("blue", blue);

        compound.putInt("red1", red1);
        compound.putInt("green1", green1);
        compound.putInt("blue1", blue1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        contains = ItemStack.of(compound.getCompound("Contains"));
        refreshDimensions();

        red = compound.getInt("red");
        green = compound.getInt("green");
        blue = compound.getInt("blue");

        red1 = compound.getInt("red1");
        green1 = compound.getInt("green1");
        blue1 = compound.getInt("blue1");
    }

    @Override
    protected void dropAllDeathLoot(DamageSource pDamageSource) {
        super.dropAllDeathLoot(pDamageSource);
        ItemStack itemstack = contains;

        if (itemstack.getItem() instanceof SpawnEggItem sei && level() instanceof ServerLevel sl) {
            EntityType<?> entitytype = sei.getType(itemstack.getTag());
            Entity entity =
                    entitytype.spawn(sl, itemstack, null, blockPosition(), MobSpawnType.SPAWN_EGG, false, false);
            if (entity != null)
                itemstack.shrink(1);
        }

        if (itemstack.isEmpty())
            return;
        ItemEntity entityIn = new ItemEntity(level(), getX(), getY(), getZ(), itemstack);
        level().addFreshEntity(entityIn);
    }
}
