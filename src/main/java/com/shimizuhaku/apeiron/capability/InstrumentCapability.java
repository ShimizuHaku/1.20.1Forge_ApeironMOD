package com.shimizuhaku.apeiron.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class InstrumentCapability implements INBTSerializable<CompoundTag> {
    private String areteId = "none";
    private int areteTier = 0; // 0 = 未装着, 1 = 低級, 2 = 中級, 3 = 上級
    private final List<String> extensions = new ArrayList<>();

    // タブラ・ラサ保持スロット。サイズは setTabulaRasaSlotCount() で外部から制御する。
    private List<ItemStack> tabulaRasaSlots = new ArrayList<>();

    public String getAreteId() { return areteId; }
    public int getAreteTier() { return areteTier; }
    public List<String> getExtensions() { return extensions; }

    public boolean hasArete() {
        return !"none".equals(areteId);
    }

    /**
     * タブラ・ラサスロットのうち、何かが入っているものが1つでもあるか。
     */
    public boolean hasAnyTabulaRasa() {
        for (ItemStack stack : tabulaRasaSlots) {
            if (!stack.isEmpty()) return true;
        }
        return false;
    }

    /**
     * アレテーモジュールを装着する。
     * 既にアレテーが装着されている場合は失敗する（先に取り外しが必要）。
     * @return 装着に成功したか
     */
    public boolean attachArete(String areteId, int tier) {
        if (hasArete()) return false;
        this.areteId = areteId;
        this.areteTier = tier;
        this.extensions.clear();
        return true;
    }

    /**
     * 装着中のアレテーを取り外す。
     * タブラ・ラサが1つでも入っている場合は取り外せない（先にすべて取り出す必要がある）。
     * @return 取り外しに成功したか
     */
    public boolean detachArete() {
        if (hasAnyTabulaRasa()) return false;
        this.areteId = "none";
        this.areteTier = 0;
        this.extensions.clear();
        return true;
    }

    /**
     * 企投モジュールを追加する。maxExtensionsはアレテー側のTierで決まる上限。
     * @return 追加に成功したか（上限超過やアレテー未装着時はfalse）
     */
    public boolean addExtension(String extensionId, int maxExtensions) {
        if (!hasArete()) return false;
        if (extensions.size() >= maxExtensions) return false;
        extensions.add(extensionId);
        return true;
    }

    public boolean removeExtension(String extensionId) {
        return extensions.remove(extensionId);
    }

    /**
     * タブラ・ラサスロットの総数を設定する。
     * アレテー装着数 × 楽器固有値、で呼び出し側が計算した値を渡す想定。
     * 縮小時、削られるスロットに何か入っていれば失敗する（呼び出し側で先に退避が必要）。
     * @return 設定に成功したか
     */
    public boolean setTabulaRasaSlotCount(int newSize) {
        if (newSize < tabulaRasaSlots.size()) {
            for (int i = newSize; i < tabulaRasaSlots.size(); i++) {
                if (!tabulaRasaSlots.get(i).isEmpty()) return false;
            }
        }
        List<ItemStack> resized = new ArrayList<>(newSize);
        for (int i = 0; i < newSize; i++) {
            resized.add(i < tabulaRasaSlots.size() ? tabulaRasaSlots.get(i) : ItemStack.EMPTY);
        }
        this.tabulaRasaSlots = resized;
        return true;
    }

    /**
     * アレテー1個あたりのタブラ・ラサスロット数（楽器固有値）を受け取り、
     * 現在装着しているアレテー数（今は0か1）に応じてスロット総数を再計算する。
     * @param slotsPerArete 楽器固有のアレテー1個あたりスロット数
     * @return 再計算（リサイズ）に成功したか
     */
    public boolean recalculateTabulaRasaSlots(int slotsPerArete) {
        int areteCount = hasArete() ? 1 : 0; // 将来複数アレテー対応時はここを実カウントに変更
        int newSize = areteCount * slotsPerArete;
        return setTabulaRasaSlotCount(newSize);
    }

    public int getTabulaRasaSlotCount() {
        return tabulaRasaSlots.size();
    }

    public ItemStack getTabulaRasa(int index) {
        if (index < 0 || index >= tabulaRasaSlots.size()) return ItemStack.EMPTY;
        return tabulaRasaSlots.get(index);
    }

    public void setTabulaRasa(int index, ItemStack stack) {
        if (index < 0 || index >= tabulaRasaSlots.size()) return;
        tabulaRasaSlots.set(index, stack);
    }

    /**
     * 装着中のタブラ・ラサの中に、指定したブロックIDを記憶しているものが1つでもあるか。
     * 破壊魔法などの対象ブロック判定に使う。
     */
    public boolean hasMemorizedBlock(String blockId) {
        for (ItemStack stack : tabulaRasaSlots) {
            if (stack.isEmpty()) continue;
            net.minecraft.nbt.CompoundTag tag = stack.getTag();
            if (tag != null && "block".equals(tag.getString("RecordType"))
                    && blockId.equals(tag.getString("RecordValue"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Arete", areteId);
        tag.putInt("AreteTier", areteTier);
        ListTag list = new ListTag();
        for (String s : extensions) list.add(StringTag.valueOf(s));
        tag.put("Extensions", list);

        ListTag tabulaList = new ListTag();
        for (ItemStack stack : tabulaRasaSlots) {
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            tabulaList.add(stackTag);
        }
        tag.put("TabulaRasaSlots", tabulaList);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.areteId = tag.getString("Arete");
        this.areteTier = tag.getInt("AreteTier");
        ListTag list = tag.getList("Extensions", Tag.TAG_STRING);
        this.extensions.clear();
        for (int i = 0; i < list.size(); i++) this.extensions.add(list.getString(i));

        ListTag tabulaList = tag.getList("TabulaRasaSlots", Tag.TAG_COMPOUND);
        this.tabulaRasaSlots = new ArrayList<>(tabulaList.size());
        for (int i = 0; i < tabulaList.size(); i++) {
            this.tabulaRasaSlots.add(ItemStack.of(tabulaList.getCompound(i)));
        }
    }
}