package com.shimizuhaku.apeiron.item;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class WoodenFluteItem extends Item {
    public WoodenFluteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        boolean hasArete = stack.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                .map(cap -> cap.hasArete())
                .orElse(false);

        if (!hasArete) {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.literal("笛が共鳴しようとしている..."));
            }
            return super.use(level, player, hand);
        }

        if (!level.isClientSide) {
            String areteId = stack.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                    .map(cap -> cap.getAreteId()).orElse("none");

            AreteRegistry.get(areteId).ifPresentOrElse(areteItem -> {
                if (areteItem instanceof DestructionAreteItem destructionArete) {
                    HitResult hit = player.pick(5.0D, 0.0F, false);
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                        boolean success = destructionArete.performDestruction(level, player, pos);
                        if (!success) {
                            player.displayClientMessage(Component.literal("§c[!] このブロックは破壊できません"), true);
                        }
                    } else {
                        player.displayClientMessage(Component.literal("§c[!] 対象がありません"), true);
                    }
                }
                // 他のアレテー種別（火攻撃系など）はここに分岐を追加していく想定
            }, () -> player.displayClientMessage(Component.literal("§c[!] 不明なアレテーです: " + areteId), true));
        }

        return InteractionResultHolder.success(stack);
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