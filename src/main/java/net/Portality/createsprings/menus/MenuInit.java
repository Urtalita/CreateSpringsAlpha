package net.Portality.createsprings.menus;

import net.Portality.createsprings.menus.Punchcard.PunchcardMenu;
import net.Portality.createsprings.menus.Spring.SpringMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.Portality.createsprings.CreateSprings;


public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CreateSprings.MODID);

    public static final RegistryObject<MenuType<PunchcardMenu>> PUNCHCARD_MENU = MENUS.register(
            "punchcard_menu",
            () -> new MenuType<>(
                    (int containerId, Inventory playerInventory) ->
                            new PunchcardMenu(
                                    containerId,
                                    playerInventory,
                                    // Получаем предмет из руки игрока
                                    playerInventory.player.getItemInHand(playerInventory.player.getUsedItemHand())
                            ),
                    FeatureFlagSet.of()
            )
    );

    public static final RegistryObject<MenuType<SpringMenu>> SPRING_MENU = MENUS.register(
            "spring_menu",
            () -> new MenuType<>(
                    (int containerId, Inventory playerInventory) ->
                            new SpringMenu(
                                    containerId,
                                    playerInventory,
                                    // Получаем предмет из руки игрока
                                    playerInventory.player.getItemInHand(playerInventory.player.getUsedItemHand())
                            ),
                    FeatureFlagSet.of()
            )
    );
}