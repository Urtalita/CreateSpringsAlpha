package com.Portality.createsprings.items.SpringStufs;

import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.items.SpringStufs.ExplosionСhamber.ExplosionChamberFuel;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.SpringLauncher;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardItem;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.Portality.createsprings.CreateSprings.SPRING_TOOLS;
import static com.Portality.createsprings.items.advanced.Spring.SpringItem.getStoredSu;

public class SpringPoweredCore {
    private final int springsMaxCount;
    private final Supplier<Item>[] allowedModifficators;

    public SpringPoweredCore(int springsMaxCount, Supplier<Item>[] allowedModifficators) {
        this.springsMaxCount = springsMaxCount;
        this.allowedModifficators = allowedModifficators;
    }

    public static int getSprings(ItemStack stack){
        if(stack.has(CSpringsDataComponents.SPRING_AMOUNT)) return stack.get(CSpringsDataComponents.SPRING_AMOUNT);
        return 0;
    }

    public static CompoundTag getContent(ItemStack stack){
        if(stack.has(CSpringsDataComponents.MODIFIERS)) return stack.get(CSpringsDataComponents.MODIFIERS);
        return new CompoundTag();
    }

    public static void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {;
        float stored = getAllStoredSum(getAllStored(stack));
        int springs = 0;
        if(stack.has(CSpringsDataComponents.SPRING_AMOUNT)) springs = stack.get(CSpringsDataComponents.SPRING_AMOUNT);
        float capacity = ModConfigs.common().SPRING_CAPACITY.get() * springs;
        tooltip.add(Component.translatable("createsprings.charge").append(": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(stored)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(capacity)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" ").append(Component.translatable("create.spring.su")).withStyle(ChatFormatting.GRAY))
        );

        if(!hasNoModifies(stack)){return;}

        tooltip.add(Component.translatable("tooltip.springstuf.createsprings.no_modifires").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC));
    }

