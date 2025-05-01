package net.Portality.createsprings.Items.advanced.hat;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Entities.ModEntities;
import net.Portality.createsprings.Entities.Packages.SusPackageEntity;
import net.Portality.createsprings.Items.CspringsArmorMaterials;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBaseRenderer;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringPoweredCore;
import net.Portality.createsprings.blocks.ModBlocks;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.simibubi.create.content.equipment.goggles.GogglesItem.addIsWearingPredicate;
import static com.simibubi.create.content.logistics.box.PackageItem.getPackageVelocity;

public class HatItem extends Item {
    public PackageStyles.PackageStyle style = PackageStyles.STYLES.get(0);

    private static final List<Predicate<Player>> IS_WEARING_PREDICATES = new ArrayList<>();
    static {
        addIsWearingPredicate(player -> ModItems.HAT.isIn(player.getItemBySlot(EquipmentSlot.HEAD)));
    }
    public HatItem(Properties properties) {
        super(properties.durability(500));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    public static void addIsWearingPredicate(Predicate<Player> predicate) {
        IS_WEARING_PREDICATES.add(predicate);
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
        return Optional.of(new HatSlotTooltipComponent(readStackFromNBT(stack)));
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
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        }
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

    public static class HatSlotTooltipComponent implements TooltipComponent {
        public final ItemStack stack;

        public HatSlotTooltipComponent(ItemStack stack) {
            this.stack = stack;
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
            GuiGraphics.renderFakeItem(component.stack, x, y + 1);
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
        return PackageEntity.fromDroppedItem(world, location, itemstack);
    }
}
