package com.shimizuhaku.apeiron.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import com.shimizuhaku.apeiron.Apeiron;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Apeiron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappings {

    public static final String CATEGORY_APEIRON = "key.categories.apeiron";

    public static final KeyMapping OPEN_INSTRUMENT_GUI = new KeyMapping(
            "key.apeiron.open_instrument_gui",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey("key.keyboard.g"), // デフォルトキー：Gキー（仮）
            CATEGORY_APEIRON
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_INSTRUMENT_GUI);
    }
}