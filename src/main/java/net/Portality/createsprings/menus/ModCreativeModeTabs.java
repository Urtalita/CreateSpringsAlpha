package net.Portality.createsprings.menus;

import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.ModItems;
import net.Portality.createsprings.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateSprings.MODID);

    public static final RegistryObject<CreativeModeTab> CREATE_SPRINGS_MAIN_TAB = CREATIVE_MODE_TAB.register("create_springs_main_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SPRING.get()))
                    .title(Component.translatable("creativetab.create_springs_main_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        ItemStack spring = ModBlocks.SPRING.asStack();
                        CompoundTag SpTag = spring.getOrCreateTag();
                        CompoundTag SpBlTag = new CompoundTag();
                        SpBlTag.putFloat("Stored", CreateSprings.SPRING_CAPACITY);
                        SpTag.put("BlockEntityTag", SpBlTag);

                        pOutput.accept(ModItems.SPRING_ALLOY.get());
                        pOutput.accept(ModItems.SPRING_ALLOY_SHEET.get());
                        pOutput.accept(ModItems.SPRING_ALLOY_NUGGET.get());
                        pOutput.accept(ModItems.PUNCHCARD.get());

                        pOutput.accept(ModBlocks.OBSIDIAN_SLAB.get());
                        pOutput.accept(ModBlocks.OBSIDIAN_PLATE.get());
                        pOutput.accept(ModBlocks.SPRING_ALLOY_BLOCK.get());
                        pOutput.accept(ModBlocks.UNFINISHED_SPRING.get());
                        pOutput.accept(ModBlocks.SPRING.get());
                        pOutput.accept(spring);
                        pOutput.accept(ModBlocks.FRICTION_WELDER.get());
                        pOutput.accept(ModBlocks.ANDESITE_MOLD.get());
                        pOutput.accept(ModBlocks.FILLED_ANDESITE_MOLD.get());

                        pOutput.accept(ModItems.SPRING_ALLOY_BUCKET.get());

                        pOutput.accept(ModItems.SPRING_LAUNCHER.get());
                        pOutput.accept(ModItems.SPRING_DRILL.get());
                        pOutput.accept(ModItems.SPRING_BASE.get());
                        pOutput.accept(ModItems.SPRING_SAW.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
