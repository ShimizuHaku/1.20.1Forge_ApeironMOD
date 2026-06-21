package com.shimizuhaku.apeiron.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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

    /**
     * このアレテーが対応する採掘ティア相当のブロックタグ。
     * 企投モジュール「精密」が装着されるとより上位のタグを返すよう、
     * 将来サブクラスや企投処理でオーバーライドする想定。
     * デフォルトは木ツール相当（incorrect_for_wooden_tool 以外なら破壊可能）。
     */
    public boolean canBreak(BlockState state) {
        return !state.is(BlockTags.NEEDS_STONE_TOOL);
    }

    /**
     * 破壊魔法を実行する。対象範囲は将来「拡大企投モジュール」で広げる想定だが、
     * 現段階では単一ブロックのみを瞬間破壊する。
     * @return 破壊に成功したか
     */
    public boolean performDestruction(Level level, Player player, BlockPos pos) {
        if (level.isClientSide) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (!canBreak(state)) return false;

        return level.destroyBlock(pos, true, player);
    }
}