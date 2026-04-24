package com.Portality.createsprings.mixins.banner;

import com.Portality.createsprings.CreateSprings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.function.Consumer;

@Mixin(CreativeModeTab.class)
public class CreativeTabMixin {
    @Shadow
    private Collection<ItemStack> displayItems;

    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @WrapMethod(method = "buildContents")
    private void createSprings$buildContents(final CreativeModeTab.ItemDisplayParameters parameters, final Operation<Void> original) {
        final CreativeModeTab self = (CreativeModeTab) (Object) this;
        if(self == CreateSprings.MAIN_TAB.get()) {
            final List<ItemStack> displayItems = new LinkedList<>();
            final Set<ItemStack> searchItems = new LinkedHashSet<>();
            create_Springs_1_21_1$processItems(displayItems::add, searchItems::add);
            this.displayItems = displayItems;
            this.displayItemsSearchTab = searchItems;
            return;
        }
        original.call(parameters);
    }

    @Unique
    private static void create_Springs_1_21_1$processItems(final Consumer<ItemStack> displayItems, final Consumer<ItemStack> searchItems) {
        // 1. Собираем предметы вашего мода.
        // Предположим, ваш регистратор называется CreateSprings.ITEMS
        List<ItemStack> itemsToAdd = new ArrayList<>();

        // ВАЖНО: Если вы используете DeferredRegister, достаем предметы так:
        CreateSprings.registrate().getAll(net.minecraft.core.registries.Registries.ITEM).forEach(entry -> {
            Item item = entry.get();
            // Проверяем, что предмет должен быть именно в этой вкладке
            // В Registrate 1.21.1 это обычно проверяется через параметры регистрации
            itemsToAdd.add(new ItemStack(item));
        });

        // 2. СДВИГ: Добавляем 9 пустых ячеек (первый ряд)
        for (int i = 0; i < 9; i++) {
            displayItems.accept(ItemStack.EMPTY);
        }

        // 3. ДОБАВЛЕНИЕ: Добавляем ваши предметы во вкладку и в поиск
        int count = 0;
        for (ItemStack stack : itemsToAdd) {
            // Мы используем копию, чтобы не испортить оригинал
            ItemStack finalStack = create_Springs_1_21_1$applyTransform(stack);

            displayItems.accept(finalStack);
            searchItems.accept(finalStack);
            count++;
        }

        // 4. ЗАВЕРШЕНИЕ: Добиваем последний ряд до конца, чтобы полоса прокрутки не дергалась
        int padding = 9 - (count % 9);
        if (padding < 9) {
            for (int i = 0; i < padding; i++) {
                displayItems.accept(ItemStack.EMPTY);
            }
        }
    }

    @Unique
    private static ItemStack create_Springs_1_21_1$applyTransform(final ItemStack item) {
        // for now to itemTransforms needed
        return item;
    }
}
