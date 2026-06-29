package com.shimizuhaku.apeiron.item;

/**
 * 軽減形相モジュールの抽象クラス。
 * 消費量計算式の除数として働く（アレテー固有値 × 楽器固有値 を割る値）。
 * 0除算を避けるため、最低でも1を返すことを実装側に保証させる。
 */
public abstract class ReductionEidosModuleItem extends EidosModuleItem {
    public ReductionEidosModuleItem(Properties properties) {
        super(properties);
    }

    /**
     * 軽減値（除数）。1未満を返してはならない。
     */
    public abstract float getReductionValue();
}