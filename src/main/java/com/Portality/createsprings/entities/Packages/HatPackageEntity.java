package com.Portality.createsprings.entities.Packages;

import com.Portality.createsprings.entities.ModEntities;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.items.advanced.hat.HatItem;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static com.Portality.createsprings.items.advanced.hat.HatItem.setPackageColor;
public class HatPackageEntity extends PackageEntity {

    public ItemStack contains;

    public HatPackageEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public HatPackageEntity(Level worldIn, double x, double y, double z) {
        this(ModEntities.HAT_PACKAGE.get(), worldIn);
        this.setPos(x, y, z);
        this.refreshDimensions();
    }

    public static PackageEntity fromItemStack(Level world, Vec3 position, ItemStack itemstack) {
        HatPackageEntity packageEntity = ModEntities.HAT_PACKAGE
                .create(world);
        packageEntity.setPos(position);
        packageEntity.setBox(setPackageColor(new ItemStack(CSpringsItems.HITBOX_HAT.get()), itemstack));
        packageEntity.setContains(HatItem.readStackFromNBT(itemstack));
        return packageEntity;
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<HatPackageEntity> boxBuilder = (EntityType.Builder<HatPackageEntity>) builder;
        return boxBuilder.sized(1, 1);
        /*.setCustomClientFactory(PackageEntity::spawn)*/
    }

    public static HatPackageEntity fromDroppedItem(Level world, Entity originalEntity, ItemStack itemstack) {
        HatPackageEntity packageEntity = ModEntities.HAT_PACKAGE.get()
                .create(world);

        Vec3 position = originalEntity.position();
        packageEntity.setPos(position);
        packageEntity.setBox(setPackageColor(new ItemStack(CSpringsItems.HITBOX_HAT.get()), itemstack));
        packageEntity.setContains(HatItem.readStackFromNBT(itemstack));
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
        ItemStack hatstack = new ItemStack(CSpringsItems.HAT.get());
        PortativeSteamEngineItem.setBurnStack(hatstack, contains);
        pPlayer.setItemInHand(pHand, HatItem.setPackageColor(hatstack, box));
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
        CompoundTag nbtResult = (CompoundTag) contains.save(level().registryAccess());
        compound.put("Contains", nbtResult);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        CompoundTag tag = compound.getCompound("Contains");
        contains = ItemStack.parseOptional(level().registryAccess(), tag);
        refreshDimensions();
    }


    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource pDamageSource) {
        super.dropAllDeathLoot(level, pDamageSource);
        ItemStack itemstack = contains;

        if (itemstack.getItem() instanceof SpawnEggItem sei && level() instanceof ServerLevel sl) {
            EntityType<?> entitytype = sei.getType(itemstack);
            Entity entity =
                    entitytype.spawn(sl, itemstack, null, blockPosition(), MobSpawnType.SPAWN_EGG, false, false);
            if (entity != null)
                itemstack.shrink(1);
        }

        ItemEntity entityIn = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(CSpringsItems.HAT.get()));
        level().addFreshEntity(entityIn);

        if (itemstack.isEmpty())
            return;

        entityIn = new ItemEntity(level(), getX(), getY(), getZ(), itemstack);
        level().addFreshEntity(entityIn);
    }
}
