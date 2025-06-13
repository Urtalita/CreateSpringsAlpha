package net.Portality.createsprings.Items.advanced.SpringStufs;

import net.Portality.createsprings.Config;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.SpringLauncher;
import net.Portality.createsprings.blocks.ModBlocks;
import net.Portality.createsprings.utill.Helpers.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.Portality.createsprings.CreateSprings.SPRING_TOOLS;

public class SpringPoweredCore {
    private final int springsMaxCount;

    public SpringPoweredCore(int springsMaxCount) {
        this.springsMaxCount = springsMaxCount;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {;
        CompoundTag tag = stack.getOrCreateTag();
        float capacity = tag.getInt("Springs_rn") * Config.spring_capacity;

        tooltip.add((Component.translatable("create.spring.saved").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(" ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(String.valueOf(getStoredSum(stack)))).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(capacity + " ")).withStyle(ChatFormatting.GRAY)
                .append(Component.translatable("create.spring.su").withStyle(ChatFormatting.DARK_GRAY)))
        ;

        if (!RenderHelper.checkForDetails(tooltip)) {

            tooltip.add(Component.translatable("tooltip.springstuf.needsprings1").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(String.valueOf(springsMaxCount)).withStyle(ChatFormatting.YELLOW))
                    .append(Component.translatable("tooltip.springstuf.needsprings2").withStyle(ChatFormatting.GOLD)));

            tooltip.add(Component.empty());
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

    public static Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return Optional.of(new SpringPoweredCore.SpringSlotTooltipComponent(
                tag.getInt("Springs_rn"),
                tag.getCompound("contains"),
                getAllStored(2, tag)));
    }

    public static class SpringSlotTooltipComponent implements TooltipComponent {
        public final int Springs;
        public final CompoundTag contains;
        public final float[] stored;

        public SpringSlotTooltipComponent(int springs, CompoundTag contains, float[] stored) {
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
                BlockEntityTag.putFloat("Stored", component.stored[i]);
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

    public static Item getItemFromContains(ItemStack stack, Item searchItem){
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.contains("contains")){return null;}
        CompoundTag contains = tag.getCompound("contains");

        if(!contains.contains(ForgeRegistries.ITEMS.getKey(searchItem).toString())){return null;}
        String itemid = ForgeRegistries.ITEMS.getKey(searchItem).toString();

        if (!contains.getBoolean(itemid)){return null;}

        contains.putBoolean(itemid, false);
        tag.put("contains", contains);
        contains.remove(itemid);

        return searchItem;
    }

    public float getStoredSu(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        float stored;

        CompoundTag BlockEntityTag = tag.getCompound("BlockEntityTag");
        stored = BlockEntityTag.getFloat("Stored");

        return stored;
    }

    public static boolean addItem(Item item, ItemStack stack1, ItemStack stack2){
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

    public static boolean removeItem(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
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
        float[] allSu = getAllStored(tag);

        if (stack2.getItem() == ModBlocks.SPRING.asItem()){
            if (springsMaxCount != Springs_rn && !tag.getBoolean("block") && exceptions(tag)){
                allSu[Springs_rn] = getStoredSu(stack2);

                Springs_rn++;

                tag.putInt("Springs_rn", Springs_rn);
                putAllStored(allSu, tag);

                stack2.shrink(1);
                return true;
            }
            return false;
        }

        if (stack2.isEmpty()){
            if(action == ClickAction.SECONDARY){
                if (Springs_rn > 0){
                    float springSu;

                    springSu = allSu[Springs_rn-1];
                    allSu[Springs_rn-1] = 0;

                    player.getInventory().add(putSuInSpring(springSu));

                    Springs_rn--;
                    tag.putInt("Springs_rn", Springs_rn);
                    putAllStored(allSu, tag);
                    return true;
                }
            }
        }

        if(addStackedLogick(ModItems.PUNCHCARD.get(), stack1, stack2, action, player)){return true;}
        if(addStackedLogick(Blocks.TRIPWIRE_HOOK.asItem(), stack1, stack2, action, player)){return true;}
        return false;
    }

    private float[] getAllStored(CompoundTag tag){
        return getAllStored(springsMaxCount, tag);
    }

    public static float[] getAllStored(int springs, CompoundTag tag){
        float[] allSu = new float[springs];
        for(int i = 0; i < springs; i++){
            allSu[i] = tag.getFloat("Stored" + i);
        }
        return allSu;
    }

    public static float getAllStoredSum(float[] allSu){
        float sum = 0;
        for(int i = 0; i < allSu.length; i++){
            sum += allSu[i];
        }
        return sum;
    }

    public static float[] spreadSu(float[] allSu, float add){
        float past = getAllStoredSum(allSu);
        float addSu = add - past;
        float addPerSpring = addSu / allSu.length;
        float ifNotEnoughSu = 0;

        for(int i = 0; i < allSu.length; i++){
            if((allSu[i] + addPerSpring) < 0){ifNotEnoughSu += addPerSpring; allSu[i] = 0; continue;}
            if((allSu[i] + addPerSpring) > Config.spring_capacity){ifNotEnoughSu += addPerSpring; allSu[i] = 0; continue;}

            allSu[i] += addPerSpring;
            if(ifNotEnoughSu != 0){allSu[i] += ifNotEnoughSu; ifNotEnoughSu = 0;}
        }
        return allSu;
    }

    public static void putAllStored(float[] allSu, CompoundTag tag){
        for(int i = 0; i < allSu.length; i++){
            tag.putFloat("Stored" + i, allSu[i]);
        }
    }

    public static void putAllPrevStored(float[] allSu, CompoundTag tag){
        for(int i = 0; i < allSu.length; i++){
            tag.putFloat("PrevStored" + i, allSu[i]);
        }
    }

    public static float[] cleanStored(float[] allSu){
        for(int i = 0; i < allSu.length; i++){
            allSu[i] = 0;
        }
        return allSu;
    }

    public static float getStoredSum(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        float[] allSu = getAllStored(2, tag);
        float sum = 0;

        for(int i = 0; i < allSu.length; i++){
            sum += allSu[i];
        }
        return sum;
    }

    public static ItemStack putSuInSpring(float su){
        ItemStack spring = ModBlocks.SPRING.asStack();
        CompoundTag SpTag = spring.getOrCreateTag();
        CompoundTag SpBlTag = new CompoundTag();
        SpBlTag.putFloat("Stored", su);
        SpTag.put("BlockEntityTag", SpBlTag);

        return spring;
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

        if(tag1.getFloat("Stored") >= Config.spring_capacity){
            tag1.putFloat("Stored", tag1.getInt("Stored") - Config.spring_capacity);
            tag2.putFloat("Stored", tag2.getInt("Stored") + Config.spring_capacity);
            return true;
        }

        putAllStored(getAllStored(tag1), tag2);
        putAllStored(cleanStored(getAllStored(tag1)), tag1);
        return true;
    }

    public void switchTagInHand(Player player, Slot slot, Item item, ItemStack stack){
        ItemStack paste = switchTagInHand(player, item, stack, springsMaxCount);
        player.getInventory().setItem(slot.getSlotIndex(), paste);
    }

    public static void switchTagInHandByHand(Player player, InteractionHand hand, Item item, ItemStack stack, int springs){
        ItemStack paste = switchTagInHand(player, item, stack, springs);
        player.setItemInHand(hand, paste);
    }

    public static ItemStack switchTagInHand(Player player, Item item, ItemStack stack, int springs){
        if (stack.isEmpty() || item == null) return ItemStack.EMPTY;

        ItemStack paste = new ItemStack(item);
        CompoundTag sourceTag = stack.getTag();

        // Проверяем существование тега "contains"
        if (sourceTag != null && sourceTag.contains("contains", Tag.TAG_COMPOUND)) {
            CompoundTag containsTag = sourceTag.getCompound("contains");
            paste.getOrCreateTag().put("contains", containsTag);
            paste.getOrCreateTag().putInt("Springs_rn",sourceTag.getInt("Springs_rn"));

            putAllStored(getAllStored(springs ,sourceTag), paste.getOrCreateTag());

            paste.getOrCreateTag().putFloat("Speed",sourceTag.getFloat("Speed"));
        }

        return paste;
    }
}
