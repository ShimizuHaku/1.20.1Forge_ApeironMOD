package com.shimizuhaku.apeiron.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseTabulaRasaItem extends Item {
    public BaseTabulaRasaItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract float getAccumulationRate();

    // 記録内容を保存するメソッド（type: "block", "tag", "entity" など）
    public static void setRecordedData(ItemStack stack, String type, String value) {
        stack.getOrCreateTag().putString("RecordType", type);
        stack.getOrCreateTag().putString("RecordValue", value);
    }

    public static boolean isExposed(ItemStack stack) {
        return stack.getOrCreateTag().contains("RecordValue");
    }

    // ツールチップ表示機能
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level context, List<Component> tooltip, TooltipFlag flag) {
        if (isExposed(stack)) {
            String type = stack.getOrCreateTag().getString("RecordType");
            String value = stack.getOrCreateTag().getString("RecordValue");
            tooltip.add(Component.literal("§d[" + type.toUpperCase() + "] §f" + value).withStyle(ChatFormatting.ITALIC));
        } else {
            tooltip.add(Component.literal("§7[無垢: 記憶を待っています]").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.literal("§b蓄積効率: " + getAccumulationRate() + "x"));
    }
}