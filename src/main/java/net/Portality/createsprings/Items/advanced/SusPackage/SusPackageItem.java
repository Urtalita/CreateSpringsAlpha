package net.Portality.createsprings.Items.advanced.SusPackage;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import net.Portality.createsprings.Config;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.Packages.SusPackageEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.List;

public class SusPackageItem extends PackageItem {
    public SusPackageItem(Properties properties, PackageStyles.PackageStyle style) {
        super(properties.stacksTo(1)
                , style);
    }

    @Override
    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        return SusPackageEntity.fromDroppedItem(world, location, itemstack);
    }

    @Override
    public InteractionResultHolder<ItemStack> open(Level worldIn, Player playerIn, InteractionHand handIn) {
        SusPackageEntity.spawnSpring(playerIn, worldIn, playerIn.getItemInHand(handIn).getOrCreateTag().getFloat("Stored"));
        return super.open(worldIn, playerIn, handIn);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int ticks) {
        if (!(entity instanceof Player player))
            return;
        int i = this.getUseDuration(stack) - ticks;
        if (i < 0)
            return;

        float f = getPackageVelocity(i);
        if (f < 0.1D)
            return;
        if (world.isClientSide)
            return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL, 0.5F, 0.5F);

        ItemStack copy = stack.copy();
        if (!player.getAbilities().instabuild)
            stack.shrink(1);

        Vec3 vec = new Vec3(entity.getX(), entity.getY() + entity.getBoundingBox()
                .getYsize() / 2f, entity.getZ());
        Vec3 motion = entity.getLookAngle()
                .scale(f * 2);
        vec = vec.add(motion);

        SusPackageEntity packageEntity = new SusPackageEntity(world, vec.x, vec.y, vec.z);
        packageEntity.setBox(copy);
        packageEntity.power = stack.getOrCreateTag().getFloat("Stored");
        packageEntity.setDeltaMovement(motion);
        packageEntity.tossedBy = new WeakReference<>(player);
        world.addFreshEntity(packageEntity);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {

        Vec3 point = context.getClickLocation();
        float h = style.height() / 16f;
        float r = style.width() / 2f / 16f;

        if (context.getClickedFace() == Direction.DOWN)
            point = point.subtract(0, h + .25f, 0);
        else if (context.getClickedFace()
                .getAxis()
                .isHorizontal())
            point = point.add(Vec3.atLowerCornerOf(context.getClickedFace()
                            .getNormal())
                    .scale(r));

        AABB scanBB = new AABB(point, point).inflate(r, 0, r)
                .expandTowards(0, h, 0);
        Level world = context.getLevel();
        if (!world.getEntities(ModEntities.SUS_PACKAGE.get(), scanBB, e -> true)
                .isEmpty())
            return super.useOn(context);

        SusPackageEntity packageEntity = new SusPackageEntity(world, point.x, point.y, point.z);
        ItemStack itemInHand = context.getItemInHand();
        packageEntity.power = itemInHand.getOrCreateTag().getFloat("Stored");
        packageEntity.setBox(itemInHand.copy());
        world.addFreshEntity(packageEntity);
        itemInHand.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        float capacity = Config.spring_capacity;
        CompoundTag tag = pStack.getOrCreateTag();
        pTooltipComponents.add(Component.literal("su: ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.valueOf(tag.getFloat("Stored")))).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(String.valueOf(capacity))).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
