package com.shimizuhaku.apeiron.item;

import com.shimizuhaku.apeiron.Apeiron;
import com.shimizuhaku.apeiron.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // アイテム登録用のレジストリを作成
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Apeiron.MOD_ID);

    // 笛の登録
    public static final RegistryObject<Item> WOODEN_FLUTE = ITEMS.register("wooden_flute",
            () -> new WoodenFluteItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PURE_FRUIT = ITEMS.register("pure_fruit",
            () -> new PureFruitItem(new Item.Properties()));
    public static final RegistryObject<Item> APEIRON_CAMERA = ITEMS.register("apeiron_camera",
            () -> new ApeironCameraItem(new Item.Properties()));

    // 低級破壊アレテーモジュール
    public static final RegistryObject<Item> LOW_DESTRUCTION_ARETE = ITEMS.register("low_destruction_arete",
            () -> new LowDestructionAreteItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}