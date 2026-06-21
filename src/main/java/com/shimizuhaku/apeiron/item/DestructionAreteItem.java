package com.shimizuhaku.apeiron.item;

// 破壊系統のアレテーの抽象クラス
public abstract class DestructionAreteItem extends AreteItem {
    public DestructionAreteItem(Properties properties) {
        super(properties);
    }

    // 破壊魔法特有の基本ダメージ補正などを定義可能
    public abstract float getBaseDamage();

    @Override
    public Rizomata elements(){
        return Rizomata.GE;
    }
}