package com.shimizuhaku.apeiron.item;

public class LowDestructionAreteItem extends DestructionAreteItem {
    public LowDestructionAreteItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getTier() {
        return 1; // 低級（1級）
    }

    @Override
    public int getMaxExtensions() {
        return 1; // 低級は企投を1つまで装着可能
    }

    @Override
    public float getBaseDamage() {
        return 3.0F; // 低級の基本威力
    }
}