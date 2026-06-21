package com.shimizuhaku.apeiron.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityRegistry {
    public static final Capability<InstrumentData> INSTRUMENT_DATA = CapabilityManager.get(new CapabilityToken<>(){});
}