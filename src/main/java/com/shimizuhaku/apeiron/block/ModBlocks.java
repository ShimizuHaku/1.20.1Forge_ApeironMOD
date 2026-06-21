package com.shimizuhaku.apeiron.block;

import com.shimizuhaku.apeiron.Apeiron;
import com.shimizuhaku.apeiron.item.ModItems; // 後で作成するBlockItemのために必要
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Apeiron.MOD_ID);

    public static final RegistryObject<Block> TUNING_ALTAR = BLOCKS.register("tuning_altar",
            () -> new TuningAltarBlock(BlockBehaviour.Properties.of().strength(2.0f)));

    // 祭壇の「アイテム版」の登録（これがないとインベントリに入りません）
    public static final RegistryObject<Item> TUNING_ALTAR_ITEM = ModItems.ITEMS.register("tuning_altar",
            () -> new BlockItem(TUNING_ALTAR.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}