package com.shimizuhaku.apeiron.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import java.util.ArrayList;
import java.util.List;

public class InstrumentCapability implements INBTSerializable<CompoundTag> {
    private String areteId = "none";
    private int areteTier = 0; // 0 = 未装着, 1 = 低級, 2 = 中級, 3 = 上級
    private final List<String> extensions = new ArrayList<>();

    public String getAreteId() { return areteId; }
    public int getAreteTier() { return areteTier; }
    public List<String> getExtensions() { return extensions; }

    public boolean hasArete() {
        return !"none".equals(areteId);
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
     */
    public void detachArete() {
        this.areteId = "none";
        this.areteTier = 0;
        this.extensions.clear();
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Arete", areteId);
        tag.putInt("AreteTier", areteTier);
        ListTag list = new ListTag();
        for (String s : extensions) list.add(StringTag.valueOf(s));
        tag.put("Extensions", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.areteId = tag.getString("Arete");
        this.areteTier = tag.getInt("AreteTier");
        ListTag list = tag.getList("Extensions", Tag.TAG_STRING);
        this.extensions.clear();
        for (int i = 0; i < list.size(); i++) this.extensions.add(list.getString(i));
    }
}