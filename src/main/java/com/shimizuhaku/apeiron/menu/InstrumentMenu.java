package com.shimizuhaku.apeiron.menu;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import com.shimizuhaku.apeiron.capability.InstrumentCapability;
import com.shimizuhaku.apeiron.item.BaseTabulaRasaItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 魔法楽器のGUI用メニュー。
 * タブラ・ラサスロットのみを表示する（アレテー・企投の表示やGUI上の操作は将来追加予定）。
 * スロット数はアレテー装着数 × 楽器固有値で決まり、InstrumentCapability から動的に取得する。
 */
public class InstrumentMenu extends AbstractContainerMenu {

    private final ItemStack instrumentStack;
    private final InstrumentCapability capability;
    private final int tabulaRasaSlotCount;

    private static final int TABULA_RASA_SLOT_X_START = 8;
    private static final int TABULA_RASA_SLOT_Y_START = 18;
    private static final int SLOTS_PER_ROW = 9;

    public InstrumentMenu(int containerId, Inventory playerInventory, InteractionHand hand) {
        super(ModMenuTypes.INSTRUMENT_MENU.get(), containerId);

        this.instrumentStack = playerInventory.player.getItemInHand(hand);
        this.capability = instrumentStack.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                .orElseThrow(() -> new IllegalStateException("楽器にInstrumentCapabilityがありません"));
        this.tabulaRasaSlotCount = capability.getTabulaRasaSlotCount();

        // タブラ・ラサスロットを動的に追加
        for (int i = 0; i < tabulaRasaSlotCount; i++) {
            int row = i / SLOTS_PER_ROW;
            int col = i % SLOTS_PER_ROW;
            int x = TABULA_RASA_SLOT_X_START + col * 18;
            int y = TABULA_RASA_SLOT_Y_START + row * 18;
            this.addSlot(new TabulaRasaSlot(capability, i, x, y));
        }

        // プレイヤーインベントリ（タブラ・ラサスロットの行数分、下に余白を空けて配置）
        int inventoryY = TABULA_RASA_SLOT_Y_START + rowCount() * 18 + 14;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, inventoryY + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, inventoryY + 3 * 18 + 4));
        }
    }
    // クライアント側でNetworkHooksから呼ばれるコンストラクタ
    public InstrumentMenu(int containerId, Inventory playerInventory, net.minecraft.network.FriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readEnum(InteractionHand.class));
    }

    private int rowCount() {
        return Math.max(1, (int) Math.ceil(tabulaRasaSlotCount / (double) SLOTS_PER_ROW));
    }

    public int getTabulaRasaSlotCount() {
        return tabulaRasaSlotCount;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        ItemStack copy = stackInSlot.copy();

        if (index < tabulaRasaSlotCount) {
            // タブラ・ラサスロット → プレイヤーインベントリへ
            if (!this.moveItemStackTo(stackInSlot, tabulaRasaSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // プレイヤーインベントリ → タブラ・ラサスロットへ（タブラ・ラサ系アイテムのみ受付）
            if (!(stackInSlot.getItem() instanceof BaseTabulaRasaItem)) return ItemStack.EMPTY;
            if (!this.moveItemStackTo(stackInSlot, 0, tabulaRasaSlotCount, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return instrumentStack.getItem() instanceof com.shimizuhaku.apeiron.item.WoodenFluteItem
                && !instrumentStack.isEmpty();
    }
}