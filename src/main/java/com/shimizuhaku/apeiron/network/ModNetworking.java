package com.shimizuhaku.apeiron.network;

import com.shimizuhaku.apeiron.Apeiron;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Apeiron.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                OpenInstrumentMenuPacket.class,
                OpenInstrumentMenuPacket::encode,
                OpenInstrumentMenuPacket::decode,
                OpenInstrumentMenuPacket::handle
        );
    }
}