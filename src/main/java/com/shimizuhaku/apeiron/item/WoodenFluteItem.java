package com.shimizuhaku.apeiron.item;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import com.shimizuhaku.apeiron.menu.InstrumentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class WoodenFluteItem extends Item {
    public WoodenFluteItem(Properties properties) {
        super(properties);
    }

    /**
     * この楽器における「アレテー1つあたりのタブラ・ラサスロット数」。
     * 楽器の種類ごとに異なる値を持たせたい場合、サブクラスでオーバーライドする想定。
     */
    public int getTabulaRasaSlotsPerArete() {
        return 1;
    }

    /**
     * 形相モジュール用の固定スロット数（楽器固有値）。増減はしない。
     */
    public int getEidosModuleSlotCount() {
        return 2; // 仮値：軽減・減少を1個ずつ装着できる想定
    }

    /**
     * 楽器固有の補正値。アレテー固有値との掛け算に使う（消費量計算式の分子側）。
     */
    public float getInstrumentFactor() {
        return 1.0F;
    }

    /**
     * タブラ・ラサ耐久消費量を計算する。
     * 消費量 = max( アレテー固有値 × 楽器固有値 / 軽減形相モジュール値 − 減少形相モジュール値 , 0 )
     */
    /**
     * タブラ・ラサ耐久消費量を計算する。
     * 消費量 = max( アレテー固有値 × 楽器固有値 / 軽減形相モジュール値 − 減少形相モジュール値 , 0 )
     */
    public static float calculateDurabilityCost(float areteValue, float instrumentFactor,
                                                com.shimizuhaku.apeiron.capability.InstrumentCapability cap) {
        float reduction = 1.0F; // 未装着時のデフォルト（除数なので1が中立値）
        float decay = 0.0F;     // 未装着時のデフォルト（減算項なので0が中立値）

        for (int i = 0; i < cap.getEidosModuleSlotCount(); i++) {
            String moduleId = cap.getEidosModule(i);
            if ("none".equals(moduleId)) continue;

            var moduleOpt = EidosModuleRegistry.get(moduleId);
            if (moduleOpt.isEmpty()) continue;

            EidosModuleItem module = moduleOpt.get();
            if (module instanceof ReductionEidosModuleItem reductionModule) {
                reduction = Math.max(1.0F, reductionModule.getReductionValue());
            } else if (module instanceof DecayEidosModuleItem decayModule) {
                decay += decayModule.getDecayValue();
            }
        }

        return Math.max(0f, areteValue * Math.max(0f, instrumentFactor) / reduction - decay);
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
            return InteractionResultHolder.success(stack);
        }

        if (!level.isClientSide) {
            String areteId = stack.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                    .map(cap -> cap.getAreteId()).orElse("none");

            AreteRegistry.get(areteId).ifPresentOrElse(areteItem -> {
                if (areteItem instanceof DestructionAreteItem destructionArete) {
                    HitResult hit = player.pick(5.0D, 0.0F, false);
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                        boolean success = destructionArete.performDestruction(level, player, pos, stack);
                        if (!success) {
                            player.displayClientMessage(Component.literal("§c[!] このブロックは破壊できません"), true);
                        }
                    } else {
                        player.displayClientMessage(Component.literal("§c[!] 対象がありません"), true);
                    }
                }
            }, () -> player.displayClientMessage(Component.literal("§c[!] 不明なアレテーです: " + areteId), true));
        }

        return InteractionResultHolder.success(stack);
    }

    private void openMenu(net.minecraft.server.level.ServerPlayer player, ItemStack stack, InteractionHand hand) {
        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40; // 40 = オフハンド
        net.minecraftforge.network.NetworkHooks.openScreen(player,
                new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("魔法楽器");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player p) {
                        return new InstrumentMenu(containerId, inv, hand);
                    }
                },
                buf -> buf.writeEnum(hand));
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

                int tabulaCount = cap.getTabulaRasaSlotCount();
                if (tabulaCount > 0) {
                    int filled = 0;
                    for (int i = 0; i < tabulaCount; i++) {
                        if (!cap.getTabulaRasa(i).isEmpty()) filled++;
                    }
                    tooltip.add(Component.literal("§3タブラ・ラサ: §f" + filled + " / " + tabulaCount)
                            .withStyle(ChatFormatting.ITALIC));
                }
            } else {
                tooltip.add(Component.literal("§7[アレテー未装着]").withStyle(ChatFormatting.GRAY));
            }
        });
        super.appendHoverText(stack, context, tooltip, flag);
    }
}