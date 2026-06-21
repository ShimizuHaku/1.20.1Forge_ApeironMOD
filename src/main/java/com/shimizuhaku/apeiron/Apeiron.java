package com.shimizuhaku.apeiron;

import com.mojang.logging.LogUtils;
import com.shimizuhaku.apeiron.block.ModBlocks;
import com.shimizuhaku.apeiron.capability.InstrumentProvider;
import com.shimizuhaku.apeiron.item.AreteItem;
import com.shimizuhaku.apeiron.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Apeiron.MOD_ID)
public class Apeiron
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "apeiron";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    // タブ自体の定義 (Apeiron.java などに記述)
    public static final RegistryObject<CreativeModeTab> APEIRON_TAB = CREATIVE_MODE_TABS.register("apeiron_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.apeiron_tab"))
                    .icon(() -> ModItems.WOODEN_FLUTE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.WOODEN_FLUTE.get());
                        output.accept(ModItems.PURE_FRUIT.get());
                        output.accept(ModItems.APEIRON_CAMERA.get());
                        output.accept(ModBlocks.TUNING_ALTAR_ITEM.get());// ここでアイテムを入れる
                    })
                    .build());

    public Apeiron(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();

        // 1. 各クラスのレジストリ登録を呼ぶ
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        // 2. クリエイティブタブのイベントを登録
        modEventBus.addListener(this::addCreative);

        // ※ BLOCKS, ITEMS等のDeferredRegisterはここでは定義せず、
        //    ModItems.javaとModBlocks.javaの中だけで定義してください。
        System.out.println("APEIRON MOD IS LOADING!");

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // メインクラスのどこかに以下を追加
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // どのタブに追加するか？

        // CreativeModeTabs.INGREDIENTS は「素材」タブ
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.WOODEN_FLUTE);
        }
        // BUILDING_BLOCKS は「建築ブロック」タブ
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.TUNING_ALTAR_ITEM);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        // あなたの楽器アイテムのチェック
        if (event.getObject().getItem() instanceof AreteItem) {
            event.addCapability(new ResourceLocation("apeiron", "instrument_data"), new InstrumentProvider());
        }
    }
}
