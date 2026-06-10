package com.Portality.createsprings.mixins.banner;

import com.Portality.createsprings.CreateSprings;
import com.Portality.createsprings.blocks.CSpringsBlocks;
import com.Portality.createsprings.items.CSpringsItems;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.SpringLauncher;
import com.Portality.createsprings.server.CSpringsDataComponents;
import com.Portality.createsprings.server.fluid.CSpringsFluids;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
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

        itemsToAdd = create_Springs_1_21_1$getOrdering();

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
    private static List<ItemStack> create_Springs_1_21_1$getOrdering(){
        ArrayList<ItemStack> output = new ArrayList<>();

        output.add(CSpringsBlocks.UNFINISHED_SPRING.asStack());
        output.add(CSpringsBlocks.SPRING.asStack());
        output.add(CSpringsDataComponents.getChargedSpring());
        output.add(CSpringsBlocks.SPRING_CATAPULT.asStack());
        output.add(CSpringsBlocks.LARGE_SPRING_COIL.asStack());
        output.add(new ItemStack(Blocks.TRIPWIRE_HOOK));

        output.add(CSpringsBlocks.FRICTION_WELDER.asStack());
        output.add(CSpringsBlocks.KINETIC_INTERFACE.asStack());
        output.add(CSpringsBlocks.ANALOG_TOGGLE_LATCH.asStack());
        output.add(CSpringsBlocks.ANDESITE_MOLD.asStack());
        output.add(CSpringsBlocks.FILLED_ANDESITE_MOLD.asStack());
        output.add(CSpringsBlocks.OBSIDIAN_PLATE.asStack());
        output.add(CSpringsBlocks.OBSIDIAN_SLAB.asStack());
        output.add(CSpringsBlocks.WEATHERED_IRON.asStack());

        output.add(CSpringsBlocks.SPRING_ALLOY_CASING.asStack());
        output.add(CSpringsBlocks.SPRING_ALLOY_BLOCK.asStack());
        output.add(CSpringsBlocks.INDUSTRIAL_SPRING_ALLOY.asStack());
        output.add(CSpringsBlocks.CUT_SPRING_ALLOY.asStack());
        output.add(CSpringsItems.SPRING_ALLOY.asStack());
        output.add(CSpringsItems.SPRING_ALLOY_SHEET.asStack());
        output.add(CSpringsItems.SPRING_ALLOY_NUGGET.asStack());
        output.add(CSpringsFluids.SPRING_ALLOY.getBucket().get().getDefaultInstance());

        output.add(CSpringsItems.SPRING_BASE.asStack());
        output.add(CSpringsItems.SPRING_DRILL.asStack());
        output.add(CSpringsItems.SPRING_SAW.asStack());
        output.add(CSpringsItems.SPRING_SHOVE.asStack());
        output.add(CSpringsItems.SPRING_FAN.asStack());
        output.add(CSpringsItems.EXPLOSION_CHAMBER.asStack());

        ItemStack chargedLauncher = new ItemStack(CSpringsItems.SPRING_LAUNCHER.get());
        if(chargedLauncher.getItem() instanceof SpringLauncher launcher){
            launcher.getCore().attachSpring(chargedLauncher, CSpringsDataComponents.getChargedSpring());
            launcher.getCore().attachSpring(chargedLauncher, CSpringsDataComponents.getChargedSpring());
        }
        output.add(chargedLauncher);

        output.add(CSpringsDataComponents.getChargedSusPackage());
        output.add(CSpringsItems.PUNCHCARD.asStack());
        output.add(CSpringsItems.PORTATIVE_STEAM_ENGINE.asStack());
        output.add(CSpringsItems.BROKEN_PSE.asStack());
        output.add(CSpringsItems.HAT.asStack());

        return output;
    }

    @Unique
    private static ItemStack create_Springs_1_21_1$applyTransform(final ItemStack item, Consumer<ItemStack> displayItems, Consumer<ItemStack> searchItems) {
        return item;
    }
}
