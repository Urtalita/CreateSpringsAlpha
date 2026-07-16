package com.Portality.createsprings.items;

import com.Portality.createsprings.items.SpringStufs.ExplosionСhamber.ChamberItem;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.BrokenPSEItem;
import com.Portality.createsprings.items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import com.Portality.createsprings.items.SpringStufs.SpringBase.SpringBase;
import com.Portality.createsprings.items.SpringStufs.SpringDrill.SpringDrill;
import com.Portality.createsprings.items.SpringStufs.SpringFan.SpringFan;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.SpringLauncher;
import com.Portality.createsprings.items.SpringStufs.SpringSaw.SpringSaw;
import com.Portality.createsprings.items.SpringStufs.SpringShowel.SpringShove;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardItem;
import com.Portality.createsprings.items.advanced.SusPackage.SusPackageItem;
import com.Portality.createsprings.items.advanced.hat.HatItem;
import com.Portality.createsprings.items.advanced.hat.HatModel;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import com.Portality.createsprings.CreateSprings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;

public class CSpringsItems {
    static {
        //CSPRINGS_REGISTRATE.setCreativeTab(CreateSprings.MAIN_TAB);
    }

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, CreateSprings.MODID);

    public static final ItemEntry<BouncyItem> SPRING_ALLOY = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_alloy", BouncyItem::new)
            .properties(p -> p.fireResistant())
            .register();

    public static final ItemEntry<BouncyItem> SPRING_ALLOY_NUGGET = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_alloy_nugget", BouncyItem::new)
            .properties(p -> p.fireResistant())
            .register();

    public static final ItemEntry<BouncyItem> SPRING_ALLOY_SHEET = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_alloy_sheet", BouncyItem::new)
            .properties(p -> p.fireResistant())
            .register();

    public static final ItemEntry<PunchcardItem> PUNCHCARD = CreateSprings.CSPRINGS_REGISTRATE
            .item("punchcard", PunchcardItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_PUNCHCARD = CreateSprings.CSPRINGS_REGISTRATE
            .item("incomplete_punchcard", SequencedAssemblyItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<SpringBase> SPRING_BASE = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_base", SpringBase::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SpringFan> SPRING_FAN = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_fan", SpringFan::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SpringDrill> SPRING_DRILL = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_drill", SpringDrill::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SpringSaw> SPRING_SAW = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_saw", SpringSaw::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SpringShove> SPRING_SHOVE = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_shove", SpringShove::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SpringLauncher> SPRING_LAUNCHER = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_launcher", SpringLauncher::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<ChamberItem> EXPLOSION_CHAMBER = CreateSprings.CSPRINGS_REGISTRATE
            .item("explosion_chamber", ChamberItem::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list


    public static final ItemEntry<PortativeSteamEngineItem> PORTATIVE_STEAM_ENGINE = CreateSprings.CSPRINGS_REGISTRATE
            .item("portative_steam_engine", PortativeSteamEngineItem::new)
            .model(AssetLookup.customGenericItemModel("_", "item"))
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_OBSIDIAN_PLATE = CreateSprings.CSPRINGS_REGISTRATE
            .item("unfinished_obsidian_plate", SequencedAssemblyItem::new)
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<BrokenPSEItem> BROKEN_PSE = CreateSprings.CSPRINGS_REGISTRATE
            .item("broken_portative_steam_engine", BrokenPSEItem::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModel("broken_portative_steam_engine"))
            .register();

    public static final DeferredHolder<Item, SusPackageItem> SUS_PACKAGE = ITEMS.register("sus_package",
            () -> new SusPackageItem(new Item.Properties()));

    public static final ItemEntry<HatItem> HAT = CreateSprings.CSPRINGS_REGISTRATE
            .item("hat", HatItem::new)
            .properties(p -> p.stacksTo(1))
            .onRegister(CreateRegistrate.itemModel(() -> HatModel::new))
            .model(AssetLookup.itemModel("hat"))
            .register();

    public static final DeferredHolder<Item, HitboxPackageItem> HITBOX_HAT = ITEMS.register("hitbox_hat",
            () -> new HitboxPackageItem(new Item.Properties(), new PackageStyles.PackageStyle("cardboard", 10, 7, 18f, false)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
