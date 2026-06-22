package com.shimizuhaku.apeiron.menu;

import com.shimizuhaku.apeiron.capability.InstrumentCapability;
import com.shimizuhaku.apeiron.item.BaseTabulaRasaItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * InstrumentCapability のタブラ・ラサスロットを、バニラのSlot/Containerにアダプトするためのクラス。
 */
public class TabulaRasaSlot extends Slot {

    private final InstrumentCapability capability;
    private final int tabulaIndex;

    public TabulaRasaSlot(InstrumentCapability capability, int tabulaIndex, int x, int y) {
        super(new CapabilityBackedContainer(capability), tabulaIndex, x, y);
        this.capability = capability;
        this.tabulaIndex = tabulaIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof BaseTabulaRasaItem;
    }

    @Override
    public int getMaxStackSize() {
        return 1; // タブラ・ラサは常に1個ずつ
    }

    /**
     * InstrumentCapability をバニラの Container インターフェースに見せかけるための内部クラス。
     * これにより既存の Slot 実装をそのまま再利用できる。
     */
    private static class CapabilityBackedContainer implements Container {
        private final InstrumentCapability capability;

        CapabilityBackedContainer(InstrumentCapability capability) {
            this.capability = capability;
        }

        @Override public int getContainerSize() { return capability.getTabulaRasaSlotCount(); }
        @Override public boolean isEmpty() { return !capability.hasAnyTabulaRasa(); }
        @Override public ItemStack getItem(int slot) { return capability.getTabulaRasa(slot); }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack existing = capability.getTabulaRasa(slot);
            if (existing.isEmpty()) return ItemStack.EMPTY;
            ItemStack split = existing.split(amount);
            capability.setTabulaRasa(slot, existing.isEmpty() ? ItemStack.EMPTY : existing);
            return split;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack existing = capability.getTabulaRasa(slot);
            capability.setTabulaRasa(slot, ItemStack.EMPTY);
            return existing;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            capability.setTabulaRasa(slot, stack);
        }

        @Override public void setChanged() { /* Capabilityはアイテム本体のNBTなので、特別な保存処理は不要 */ }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() {
            for (int i = 0; i < capability.getTabulaRasaSlotCount(); i++) {
                capability.setTabulaRasa(i, ItemStack.EMPTY);
            }
        }
    }
}