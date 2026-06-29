package com.shimizuhaku.apeiron.item;

/**
 * 減少形相モジュールの抽象クラス。
 * 消費量計算式の減算項として働く（除算後の値から、さらにこの値を引く）。
 */
public abstract class DecayEidosModuleItem extends EidosModuleItem {
    public DecayEidosModuleItem(Properties properties) {
        super(properties);
    }

    /**
     * 減少値（減算項）。0以上を想定。
     */
    public abstract float getDecayValue();
}