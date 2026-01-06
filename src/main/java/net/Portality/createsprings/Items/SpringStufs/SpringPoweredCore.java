package net.Portality.createsprings.Items.SpringStufs;

import net.Portality.createsprings.Items.SpringStufs.ExplosionСhamber.ExplosionChamberFuel;
import net.Portality.createsprings.config.ModConfigs;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardItem;
import net.Portality.createsprings.Items.SpringStufs.SpringLauncher.SpringLauncher;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.Portality.createsprings.CreateSprings.SPRING_TOOLS;

public class SpringPoweredCore {
    private final int springsMaxCount;
    private final Item[] allowedModifficators;

    public SpringPoweredCore(int springsMaxCount, Item[] allowedModifficators) {
        this.springsMaxCount = springsMaxCount;
        this.allowedModifficators = allowedModifficators;
    }

    public static void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {;
        CompoundTag tag = stack.getOrCreateTag();

        float stored = getAllStoredSum(getAllStored(tag));
        float capacity = ModConfigs.common().SPRING_CAPACITY.get() * tag.getInt("Springs_rn");
        tooltip.add(Component.translatable("createsprings.charge").append(": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(stored)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(capacity)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" ").append(Component.translatable("create.spring.su")).withStyle(ChatFormatting.GRAY))
        );

        if(!hasNoModifies(tag)){return;}

        tooltip.add(Component.translatable("tooltip.springstuf.createsprings.no_modifires").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC));
    }

