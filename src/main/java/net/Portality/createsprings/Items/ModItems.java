package net.Portality.createsprings.Items;

import com.mojang.datafixers.types.templates.List;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardItem;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringBase.SpringBase;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringDrill.SpringDrill;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringLauncher.SpringLauncher;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringSaw.SpringSaw;
import net.Portality.createsprings.Items.advanced.SpringStufs.SpringShowel.SpringShove;
import net.Portality.createsprings.Items.advanced.SusPackage.SusPackageItem;
import net.Portality.createsprings.fluid.ModFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CreateSprings.MODID);

    public static final RegistryObject<Item> SPRING_ALLOY = ITEMS.register("spring_alloy",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SPRING_ALLOY_NUGGET = ITEMS.register("spring_alloy_nugget",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SPRING_ALLOY_SHEET = ITEMS.register("spring_alloy_sheet",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> PUNCHCARD = ITEMS.register("punchcard",
            () -> new PunchcardItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPRING_BASE = ITEMS.register("spring_base",
            () -> new SpringBase(new Item.Properties().stacksTo(1))); // add to spring tools list

    public static final RegistryObject<Item> SPRING_DRILL = ITEMS.register("spring_drill",
            () -> new SpringDrill(new Item.Properties().stacksTo(1))); // add to spring tools list

    public static final RegistryObject<Item> SPRING_LAUNCHER = ITEMS.register("spring_launcher",
            () -> new SpringLauncher(new Item.Properties().stacksTo(1))); // add to spring tools list

    public static final RegistryObject<Item> SPRING_SAW = ITEMS.register("spring_saw",
            () -> new SpringSaw(new Item.Properties().stacksTo(1))); // add to spring tools list

    public static final RegistryObject<Item> SPRING_SHOVE = ITEMS.register("spring_shove",
            () -> new SpringShove(new Item.Properties().stacksTo(1))); // add to spring tools list

    public static final RegistryObject<Item> SPRING_PROJECTILE_ITEM = ITEMS.register("spring_projectile",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SUS_PACKAGE = ITEMS.register("sus_package",
            () -> new SusPackageItem(new Item.Properties(), PackageStyles.STYLES.get(0)));

    public static final RegistryObject<Item> SPRING_ALLOY_BUCKET = ITEMS.register(
            "spring_alloy_bucket",
            () -> new BucketItem(
                    ModFluids.SOURCE,
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)
            )
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
