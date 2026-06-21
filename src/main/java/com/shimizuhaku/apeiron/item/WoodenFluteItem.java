package com.shimizuhaku.apeiron.item;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class WoodenFluteItem extends Item {
    public WoodenFluteItem(Properties properties) {
        super(properties);
    }

    // ここで右クリックした時の挙動（GUIを開くなど）を制御します
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // ここに「GUIを開く」処理を後で記述します
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("笛が共鳴しようとしている..."));
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level context, List<Component> tooltip, TooltipFlag flag) {
        stack.getCapability(CapabilityRegistry.INSTRUMENT_DATA).ifPresent(cap -> {
            if (cap.hasArete()) {
                String tierName = switch (cap.getAreteTier()) {
                    case 1 -> "低級";
                    case 2 -> "中級";
                    case 3 -> "上級";
                    default -> "不明";
                };
                tooltip.add(Component.literal("§d装着中: §f" + cap.getAreteId() + " §7(" + tierName + ")")
                        .withStyle(ChatFormatting.ITALIC));

                int extCount = cap.getExtensions().size();
                if (extCount > 0) {
                    tooltip.add(Component.literal("§b企投モジュール: §f" + extCount + "個")
                            .withStyle(ChatFormatting.ITALIC));
                    for (String ext : cap.getExtensions()) {
                        tooltip.add(Component.literal("  §7- " + ext));
                    }
                }
            } else {
                tooltip.add(Component.literal("§7[アレテー未装着]").withStyle(ChatFormatting.GRAY));
            }
        });
        super.appendHoverText(stack, context, tooltip, flag);
    }
}