package com.shimizuhaku.apeiron.item;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import com.shimizuhaku.apeiron.capability.InstrumentCapability;
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

    public boolean canBreak(BlockState state) {
        return !state.is(BlockTags.NEEDS_STONE_TOOL)
                && !state.is(BlockTags.NEEDS_IRON_TOOL)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    /**
     * 破壊魔法を実行する。対象範囲は将来「拡大企投モジュール」で広げる想定だが、
     * 現段階では単一ブロックのみを瞬間破壊する。
     * 楽器（instrumentStack）が記憶しているタブラ・ラサのブロックとマッチしない場合は破壊できない。
     * 発動成功時、マッチしたタブラ・ラサの耐久値を消費し、0になれば消滅させる。
     * @return 破壊に成功したか
     */
    public boolean performDestruction(Level level, Player player, BlockPos pos, ItemStack instrumentStack) {
        if (level.isClientSide) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (!canBreak(state)) return false;

        String blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString();

        var capOpt = instrumentStack.getCapability(CapabilityRegistry.INSTRUMENT_DATA).resolve();
        if (capOpt.isEmpty()) return false;
        InstrumentCapability cap = capOpt.get();

        int matchedSlot = findMemorizedSlot(cap, blockId);
        if (matchedSlot < 0) return false;

        boolean destroyed = level.destroyBlock(pos, true, player);
        if (!destroyed) return false;

        consumeTabulaRasaDurability(cap, matchedSlot, instrumentStack);
        return true;
    }

    /**
     * 装着中のタブラ・ラサの中から、指定ブロックIDを記憶しているスロットを探す。
     * @return 見つかったスロット番号。なければ-1。
     */
    private int findMemorizedSlot(InstrumentCapability cap, String blockId) {
        for (int i = 0; i < cap.getTabulaRasaSlotCount(); i++) {
            ItemStack tabula = cap.getTabulaRasa(i);
            if (tabula.isEmpty()) continue;
            var tag = tabula.getTag();
            if (tag != null && "block".equals(tag.getString("RecordType"))
                    && blockId.equals(tag.getString("RecordValue"))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * マッチしたタブラ・ラサの耐久を消費する。0以下になれば枠を空にする（消滅）。
     */
    private void consumeTabulaRasaDurability(InstrumentCapability cap, int slot, ItemStack instrumentStack) {
        ItemStack tabula = cap.getTabulaRasa(slot);
        if (tabula.isEmpty()) return;

        float areteValue = getBaseDamage();
        float instrumentFactor = (instrumentStack.getItem() instanceof WoodenFluteItem flute)
                ? flute.getInstrumentFactor() : 1.0F;

        float cost = WoodenFluteItem.calculateDurabilityCost(areteValue, instrumentFactor, cap);
        float remaining = BaseTabulaRasaItem.consumeDurability(tabula, cost);

        if (remaining <= 0f) {
            cap.setTabulaRasa(slot, ItemStack.EMPTY); // 耐久0で消滅
        } else {
            cap.setTabulaRasa(slot, tabula); // 念のため明示的に書き戻す
        }
    }
}