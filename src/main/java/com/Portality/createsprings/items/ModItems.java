package com.Portality.createsprings.items;

import com.Portality.createsprings.items.SpringStufs.ExplosionСhamber.ChamberItem;
import com.Portality.createsprings.items.SpringStufs.SpringBase.SpringBase;
import com.Portality.createsprings.items.SpringStufs.SpringDrill.SpringDrill;
import com.Portality.createsprings.items.SpringStufs.SpringFan.SpringFan;
import com.Portality.createsprings.items.SpringStufs.SpringLauncher.SpringLauncher;
import com.Portality.createsprings.items.SpringStufs.SpringSaw.SpringSaw;
import com.Portality.createsprings.items.SpringStufs.SpringShowel.SpringShove;
import com.Portality.createsprings.items.advanced.Punchcard.PunchcardItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import com.Portality.createsprings.CreateSprings;
import net.neoforged.bus.api.IEventBus;

import static com.Portality.createsprings.CreateSprings.CSPRINGS_REGISTRATE;

public class ModItems {
    static {
        CSPRINGS_REGISTRATE.setCreativeTab(CreateSprings.MAIN_TAB);
    }

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



    public static void register(IEventBus eventBus) {

    }
}
