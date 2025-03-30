package net.Portality.createsprings.Items.advanced.SpringStufs;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.SpringLauncher;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.Portality.createsprings.CreateSprings.SPRING_TOOLS;

public class SpringPoweredCore {
    private final int springsMaxCount;

    public SpringPoweredCore(int springsMaxCount) {
        this.springsMaxCount = springsMaxCount;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {;
        float capacity = springsMaxCount * CreateSprings.SPRING_CAPACITY;
        CompoundTag tag = stack.getOrCreateTag();
        tooltip.add(Component.literal("su: ").withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal(String.valueOf(tag.getFloat("Stored")))).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(String.valueOf(capacity))).withStyle(ChatFormatting.GRAY));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.create.hold_for_details1").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("tooltip.create.hold_for_details2")));
        } else {
            tooltip.add(Component.translatable("tooltip.create.hold_for_details1").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.WHITE))
                    .append(Component.translatable("tooltip.create.hold_for_details2")));

            tooltip.add(Component.translatable("tooltip.springstuf.needsprings1").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(String.valueOf(springsMaxCount)).withStyle(ChatFormatting.YELLOW))
                    .append(Component.translatable("tooltip.springstuf.needsprings2").withStyle(ChatFormatting.GOLD)));

            appendHoverTextItemsCategory(tooltip, stack);
        }
    }

    private void appendHoverTextItemsCategory(List<Component> tooltip, ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");

        tooltip.add(Component.translatable("tooltip.springstuf.modifiers").withStyle(ChatFormatting.GRAY));

        for (String key : contains.getAllKeys()) {
            if (contains.getBoolean(key)){
                String modiferName = I18n.get(getItemFromName(key).getDescriptionId());

                tooltip.add(Component.literal(modiferName).withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal(" - ").withStyle(ChatFormatting.GOLD))
                        .append(Component.translatable("tooltip.springstuf." + key).withStyle(ChatFormatting.GOLD)));
            }
        }
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return Optional.of(new SpringPoweredCore.SpringSlotTooltipComponent(
                tag.getInt("Springs_rn"),
                tag.getCompound("contains"),
                tag.getFloat("Stored")));
    }

    public static class SpringSlotTooltipComponent implements TooltipComponent {
        public final int Springs;
        public final CompoundTag contains;
        public final float stored;

        public SpringSlotTooltipComponent(int springs, CompoundTag contains, float stored) {
            this.Springs = springs;
            this.contains = contains;
            this.stored = stored;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SpringSlotRenderer implements ClientTooltipComponent {
        private final SpringPoweredCore.SpringSlotTooltipComponent component;

        public SpringSlotRenderer(SpringPoweredCore.SpringSlotTooltipComponent component) {
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
            int moving = -getWidth(Font);
            CompoundTag contains = component.contains;

            for(int i = 0; i < component.Springs; i++){
                moving += getWidth(Font)+2;
                ItemStack SpringStack = new ItemStack(ModBlocks.SPRING.asItem());

                CompoundTag BlockEntityTag = new CompoundTag();
                BlockEntityTag.putFloat("Stored", component.stored / component.Springs);
                CompoundTag tag = SpringStack.getOrCreateTag();
                tag.put("BlockEntityTag", BlockEntityTag);

                GuiGraphics.renderFakeItem(SpringStack, x + moving, y + 1);
            }

            for (String key : contains.getAllKeys()) {
                if (contains.getBoolean(key)){
                    moving += getWidth(Font)+2;
                    ItemStack itemStack = new ItemStack(getItemFromName(key));
                    GuiGraphics.renderFakeItem(itemStack, x + moving, y + 1);
                }
            }
        }
    }

    public static Item getItemFromName(String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            return null;
        }

        ResourceLocation resLoc = ResourceLocation.tryParse(itemName);
        if (resLoc == null) {
            return null;
        }

        return ForgeRegistries.ITEMS.getValue(resLoc);
    }

    public float getStoredSu(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        float stored = 0;

        CompoundTag BlockEntityTag = tag.getCompound("BlockEntityTag");
        stored = BlockEntityTag.getFloat("Stored");

        return stored;
    }

    public boolean addItem(Item item, ItemStack stack1, ItemStack stack2){
        CompoundTag tag = stack1.getOrCreateTag();
        if (stack2.getItem() == item){

            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId == null) return false;

            CompoundTag contains = tag.getCompound("contains");

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
            }
            tag.put("contains", contains);

            stack2.shrink(1);
            return true;
        }
        return false;
    }

    public boolean removeItem(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        CompoundTag tag = stack1.getOrCreateTag();
        if (stack2.isEmpty()) {
            CompoundTag contains = tag.getCompound("contains");
            String itemid = ForgeRegistries.ITEMS.getKey(item).toString();

            if (action == ClickAction.SECONDARY) {
                if (contains.getBoolean(itemid)){
                    ItemStack addstack = new ItemStack(item);

                    player.getInventory().add(addstack);

                    contains.putBoolean(itemid, false);
                    tag.put("contains", contains);

                    contains.remove(itemid);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addStackedLogick(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        CompoundTag tag = stack1.getOrCreateTag();
        int Springs_rn = tag.getInt("Springs_rn");

        if(Springs_rn == 2 && item == ModBlocks.SPRING_ALLOY_BLOCK.get().asItem()){
            return false;
        }
        if(addItem(item, stack1, stack2)){
            return true;
        }
        if(removeItem(item, stack1, stack2, action, player)){
            return true;
        }

        return false;
    }

    private boolean exceptions(CompoundTag tag){
        CompoundTag contains = tag.getCompound("contains");
        if (contains.getBoolean(SpringLauncher.BlockAmmo)){
            return false;
        }
        return true;
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess slotaccess) {
        CompoundTag tag = stack1.getOrCreateTag();
        int Springs_rn = tag.getInt("Springs_rn");

        if (stack2.getItem() == ModBlocks.SPRING.asItem()){
            if (springsMaxCount != Springs_rn && !tag.getBoolean("block") && exceptions(tag)){
                float Stored = 0;
                Stored = tag.getFloat("Stored");
                float getsu = getStoredSu(stack2);
                Stored += getsu;

                Springs_rn++;

                tag.putInt("Springs_rn", Springs_rn);
                tag.putFloat("Stored", Stored);

                stack2.shrink(1);
                return true;
            } else {
                return false;
            }
        } else {
            if (stack2.isEmpty()){
                if(action == ClickAction.SECONDARY){
                    float Stored = 0;
                    Stored = tag.getFloat("Stored");
                    if (Springs_rn > 0){
                        float springSu = 0;

                        if (Stored >= (int) CreateSprings.SPRING_CAPACITY){
                            springSu = (int) CreateSprings.SPRING_CAPACITY;
                            Stored -= (int) CreateSprings.SPRING_CAPACITY;
                        } else {
                            springSu = Stored;
                            Stored = 0;
                        }

                        ItemStack spring = ModBlocks.SPRING.asStack();
                        CompoundTag SpTag = spring.getOrCreateTag();
                        CompoundTag SpBlTag = new CompoundTag();
                        SpBlTag.putFloat("Stored", springSu);
                        SpTag.put("BlockEntityTag", SpBlTag);

                        player.getInventory().add(spring);
                        Springs_rn--;
                        tag.putInt("Springs_rn", Springs_rn);
                        tag.putFloat("Stored", Stored);

                        return true;
                    }
                }
            }

            if(addStackedLogick(ModItems.PUNCHCARD.get(), stack1, stack2, action, player)){
                return true;
            }

            return false;
        }
    }

    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        ItemStack slotStack = slot.getItem();
        CompoundTag tag1 = stack.getOrCreateTag();
        CompoundTag tag2 = slotStack.getOrCreateTag();

        if(action == ClickAction.PRIMARY){
            for (Item tool : SPRING_TOOLS) {
                if (slotStack.getItem() == tool) {
                    return switchSu(tag1, tag2);
                }
            }
        } else {
            for (Item tool : SPRING_TOOLS) {
                if (slotStack.getItem() == tool) {
                    return switchSu(tag2, tag1);
                }
            }
        }

        return false;
    }

    private boolean switchSu(CompoundTag tag1, CompoundTag tag2){
        if(tag1.getInt("Springs_rn") == 0){return false;}

        if(tag2.getInt("Springs_rn") == springsMaxCount){return false;}

        tag2.putInt("Springs_rn", tag2.getInt("Springs_rn") + 1);
        tag1.putInt("Springs_rn", tag1.getInt("Springs_rn") - 1);

        if(tag1.getFloat("Stored") >= CreateSprings.SPRING_CAPACITY){
            tag1.putFloat("Stored", tag1.getInt("Stored") - CreateSprings.SPRING_CAPACITY);
            tag2.putFloat("Stored", tag2.getInt("Stored") + CreateSprings.SPRING_CAPACITY);
            return true;
        }
        tag2.putFloat("Stored", tag1.getFloat("Stored"));
        tag1.putFloat("Stored", 0);
        return true;
    }

    public void switchToolInHand(Player player, Slot slot, Item item, ItemStack stack){
        if (stack.isEmpty() || item == null) return;

        ItemStack paste = new ItemStack(item);
        CompoundTag sourceTag = stack.getTag();

        // Проверяем существование тега "contains"
        if (sourceTag != null && sourceTag.contains("contains", Tag.TAG_COMPOUND)) {
            CompoundTag containsTag = sourceTag.getCompound("contains");
            paste.getOrCreateTag().put("contains", containsTag);
            paste.getOrCreateTag().putInt("Springs_rn",sourceTag.getInt("Springs_rn"));
            paste.getOrCreateTag().putFloat("Stored",sourceTag.getFloat("Stored"));
            paste.getOrCreateTag().putFloat("Speed",sourceTag.getFloat("Speed"));
        }

        player.getInventory().setItem(slot.getSlotIndex(), paste);
    }
}
