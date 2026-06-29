package com.shimizuhaku.apeiron.item;

import net.minecraft.world.item.Item;

/**
 * 形相モジュールの基底クラス。
 * アレテーモジュールとは別枠（楽器固有の固定スロット）に装着される、
 * 魔法発動時のタブラ・ラサ耐久消費量を調整するモジュール群。
 */
public abstract class EidosModuleItem extends Item {
    public EidosModuleItem(Properties properties) {
        super(properties);
    }

    /**
     * 形相モジュールの一意識別子（楽器のNBTに記録する文字列）。
     */
    public abstract String getEidosModuleId();
}