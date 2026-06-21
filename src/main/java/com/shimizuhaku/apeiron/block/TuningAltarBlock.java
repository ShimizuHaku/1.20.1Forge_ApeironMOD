package com.shimizuhaku.apeiron.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TuningAltarBlock extends Block {
    public TuningAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // 右クリックされた時の処理をここに書きます
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("祭壇が共鳴を待っている..."));

            // 手持ちのアイテムをチェックするロジックをここに追加していく
            // if (player.getItemInHand(hand).is(ModItems.WOODEN_FLUTE.get())) { ... }
        }
        return InteractionResult.SUCCESS;
    }
}