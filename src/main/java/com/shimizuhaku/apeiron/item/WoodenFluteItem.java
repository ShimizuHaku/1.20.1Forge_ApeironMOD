package com.shimizuhaku.apeiron.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
}