package com.shimizuhaku.apeiron.capability;

public interface IApeironData {
    String getAttribute();    // 土、水、火、空気
    float getAccumulation(); // 蓄積性
    float getDecay();        // 減衰性

    void setProperties(String attribute, float accumulation, float decay);
}