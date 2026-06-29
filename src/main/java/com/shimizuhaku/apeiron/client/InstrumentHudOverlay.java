package com.shimizuhaku.apeiron.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import com.shimizuhaku.apeiron.capability.InstrumentCapability;
import com.shimizuhaku.apeiron.item.BaseTabulaRasaItem;
import com.shimizuhaku.apeiron.item.WoodenFluteItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 手持ちの魔法楽器について、装着中タブラ・ラサの耐久値ゲージをHUDに表示するオーバーレイ。
 * 将来「楽器本体の耐久値」が実装されたら、同じ描画ループの中に1本追加すればよい設計にしている。
 */
public class InstrumentHudOverlay implements IGuiOverlay {

    private static final int GAUGE_WIDTH = 40;
    private static final int GAUGE_HEIGHT = 4;
    private static final int GAUGE_GAP = 2;
    private static final int MARGIN_LEFT = 6;
    private static final int MARGIN_BOTTOM = 50; // ホットバーの上あたり

    @Override
    public void render(net.minecraftforge.client.gui.overlay.ForgeGui gui, GuiGraphics guiGraphics,
                       float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack instrument = resolveInstrument(player);
        if (instrument.isEmpty()) return;

        InstrumentCapability cap = instrument.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                .resolve().orElse(null);
        if (cap == null || !cap.hasArete()) return;

        int slotCount = cap.getTabulaRasaSlotCount();
        if (slotCount == 0) return;

        int baseY = screenHeight - MARGIN_BOTTOM;

        for (int i = 0; i < slotCount; i++) {
            ItemStack tabula = cap.getTabulaRasa(i);
            int y = baseY - i * (GAUGE_HEIGHT + GAUGE_GAP);
            drawGauge(guiGraphics, MARGIN_LEFT, y, tabula);
        }

        // 将来：楽器本体の耐久値ゲージをここに追加する
        // 例: drawGauge(guiGraphics, MARGIN_LEFT, baseY - slotCount * (GAUGE_HEIGHT + GAUGE_GAP) - 4, instrumentDurabilityRatio);
    }

    private ItemStack resolveInstrument(Player player) {
        ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (main.getItem() instanceof WoodenFluteItem) return main;
        ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
        if (off.getItem() instanceof WoodenFluteItem) return off;
        return ItemStack.EMPTY;
    }

    private void drawGauge(GuiGraphics guiGraphics, int x, int y, ItemStack tabula) {
        // 背景（枠）
        guiGraphics.fill(x, y, x + GAUGE_WIDTH, y + GAUGE_HEIGHT, 0xFF202020);

        if (tabula.isEmpty()) {
            // 空スロットは薄い枠だけ表示
            guiGraphics.fill(x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0xFF404040);
            return;
        }

        if (!(tabula.getItem() instanceof BaseTabulaRasaItem item)) return;

        float durability = BaseTabulaRasaItem.getDurability(tabula);
        float maxDurability = item.getMaxDurability();
        float ratio = maxDurability <= 0f ? 0f : Math.max(0f, Math.min(1f, durability / maxDurability));

        int filledWidth = Math.round((GAUGE_WIDTH - 2) * ratio);
        int color = gaugeColor(ratio);

        guiGraphics.fill(x + 1, y + 1, x + 1 + filledWidth, y + GAUGE_HEIGHT - 1, color);
    }

    private int gaugeColor(float ratio) {
        // 緑→黄→赤のグラデーション（バニラのダメージバーと同系統の配色）
        float hue = Math.max(0f, ratio) / 3f;
        return 0xFF000000 | net.minecraft.util.Mth.hsvToRgb(hue, 1.0F, 1.0F);
    }
}