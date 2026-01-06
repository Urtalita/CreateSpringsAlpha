package net.Portality.createsprings.Items;

import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.Items.advanced.Punchcard.PunchcardItem;
import net.Portality.createsprings.Items.SpringStufs.ExplosionСhamber.ChamberItem;
import net.Portality.createsprings.Items.SpringStufs.PortativeSteamEngine.PortativeSteamEngineItem;
import net.Portality.createsprings.Items.SpringStufs.SpringBase.SpringBase;
import net.Portality.createsprings.Items.SpringStufs.SpringDrill.SpringDrill;
import net.Portality.createsprings.Items.SpringStufs.SpringFan.SpringFan;
import net.Portality.createsprings.Items.SpringStufs.SpringLauncher.SpringLauncher;
import net.Portality.createsprings.Items.SpringStufs.SpringSaw.SpringSaw;
import net.Portality.createsprings.Items.SpringStufs.SpringShowel.SpringShove;
import net.Portality.createsprings.Items.advanced.SusPackage.SusPackageItem;
import net.Portality.createsprings.Items.advanced.hat.HatItem;
import net.Portality.createsprings.Items.advanced.hat.HatModel;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CreateSprings.MODID);

    public static final ItemEntry<Item> SPRING_ALLOY = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_alloy", Item::new)
            .properties(p -> p.fireResistant())
            .register();

    public static final ItemEntry<Item> SPRING_ALLOY_NUGGET = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_alloy_nugget", Item::new)
            .properties(p -> p.fireResistant())
            .register();

    public static final ItemEntry<Item> SPRING_ALLOY_SHEET = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_alloy_sheet", Item::new)
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

    public static final ItemEntry<SpringDrill> SPRING_DRILL = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_drill", SpringDrill::new)
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModelWithPartials())
            .register();  // add to spring tools list

    public static final ItemEntry<SpringLauncher> SPRING_LAUNCHER = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_launcher", SpringLauncher::new)
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

    public static final ItemEntry<SpringFan> SPRING_FAN = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_fan", SpringFan::new)
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

    public static final ItemEntry<ArmorItem> BROKEN_PSE = CreateSprings.CSPRINGS_REGISTRATE
            .item("broken_portative_steam_engine", p -> new ArmorItem(CspringsArmorMaterials.HAT, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)))
            .properties(p -> p.stacksTo(1))
            .model(AssetLookup.itemModel("broken_portative_steam_engine"))
            .register();

    public static final ItemEntry<Item> SPRING_PROJECTILE_ITEM = CreateSprings.CSPRINGS_REGISTRATE
            .item("spring_projectile", Item::new)
            .model(AssetLookup.itemModel("spring_projectile"))
            .register();

    public static final RegistryObject<Item> SUS_PACKAGE = ITEMS.register("sus_package",
            () -> new SusPackageItem(new Item.Properties()));

    public static final ItemEntry<HatItem> HAT = CreateSprings.CSPRINGS_REGISTRATE
            .item("hat", HatItem::new)
            .properties(p -> p.stacksTo(1))
            .onRegister(CreateRegistrate.itemModel(() -> HatModel::new))
            .model(AssetLookup.itemModel("hat"))
            .register();

    public static final RegistryObject<HitboxPackageItem> HITBOX_HAT = ITEMS.register("hitbox_hat",
            () -> new HitboxPackageItem(new Item.Properties(), new PackageStyles.PackageStyle("cardboard", 10, 7, 18f, false)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
