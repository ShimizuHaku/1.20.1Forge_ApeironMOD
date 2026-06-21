package com.shimizuhaku.apeiron.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class ApeironCameraItem extends Item {
    public ApeironCameraItem(Properties properties) { super(properties.stacksTo(1)); }

    public static ItemStack getStoredFilm(ItemStack camera) {
        CompoundTag tag = camera.getTag();
        return (tag != null && tag.contains("StoredFilm", 10)) ? ItemStack.of(tag.getCompound("StoredFilm")) : ItemStack.EMPTY;
    }

    public static void setStoredFilm(ItemStack camera, ItemStack film) {
        CompoundTag tag = camera.getOrCreateTag();
        if (film.isEmpty()) tag.remove("StoredFilm");
        else { CompoundTag f = new CompoundTag(); film.save(f); tag.put("StoredFilm", f); }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack camera = player.getItemInHand(hand);
        ItemStack film = getStoredFilm(camera);

        // 1. 取り出し処理 (スニーク中)
        if (player.isCrouching()) {
            if (!film.isEmpty()) {
                if (!level.isClientSide) {
                    player.addItem(film);
                    setStoredFilm(camera, ItemStack.EMPTY);
                    player.sendSystemMessage(Component.literal("§bフィルムを取り出しました。"));
                }
                return InteractionResultHolder.success(camera);
            }
        }

        // 2. 装填処理
        if (film.isEmpty()) {
            ItemStack offHand = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (offHand.getItem() instanceof BaseTabulaRasaItem) {
                if (!level.isClientSide) {
                    setStoredFilm(camera, offHand.copy());
                    offHand.shrink(1);
                    player.sendSystemMessage(Component.literal("§aフィルムを装填しました。"));
                }
                return InteractionResultHolder.success(camera);
            }
        } else if (!BaseTabulaRasaItem.isExposed(film)) {
            // 3. 撮影開始 (ターゲット座標を保存)
            HitResult hit = player.pick(5.0D, 0.0F, false);
            if (hit.getType() == HitResult.Type.BLOCK) {
                camera.getOrCreateTag().putLong("TargetPos", ((BlockHitResult)hit).getBlockPos().asLong());
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(camera);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player) || !player.isUsingItem() || player.getUseItem() != stack) return;

        ItemStack film = getStoredFilm(stack);
        if (!(film.getItem() instanceof BaseTabulaRasaItem baseFilm) || BaseTabulaRasaItem.isExposed(film)) return;

        // ターゲットの固定チェック
        HitResult hit = player.pick(5.0D, 0.0F, false);
        BlockPos targetPos = BlockPos.of(stack.getOrCreateTag().getLong("TargetPos"));

        if (hit.getType() != HitResult.Type.BLOCK || !((BlockHitResult)hit).getBlockPos().equals(targetPos)) {
            stack.getOrCreateTag().remove("FocusTicks"); // 視点がズレたらリセット
            player.displayClientMessage(Component.literal("§c[!] 視点がズレました"), true);
            return;
        }

        // 集中処理
        CompoundTag tag = stack.getOrCreateTag();
        int ticks = tag.getInt("FocusTicks") + 1;
        tag.putInt("FocusTicks", ticks);

        float rate = baseFilm.getAccumulationRate();
        int required = (int) (100 / rate);
        player.displayClientMessage(Component.literal("§6表象中... " + (ticks * 100 / required) + "%"), true);

        if (ticks >= required) {
            String blockId = ForgeRegistries.BLOCKS.getKey(level.getBlockState(targetPos).getBlock()).toString();
            BaseTabulaRasaItem.setRecordedData(film, "block", blockId);
            setStoredFilm(stack, film);
            tag.remove("FocusTicks");
            player.sendSystemMessage(Component.literal("§d[表象成功] §fID: " + blockId));
            player.stopUsingItem();
        }
    }

    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @Override public int getUseDuration(ItemStack stack) { return 72000; }
}