    public static void checkAndAddModifier(ItemStack stack, Item item){
        if(!stack.has(CSpringsDataComponents.MODIFIERS)){
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);

            CompoundTag contains = new CompoundTag();

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
            }
            stack.set(CSpringsDataComponents.MODIFIERS, contains);
        }
    }

    public static Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if(hasNoModifies(stack)){return Optional.empty();}
        return Optional.of(new SpringSlotTooltipComponent(
                getSprings(stack),
                getContent(stack),
                getAllStored(stack)));
    }

    public static boolean hasNoModifies(ItemStack stack){
        boolean noModifies = getContent(stack).getAllKeys().isEmpty();
        boolean noSprings = getSprings(stack) == 0;
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
        private final SpringSlotTooltipComponent component;

        public SpringSlotRenderer(SpringSlotTooltipComponent component) {
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
                ItemStack SpringStack = new ItemStack(CSpringsBlocks.SPRING.asItem());
                int finalI = i;

                SpringStack.set(DataComponents.BLOCK_ENTITY_DATA,
                        CustomData.EMPTY.update(tag -> {
                                    tag.putFloat("Stored", component.stored[finalI]);
                                    tag.putString("id", "createsprings:spring");
                                }
                        ));

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

        return BuiltInRegistries.ITEM.get(resLoc);
    }

    public static Item getItemFromContains(ItemStack stack, Item searchItem){
        if(!stack.has(CSpringsDataComponents.MODIFIERS)){return null;}
        CompoundTag contains = stack.get(CSpringsDataComponents.MODIFIERS);

        if(!contains.contains(BuiltInRegistries.ITEM.getKey(searchItem).toString())){return null;}
        String itemid = BuiltInRegistries.ITEM.getKey(searchItem).toString();

        if (!contains.getBoolean(itemid)){return null;}

        contains.putBoolean(itemid, false);

        stack.set(CSpringsDataComponents.MODIFIERS, contains);

        return searchItem;
    }

    public static boolean addItem(Item item, ItemStack stack1, ItemStack stack2){
        if (stack2.getItem() == item){

            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (itemId == null) return false;

            CompoundTag contains = getContent(stack1);

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
            }
            stack1.set(CSpringsDataComponents.MODIFIERS, contains);

            stack2.shrink(1);
            return true;
        }
        return false;
    }

    public static boolean removeItem(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        if (stack2.isEmpty()) {
            CompoundTag contains = getContent(stack1);
            String itemid = BuiltInRegistries.ITEM.getKey(item).toString();

            if (action == ClickAction.SECONDARY) {
                if (contains.getBoolean(itemid)){
                    ItemStack addstack = new ItemStack(item);

                    player.getInventory().add(addstack);

                    contains.putBoolean(itemid, false);
                    stack1.set(CSpringsDataComponents.MODIFIERS, contains);

                    contains.remove(itemid);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean addItemWithCount(Item item, ItemStack stack1, ItemStack stack2){
        if (stack2.getItem() == item){

            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (itemId == null) return false;

            CompoundTag contains = getContent(stack1);

            if (!contains.getBoolean(itemId.toString())){
                contains.putBoolean(itemId.toString(), true);
                int count = stack2.getCount();
                contains.putInt(itemId + "_count", count);
                stack2.setCount(0);
            }
            stack1.set(CSpringsDataComponents.MODIFIERS, contains);

            return true;
        }
        return false;
    }

    public static boolean removeWithCount(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        if (stack2.isEmpty()) {
            CompoundTag contains = getContent(stack1);
            String itemid = BuiltInRegistries.ITEM.getKey(item).toString();

            if (action == ClickAction.SECONDARY) {
                if (contains.getBoolean(itemid)){
                    ItemStack addstack = new ItemStack(item);
                    addstack.setCount(contains.getInt(itemid + "_count"));

                    player.getInventory().add(addstack);

                    contains.putBoolean(itemid, false);
                    contains.putInt(itemid + "_count", 0);
                    stack1.set(CSpringsDataComponents.MODIFIERS, contains);

                    contains.remove(itemid);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeOne(ItemStack stack1){
        CompoundTag contains = getContent(stack1);

        for(ExplosionChamberFuel fuel : ExplosionChamberFuel.values()){
            String itemid = BuiltInRegistries.ITEM.getKey(fuel.item).toString();
            if (contains.getBoolean(itemid)){
                int itemCount = contains.getInt(itemid + "_count");
                if(itemCount < 2){
                    contains.putBoolean(itemid, false);
                    contains.putInt(itemid + "_count", 0);
                    stack1.set(CSpringsDataComponents.MODIFIERS, contains);

                    contains.remove(itemid);
                    return false;
                }

                contains.putInt(itemid + "_count", contains.getInt(itemid + "_count") - 1);
                stack1.set(CSpringsDataComponents.MODIFIERS, contains);
                return true;
            }
        }
        return false;
    }

    public static boolean addStackedLogic(Item item, ItemStack stack1, ItemStack stack2, ClickAction action, Player player){
        int Springs_rn = getSprings(stack1);

        if(Springs_rn == 2 && item == CSpringsBlocks.SPRING_ALLOY_BLOCK.get().asItem()){
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

    public static boolean exceptions(ItemStack stack){

        CompoundTag contains = getContent(stack);
        if (contains.getBoolean(SpringLauncher.BlockAmmo)){
            return false;
        }

        return true;
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack1, ItemStack stack2, Slot slot, ClickAction action, Player player, SlotAccess slotaccess) {
        float[] all = getAllStored(stack1);
        ArrayList<Float> allSu = new ArrayList<>();
        for (float f : all){allSu.add(f);}

        if (stack2.getItem() == CSpringsBlocks.SPRING.asItem()){
            return attachSpring(stack1, stack2);
        }

        if (stack2.isEmpty()){
            if(action == ClickAction.SECONDARY){
                if(detachSpring(stack1, player)){return true;}
            }
        }

        for(int i = 0; i < allowedModifficators.length; i++){
            Object entry = allowedModifficators[i].get();
            if (entry instanceof ItemEntry<?> itemEntry) {
                Item item = itemEntry.get(); // Извлекаем реальный Minecraft Item

                if (item instanceof PunchcardItem punchcardItem) {
                    if (punchcardInOut(punchcardItem, stack1, stack2, action, player)) {
                        return true;
                    }
                }

                if (addStackedLogic(item, stack1, stack2, action, player)) {
                    return true;
                }
            } else {
                if(allowedModifficators[i].get() instanceof PunchcardItem punchcardItem){
                    if(punchcardInOut(punchcardItem, stack1, stack2, action, player)){return true;}}

                if(addStackedLogic(allowedModifficators[i].get(), stack1, stack2, action, player)){return true;}
            }
        }
        return false;
    }

    public boolean attachSpring(ItemStack stack, ItemStack spring){
        float[] all = getAllStored(stack);
        ArrayList<Float> allSu = new ArrayList<>();
        for (float f : all){allSu.add(f);}

        int Springs_rn = getSprings(stack);
        if (springsMaxCount != Springs_rn){
            allSu.add(getStoredSu(spring));
            Springs_rn++;

            stack.set(CSpringsDataComponents.SPRING_AMOUNT, Springs_rn);

            float[] newAll = new float[allSu.size()];
            for(int i = 0; i < newAll.length; i++){newAll[i] = allSu.get(i);}

            putAllStored(newAll, stack);
            spring.shrink(1);
            return true;
        }
        return false;
    }

    private static boolean punchcardInOut(PunchcardItem item, ItemStack stackedOn, ItemStack stack, ClickAction action, Player player){
        CompoundTag tag = stack.getOrDefault(CSpringsDataComponents.PUNCHCARD, new CompoundTag());
        if(addItem(item, stackedOn, stack)){
            stackedOn.set(CSpringsDataComponents.PUNCHCARD, tag);
            return true;
        }

        if (stack.isEmpty()) {
            CompoundTag contains = getContent(stackedOn);
            String itemid = BuiltInRegistries.ITEM.getKey(item).toString();

            if (action == ClickAction.SECONDARY) {
                if (contains.getBoolean(itemid)){
                    ItemStack addstack = CSpringsDataComponents.punchcardFromTag(stackedOn.getOrDefault(CSpringsDataComponents.PUNCHCARD, new CompoundTag()), player.level());

                    player.getInventory().add(addstack);

                    contains.putBoolean(itemid, false);
                    contains.remove(itemid);
                    stackedOn.set(CSpringsDataComponents.MODIFIERS, contains);
                    stackedOn.remove(CSpringsDataComponents.PUNCHCARD);
                    return true;
                }
            }
        }
        return false;
    }

    public static float[] getAllStored(ItemStack stack){
        int springs = getSprings(stack);
        float[] allSu = new float[springs];
        List<Float> list = stack.get(CSpringsDataComponents.STORED_LIST);

        if(list != null && list.size() >= springs){
            for(int i = 0; i < springs; i++){
                allSu[i] = list.get(i);
            }
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

    public static void putAllStored(float[] allSu, ItemStack stack){
        List<Float> list = new ArrayList<Float>();
        for(float f : allSu){
            list.add(f);
        }
        stack.set(CSpringsDataComponents.STORED_LIST, list);
    }

    /*
    public static void putAllPrevStored(float[] allSu, CompoundTag tag){
        for(int i = 0; i < allSu.length; i++){
            tag.putFloat("PrevStored" + i, allSu[i]);
        }
    }

     */

    public static float[] cleanStored(float[] allSu){
        for(int i = 0; i < allSu.length; i++){
            allSu[i] = 0;
        }
        return allSu;
    }

    public static float getStoredSum(ItemStack stack){
        float[] allSu = getAllStored(stack);
        float sum = 0;

        for(int i = 0; i < allSu.length; i++){
            sum += allSu[i];
        }
        return sum;
    }

    public static ItemStack putSuInSpring(float su){
        ItemStack spring = CSpringsBlocks.SPRING.asStack();
        spring.set(DataComponents.BLOCK_ENTITY_DATA,
                CustomData.EMPTY.update(tag -> {
                            tag.putFloat("Stored", su);
                            tag.putString("id", "createsprings:spring");
                        }));

        return spring;
    }

    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        ItemStack slotStack = slot.getItem();
        if(action == ClickAction.PRIMARY){
            for (Item tool : SPRING_TOOLS) {
                if (slotStack.getItem() == tool) {
                    return switchSu(stack, slotStack);
                }
            }
        } else {
            for (Item tool : SPRING_TOOLS) {
                if (slotStack.getItem() == tool) {
                    return switchSu(stack, slotStack);
                }
            }
        }

        return false;
    }

    private boolean switchSu(ItemStack stack1, ItemStack stack2) {
        /*
        int s1Count = stack1.getOrDefault(CSpringsDataComponents.SPRING_AMOUNT, 0);
        int s2Count = stack2.getOrDefault(CSpringsDataComponents.SPRING_AMOUNT, 0);

        // Проверяем: есть ли что переносить и влезет ли это во второй предмет
        if (s1Count <= 0 || (s1Count + s2Count) > springsMaxCount) {
            return false;
        }

        // 1. Сначала извлекаем данные, пока stack1 еще "цел"
        var dataFrom1 = getAllStored(stack1);

        // 2. ВАЖНО: Сначала расширяем "емкость" stack2,
        // чтобы putAllStored не упал при попытке записи во второй слот
        stack2.set(CSpringsDataComponents.SPRING_AMOUNT, s1Count + s2Count);

        // 3. Теперь переносим заряд в расширенный стек
        putAllStored(dataFrom1, stack2);

        // 4. Очищаем исходный стек: сначала заряд, потом количество
        putAllStored(cleanStored(dataFrom1), stack1);
        stack1.set(CSpringsDataComponents.SPRING_AMOUNT, 0);

        return true;

         */
        return false;
    }




    public void switchTagInHand(Player player, Slot slot, Item item, ItemStack stack){
        ItemStack paste = switchTagInHand(player, item, stack, springsMaxCount);
        player.getInventory().setItem(slot.getSlotIndex(), paste);
    }

    public static void switchTagInHandByHand(Player player, InteractionHand hand, Item item, ItemStack stack, int springs){
        ItemStack paste = switchTagInHand(player, item, stack, springs);
        player.setItemInHand(hand, paste);
    }


    public static ItemStack switchTagInHand(Player player, Item item, ItemStack stack, int springs) {
        if (stack.isEmpty() || item == null) return ItemStack.EMPTY;
        ItemStack paste = new ItemStack(item);

        copyComponent(stack, paste, CSpringsDataComponents.MODIFIERS);
        copyComponent(stack, paste, CSpringsDataComponents.PUNCHCARD);

        return paste;
    }

    public static void copyComponent(ItemStack stack, ItemStack paste, DataComponentType component){
        if (stack.has(component)) {
            var data = stack.get(component);
            paste.set(component, data);
        }
    }

    public static boolean checkItemInContains(CompoundTag tag, Item item){
        return tag.getCompound("contains").getBoolean(BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public static boolean detachSpring(ItemStack stack, Player player){
        int Springs_rn = getSprings(stack);
        float[] allSu = getAllStored(stack);

        if (Springs_rn > 0){
            List<Float> storedList = new ArrayList<>();
            for (float f : allSu) {
                storedList.add(f);
            }

            float springSu = storedList.remove(storedList.size() - 1);

            player.getInventory().add(putSuInSpring(springSu));

            Springs_rn--;
            stack.set(CSpringsDataComponents.SPRING_AMOUNT, Springs_rn);

            float[] newAllSu = new float[storedList.size()];
            for (int i = 0; i < storedList.size(); i++) {
                newAllSu[i] = storedList.get(i);
            }

            putAllStored(newAllSu, stack);
            return true;
        }
        return false;
    }
}
