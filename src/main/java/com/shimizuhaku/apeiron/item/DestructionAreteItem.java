package com.shimizuhaku.apeiron.item;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

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
     * このアレテーが対応する採掘ティア相当の判定。
     * 企投モジュール「精密」が装着されるとより上位の判定を返すよう、
     * 将来サブクラスや企投処理でオーバーライドする想定。
     * デフォルトは木ツール相当（石・鉄・ダイヤ要求ブロックは破壊不可）。
     */
    public boolean canBreak(BlockState state) {
        return !state.is(BlockTags.NEEDS_STONE_TOOL)
                && !state.is(BlockTags.NEEDS_IRON_TOOL)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    /**
     * 破壊魔法を実行する。対象範囲は将来「拡大企投モジュール」で広げる想定だが、
     * 現段階では単一ブロックのみを瞬間破壊する。
     * 楽器（instrumentStack）が記憶しているタブラ・ラサのブロックとマッチしない場合は破壊できない。
     * @return 破壊に成功したか
     */
    public boolean performDestruction(Level level, Player player, BlockPos pos, ItemStack instrumentStack) {
        if (level.isClientSide) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (!canBreak(state)) return false;

        String blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString();

        boolean memorized = instrumentStack.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                .map(cap -> cap.hasMemorizedBlock(blockId))
                .orElse(false);
        if (!memorized) return false;

        return level.destroyBlock(pos, true, player);
    }
}