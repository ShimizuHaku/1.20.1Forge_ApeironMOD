package com.shimizuhaku.apeiron.item;

public class PureFruitItem extends BaseTabulaRasaItem {
    public PureFruitItem(Properties properties) {
        super(properties);
    }

    @Override
    public float getAccumulationRate() {
        return 1.0f; // 無垢の果実は標準的な蓄積性能
    }
}