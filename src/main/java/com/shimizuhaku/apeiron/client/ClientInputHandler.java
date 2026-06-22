package com.shimizuhaku.apeiron.client;

import com.shimizuhaku.apeiron.Apeiron;
import com.shimizuhaku.apeiron.network.ModNetworking;
import com.shimizuhaku.apeiron.network.OpenInstrumentMenuPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Apeiron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) return; // GUI表示中は無視

        if (ModKeyMappings.OPEN_INSTRUMENT_GUI.consumeClick()) {
            // メインハンド優先、なければオフハンドを確認してパケット送信
            InteractionHand hand = resolveInstrumentHand(minecraft);
            if (hand != null) {
                ModNetworking.CHANNEL.sendToServer(new OpenInstrumentMenuPacket(hand));
            }
        }
    }

    private static InteractionHand resolveInstrumentHand(Minecraft minecraft) {
        var player = minecraft.player;
        if (player.getMainHandItem().getItem() instanceof com.shimizuhaku.apeiron.item.WoodenFluteItem) {
            return InteractionHand.MAIN_HAND;
        }
        if (player.getOffhandItem().getItem() instanceof com.shimizuhaku.apeiron.item.WoodenFluteItem) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }
}