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

    /**
     * このタブラ・ラサの最大耐久値。サブクラスでオーバーライドして種類ごとに変えられる。
     */
    public float getMaxDurability() {
        return 100.0F;
    }

    // 記録内容を保存するメソッド（type: "block", "tag", "entity" など）
    public static void setRecordedData(ItemStack stack, String type, String value) {
        stack.getOrCreateTag().putString("RecordType", type);
        stack.getOrCreateTag().putString("RecordValue", value);
    }

    public static boolean isExposed(ItemStack stack) {
        return stack.getOrCreateTag().contains("RecordValue");
    }

    /**
     * 現在の耐久値を取得する。NBTに未設定の場合は最大値を返す（記憶直後はフル耐久）。
     */
    public static float getDurability(ItemStack stack) {
        if (!(stack.getItem() instanceof BaseTabulaRasaItem item)) return 0f;
        if (!stack.getOrCreateTag().contains("Durability")) {
            return item.getMaxDurability();
        }
        return stack.getOrCreateTag().getFloat("Durability");
    }

    /**
     * 耐久値を設定する。0以下になった場合は呼び出し側で消滅処理を行うこと
     * （このメソッド自体はNBTの値を書き込むだけで、スタックの消滅は行わない）。
     */
    public static void setDurability(ItemStack stack, float value) {
        stack.getOrCreateTag().putFloat("Durability", Math.max(0f, value));
    }

    /**
     * 耐久値を消費する。消費後の残り耐久値を返す（0以下なら消滅させるべき状態）。
     */
    public static float consumeDurability(ItemStack stack, float amount) {
        float current = getDurability(stack);
        float remaining = current - amount;
        setDurability(stack, Math.max(0f, remaining));
        return remaining;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isExposed(stack); // 記憶済み（タブララサとして機能している）時のみバーを表示
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float ratio = getDurability(stack) / Math.max(1f, getMaxDurability());
        return Math.round(ratio * 13f); // バニラのダメージバーは最大13px
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = getDurability(stack) / Math.max(1f, getMaxDurability());
        // バニラのツールと同じ配色ロジック（緑→赤）
        return net.minecraft.util.Mth.hsvToRgb(Math.max(0f, ratio) / 3f, 1.0F, 1.0F);
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

        float durability = getDurability(stack);
        float maxDurability = getMaxDurability();
        tooltip.add(Component.literal("§a耐久値: " + (int) durability + " / " + (int) maxDurability)
                .withStyle(ChatFormatting.ITALIC));
    }
}