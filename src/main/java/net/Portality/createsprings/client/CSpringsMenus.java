package net.Portality.createsprings.client;

import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.cannon.SchematicannonScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.Portality.createsprings.CreateSprings;
import net.Portality.createsprings.menus.PortativeEngine.PortativeEngineScreen;
import net.Portality.createsprings.menus.PortativeEngine.PortativeSteamEngineMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CSpringsMenus {

    public static final MenuEntry<PortativeSteamEngineMenu> PSE = register("portative_steam_engine", PortativeSteamEngineMenu::new, () -> PortativeEngineScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C>
            register(String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return CreateSprings.CSPRINGS_REGISTRATE.menu(name, factory, screenFactory).register();
    }

    public static void register() {
    }
}
