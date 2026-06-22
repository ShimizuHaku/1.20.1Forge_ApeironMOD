package com.shimizuhaku.apeiron.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InstrumentScreen extends AbstractContainerScreen<InstrumentMenu> {
    private static final ResourceLocation CHEST_TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public InstrumentScreen(InstrumentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        // タブラ・ラサスロット行数 + プレイヤーインベントリ分の高さを動的に計算
        int tabulaRows = Math.max(1, (int) Math.ceil(menu.getTabulaRasaSlotCount() / 9.0));
        this.imageHeight = 17 + tabulaRows * 18 + 14 + 76 + 4; // 上部余白 + タブララサ行 + 余白 + プレイヤーインベントリ
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int tabulaRows = Math.max(1, (int) Math.ceil(menu.getTabulaRasaSlotCount() / 9.0));

        // 上部（タブラ・ラサ領域）
        guiGraphics.blit(CHEST_TEXTURE, x, y, 0, 0, this.imageWidth, 17 + tabulaRows * 18);
        // 下部（プレイヤーインベントリ領域）はチェストテクスチャの下部分を流用
        guiGraphics.blit(CHEST_TEXTURE, x, y + 17 + tabulaRows * 18,
                0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}