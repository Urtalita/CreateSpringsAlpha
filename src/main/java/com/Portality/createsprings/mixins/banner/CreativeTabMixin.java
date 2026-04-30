package com.Portality.createsprings.mixins.banner;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.ModBlocks;
import com.Portality.createsprings.config.ModConfigs;
import com.Portality.createsprings.items.ModItems;
import com.Portality.createsprings.items.SpringStufs.ISpringPoweredTool;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.SpringLauncher;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
        List<ItemStack> itemsToAdd = new ArrayList<>();

        CreateSprings.registrate().getAll(net.minecraft.core.registries.Registries.ITEM).forEach(entry -> {
            Item item = entry.get();
            itemsToAdd.add(new ItemStack(item));
        });

        for (int i = 0; i < 9; i++) {
            displayItems.accept(ItemStack.EMPTY);
        }

        int count = 0;
        for (ItemStack stack : itemsToAdd) {
            ItemStack finalStack = create_Springs_1_21_1$applyTransform(stack, displayItems, searchItems);

            displayItems.accept(finalStack);
            searchItems.accept(finalStack);
            count++;
        }

        int padding = 9 - (count % 9);
        if (padding < 9) {
            for (int i = 0; i < padding; i++) {
                displayItems.accept(ItemStack.EMPTY);
            }
        }
    }

    @Unique
    private static ItemStack create_Springs_1_21_1$applyTransform(final ItemStack item, Consumer<ItemStack> displayItems, Consumer<ItemStack> searchItems) {
        if(item.getItem() == ModBlocks.SPRING.asItem()){
            if(item.getItem() == ModBlocks.SPRING.asItem()){
                ItemStack stack = CSpringsDataComponents.getChargedSpring();
                displayItems.accept(stack);
                searchItems.accept(stack);
            }
        }

        if(item.getItem() instanceof SpringLauncher tool){
            tool.getCore().attachSpring(item, CSpringsDataComponents.getChargedSpring());
            tool.getCore().attachSpring(item, CSpringsDataComponents.getChargedSpring());
        }

        return item;
    }
}
