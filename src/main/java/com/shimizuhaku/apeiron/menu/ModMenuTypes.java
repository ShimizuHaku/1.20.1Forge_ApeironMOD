package com.shimizuhaku.apeiron.menu;

import com.shimizuhaku.apeiron.Apeiron;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Apeiron.MOD_ID);

    public static final RegistryObject<MenuType<InstrumentMenu>> INSTRUMENT_MENU =
            MENU_TYPES.register("instrument_menu", () ->
                    net.minecraftforge.common.extensions.IForgeMenuType.create(InstrumentMenu::new));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}