    public static void checkAndAddModifier(ItemStack stack, Item item){
        if(!stack.getOrCreateTag().contains("contains")){
            CompoundTag tag = stack.getOrCreateTag();
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);

            CompoundTag contains = tag.getCompound("contains");

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
            }
            tag.put("contains", contains);
        }
    }

    public static Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(hasNoModifies(tag)){return Optional.empty();}
        return Optional.of(new SpringPoweredCore.SpringSlotTooltipComponent(
                tag.getInt("Springs_rn"),
                tag.getCompound("contains"),
                getAllStored(tag)));
    }

    public static boolean hasNoModifies(CompoundTag tag){
        boolean noModifies = tag.getCompound("contains").getAllKeys().isEmpty();
        boolean noSprings = tag.getInt("Springs_rn") == 0;
        return noSprings && noModifies;
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

    public static float getStoredSu(ItemStack stack){
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

    public static boolean addItemWithCount(Item item, ItemStack stack1, ItemStack stack2){
        CompoundTag tag = stack1.getOrCreateTag();
        if (stack2.getItem() == item){

            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId == null) return false;

            CompoundTag contains = tag.getCompound("contains");

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
                contains.putInt(itemId + "_count", stack2.getCount());
                stack2.setCount(0);
            }
            tag.put("contains", contains);

            return true;
        }
        return false;
    }

    public static boolean removeWithCount(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        CompoundTag tag = stack1.getOrCreateTag();
        if (stack2.isEmpty()) {
            CompoundTag contains = tag.getCompound("contains");
            String itemid = ForgeRegistries.ITEMS.getKey(item).toString();

            if (action == ClickAction.SECONDARY) {
                if (contains.getBoolean(itemid)){
                    ItemStack addstack = new ItemStack(item);
                    addstack.setCount(contains.getInt(itemid + "_count"));

                    player.getInventory().add(addstack);

                    contains.putBoolean(itemid, false);
                    contains.putInt(itemid + "_count", 0);
                    tag.put("contains", contains);

                    contains.remove(itemid);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeOne(ItemStack stack1){
        CompoundTag tag = stack1.getOrCreateTag();
        CompoundTag contains = tag.getCompound("contains");

        for(ExplosionChamberFuel fuel : ExplosionChamberFuel.values()){
            String itemid = ForgeRegistries.ITEMS.getKey(fuel.item).toString();
            if (contains.getBoolean(itemid)){
                int itemCount = contains.getInt(itemid + "_count");
                if(itemCount < 2){
                    contains.putBoolean(itemid, false);
                    contains.putInt(itemid + "_count", 0);
                    tag.put("contains", contains);

                    contains.remove(itemid);
                    return false;
                }

                contains.putInt(itemid + "_count", contains.getInt(itemid + "_count") - 1);
                tag.put("contains", contains);
                return true;
            }
        }
        return false;
    }

    public static boolean addStackedLogic(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
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

    public static boolean exceptions(CompoundTag tag){
        CompoundTag contains = tag.getCompound("contains");
        if (contains.getBoolean(SpringLauncher.BlockAmmo)){
            return false;
        }
        return true;
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess slotaccess) {
        CompoundTag tag = stack1.getOrCreateTag();
        int Springs_rn = tag.getInt("Springs_rn");

        float[] all = getAllStored(tag);
        ArrayList<Float> allSu = new ArrayList<>();
        for (float f : all){allSu.add(f);}

        if (stack2.getItem() == ModBlocks.SPRING.asItem()){
            if (springsMaxCount != Springs_rn && !tag.getBoolean("block") && exceptions(tag)){
                allSu.add(getStoredSu(stack2));
                Springs_rn++;

                tag.putInt("Springs_rn", Springs_rn);

                float[] newAll = new float[allSu.size()];
                for(int i = 0; i < newAll.length; i++){newAll[i] = allSu.get(i);}

                putAllStored(newAll, tag);
                stack2.shrink(1);
                return true;
            }
            return false;
        }

        if (stack2.isEmpty()){
            if(action == ClickAction.SECONDARY){
                if(detachSpring(tag, player)){return true;}
            }
        }

        for(int i = 0; i < allowedModifficators.length; i++){
            if(allowedModifficators[i] instanceof PunchcardItem punchcardItem){
                if(punchcardInOut(punchcardItem, stack1, stack2, action, player)){return true;}}

            if(addStackedLogic(allowedModifficators[i], stack1, stack2, action, player)){return true;}
        }
        return false;
    }

    private static boolean punchcardInOut(PunchcardItem item, ItemStack stack, ItemStack stackedOn, ClickAction action, Player player){
        CompoundTag tag = stack.getOrCreateTag();

        if(addItem(item, stack, stackedOn)){
            tag.put("punchcard", stackedOn.getOrCreateTag());
            return true;
        }

        if (stackedOn.isEmpty()) {
            CompoundTag contains = tag.getCompound("contains");
            String itemid = ForgeRegistries.ITEMS.getKey(item).toString();

            if (action == ClickAction.SECONDARY) {
                if (contains.getBoolean(itemid)){
                    ItemStack addstack = new ItemStack(item);
                    addstack.setTag(tag.getCompound("punchcard"));

                    player.getInventory().add(addstack);

                    contains.putBoolean(itemid, false);
                    tag.put("contains", contains);

                    contains.remove(itemid);
                    tag.remove("punchcard");
                    return true;
                }
                tag.remove("punchcard");
            }
        }
        return false;
    }

    public static float[] getAllStored(CompoundTag tag){
        int springs = tag.getInt("Springs_rn");
        float[] allSu = new float[springs];
        for(int i = 0; i < springs; i++){
            allSu[i] = tag.getFloat("Stored" + i);
        }
        return allSu;
    }

    public static float getAllStoredSum(float[] allSu){
        float sum = 0;
        for(float value : allSu){
            sum += value;
        }
        return sum;
    }

    public static float[] spreadSu(float[] allSu, float add){
        int springs = allSu.length;
        float capacity = ModConfigs.common().SPRING_CAPACITY.get();
        float maxTotal = springs * capacity;

        float targetTotal = Math.min(add, maxTotal);
        float currentTotal = getAllStoredSum(allSu);
        float delta = targetTotal - currentTotal;

        if(Math.abs(delta) < 1f) return allSu;
        if(delta > 0) {
            return distributeCharge(allSu, delta, capacity);
        } else {
            return removeCharge(allSu, -delta, capacity);
        }
    }

    private static float[] distributeCharge(float[] allSu, float toAdd, float capacity){
        int springs = allSu.length;
        float remaining = toAdd;

        for(int i = 0; i < springs && remaining > 0; i++){
            float space = capacity - allSu[i];
            if(space > 0) {
                float addAmount = Math.min(space, remaining / (springs - i));
                allSu[i] += addAmount;
                remaining -= addAmount;
            }
        }

        if(remaining > 0){
            float addPerSpring = remaining / springs;
            for(int i = 0; i < springs; i++){
                allSu[i] += addPerSpring;
            }
        }

        for(int i = 0; i < springs; i++){
            allSu[i] = Math.min(allSu[i], capacity);
        }

        return allSu;
    }

    private static float[] removeCharge(float[] allSu, float toRemove, float capacity){
        int springs = allSu.length;
        float remaining = toRemove;

        for(int i = 0; i < springs && remaining > 0; i++){
            if(allSu[i] > 0) {
                float removeAmount = Math.min(allSu[i], remaining / (springs - i));
                allSu[i] -= removeAmount;
                remaining -= removeAmount;
            }
        }

        if(remaining > 0){
            float removePerSpring = remaining / springs;
            for(int i = 0; i < springs; i++){
                allSu[i] = Math.max(0, allSu[i] - removePerSpring);
            }
        }

        for(int i = 0; i < springs; i++){
            allSu[i] = Math.max(allSu[i], 0);
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
        float[] allSu = getAllStored(tag);
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

        if(tag1.getFloat("Stored") >= ModConfigs.common().SPRING_CAPACITY.get()){
            tag1.putFloat("Stored", tag1.getInt("Stored") - ModConfigs.common().SPRING_CAPACITY.get());
            tag2.putFloat("Stored", tag2.getInt("Stored") + ModConfigs.common().SPRING_CAPACITY.get());
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
            paste.setTag(sourceTag);
        }

        return paste;
    }

    public static boolean checkItemInContains(CompoundTag tag, Item item){
        return tag.getCompound("contains").getBoolean(ForgeRegistries.ITEMS.getKey(item).toString());
    }

    public static boolean detachSpring(CompoundTag tag, Player player){
        int Springs_rn = tag.getInt("Springs_rn");
        float[] allSu = getAllStored(tag);

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
        return false;
    }
}
