package com.shimizuhaku.apeiron.item;

import net.minecraft.world.item.Item;

// すべてのアレテーの基底クラス
public abstract class AreteItem extends Item {
    public AreteItem(Properties properties) {
        super(properties);
    }

    // モジュールのTier（級）を取得
    public abstract int getTier();

    // このアレテーが装着可能な企投の最大数
    public abstract int getMaxExtensions();

    public abstract Rizomata elements();

    // アレテーの一意識別子（楽器のNBTに記録する文字列）
    public abstract String getAreteId();

    public enum Rizomata{PYR,HYDOR,AER,GE}
}