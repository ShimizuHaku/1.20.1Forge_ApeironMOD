package com.shimizuhaku.apeiron.event;

import com.shimizuhaku.apeiron.Apeiron;
import com.shimizuhaku.apeiron.capability.InstrumentProvider;
import com.shimizuhaku.apeiron.item.WoodenFluteItem; // 笛アイテムのクラス
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Apeiron.MOD_ID)
public class CapabilityHandler {
    private static final ResourceLocation IDENTIFIER = new ResourceLocation(Apeiron.MOD_ID, "instrument_data");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof WoodenFluteItem) {
            event.addCapability(IDENTIFIER, new InstrumentProvider());
        }
    }
}