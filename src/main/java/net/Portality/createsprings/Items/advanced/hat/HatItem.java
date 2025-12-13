package net.Portality.createsprings.Items.advanced.hat;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.Packages.HatPackageEntity;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.hat.render.HatRenderer;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

import static com.simibubi.create.content.logistics.box.PackageItem.getPackageVelocity;

public class HatItem extends Item implements IClientItemExtensions{

    public HatItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new HatRenderer()));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new HatSlotTooltipComponent(readStackFromNBT(stack), stack.getOrCreateTag().getBoolean("goggles")));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        EquipmentSlot equipmentslottype = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = playerIn.getItemBySlot(equipmentslottype);
        if (itemstack1.isEmpty()) {
            playerIn.setItemSlot(equipmentslottype, itemstack.copy());
            itemstack.shrink(1);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        } else {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.success(itemstack);
        }
    }

    public static class HatSlotTooltipComponent implements TooltipComponent {
        public final ItemStack stack;
        public final boolean goggles;

        public HatSlotTooltipComponent(ItemStack stack, boolean goggles) {
            this.stack = stack;
            this.goggles = goggles;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class HatSlotRenderer implements ClientTooltipComponent {
        private final HatSlotTooltipComponent component;

        public HatSlotRenderer(HatSlotTooltipComponent component) {
            this.component = component;
        }

        @Override
        public int getHeight() {
            return 20;
        }

        @Override
        public int getWidth(net.minecraft.client.gui.Font font) {
            return 18;
        }

        @Override
        public void renderImage(net.minecraft.client.gui.Font Font, int x, int y, GuiGraphics GuiGraphics) {
            int moving = 0;
            if (component.stack != ItemStack.EMPTY){
                GuiGraphics.renderFakeItem(component.stack, x, y + 1);
                moving = getHeight() + 2;
            }
            if(component.goggles){
                GuiGraphics.renderFakeItem(AllItems.GOGGLES.asStack(), x + moving, y + 1);
            }
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if(player.isShiftKeyDown()){
            if(!stack.getOrCreateTag().getBoolean("animation")){
                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("animation", true);
                tag.putInt("animation_tick", (int) (AnimationTickHolder.getTicks() % HatRenderer.duration));
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess slotaccess) {
        return addStackedLogick(stack1, stack2, action, player);
    }

    public boolean addStackedLogick(ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        if(addItem(stack1, stack2, action)){
            return true;
        }
        if(removeItem(stack1, stack2, action, player)){
            return true;
        }

        return false;
    }

    private boolean addItem(ItemStack addto, ItemStack add, ClickAction action){
        if(action == ClickAction.PRIMARY){
            if (add.getItem() == AllItems.GOGGLES.asItem()){
                if (!addto.getOrCreateTag().getBoolean("goggles")){
                    add.setCount(0);
                    addto.getOrCreateTag().putBoolean("goggles", true);
                    return true;
                }
            }
            if(addto.getOrCreateTag().getCompound("StoredStack").isEmpty()){
                if(add.getItem() == Items.AIR){return false;}
                writeStackToNBT(addto, add);
                add.setCount(0);
                return true;
            }
        }
        return false;
    }

    private boolean removeItem(ItemStack addto, ItemStack add, ClickAction action, Player player){
        if(action == ClickAction.SECONDARY){
            if(!addto.getOrCreateTag().getCompound("StoredStack").isEmpty()){
                if(add.getItem() != Items.AIR){return true;}
                player.addItem(readStackFromNBT(addto));

                addto.getOrCreateTag().put("StoredStack", ItemStack.EMPTY.getOrCreateTag());
                return true;
            }
            if (addto.getOrCreateTag().getBoolean("goggles")){
                player.addItem(AllItems.GOGGLES.asStack());
                addto.getOrCreateTag().putBoolean("goggles", false);
                return true;
            }
        }
        return false;
    }

    public static ItemStack writeStackToNBT(ItemStack stack1, ItemStack stack2) {
        CompoundTag parentTag = stack1.getOrCreateTag();
        CompoundTag stackTag = new CompoundTag();
        stack2.save(stackTag);
        parentTag.put("StoredStack", stackTag);
        return stack1;
    }

    public static ItemStack readStackFromNBT(ItemStack stack1) {
        CompoundTag parentTag = stack1.getTag();
        if (parentTag != null && parentTag.contains("StoredStack")) {
            CompoundTag stackTag = parentTag.getCompound("StoredStack");
            return ItemStack.of(stackTag);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(Level world, Entity location, ItemStack itemstack) {
        return HatPackageEntity.fromDroppedItem(world, location, itemstack);
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Vec3 point = context.getClickLocation();
        float h = 7 / 16f;
        float r = 11 / 16f / 2f;

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
        ItemStack itemInHand = context.getItemInHand();

        if(itemInHand.getOrCreateTag().getBoolean("prevent_place")){
            itemInHand.getOrCreateTag().putBoolean("prevent_place", false);
            return InteractionResult.SUCCESS;
        }

        HatPackageEntity packageEntity = new HatPackageEntity(world, point.x, point.y, point.z);
        packageEntity.setBox(setPackageColor(new ItemStack(ModItems.HITBOX_HAT.get()), context.getItemInHand()));
        packageEntity.setContains(readStackFromNBT(context.getItemInHand()));
        world.addFreshEntity(packageEntity);
        itemInHand.shrink(1);
        return InteractionResult.SUCCESS;
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

        HatPackageEntity packageEntity = new HatPackageEntity(world, vec.x, vec.y, vec.z);
        packageEntity.setBox(setPackageColor(new ItemStack(ModItems.HITBOX_HAT.get()), stack));
        packageEntity.setContains(readStackFromNBT(stack));
        packageEntity.setDeltaMovement(motion);
        packageEntity.tossedBy = new WeakReference<>(player);
        world.addFreshEntity(packageEntity);
    }

    public static ItemStack setPackageColor(ItemStack hat, ItemStack colorStack){
        CompoundTag tag = hat.getOrCreateTag();
        CompoundTag colorTag = colorStack.getOrCreateTag();

        if(!colorTag.contains("red")){
            return hat;
        }
        tag.putInt("red", colorTag.getInt("red"));
        tag.putInt("blue", colorTag.getInt("blue"));
        tag.putInt("green", colorTag.getInt("green"));

        tag.putInt("red1", colorTag.getInt("red1"));
        tag.putInt("blue1", colorTag.getInt("blue1"));
        tag.putInt("green1", colorTag.getInt("green1"));

        hat.setTag(tag);
        return hat;
    }
}
