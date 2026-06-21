package com.shimizuhaku.apeiron.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class InstrumentData {
    private String areteId = "none";
    private List<String> extensions = new ArrayList<>();

    public void saveNBT(CompoundTag tag) {
        tag.putString("Arete", areteId);
        ListTag list = new ListTag();
        for (String s : extensions) list.add(StringTag.valueOf(s));
        tag.put("Extensions", list);
    }

    public void loadNBT(CompoundTag tag) {
        this.areteId = tag.getString("Arete");
        ListTag list = tag.getList("Extensions", Tag.TAG_STRING);
        this.extensions.clear();
        for (int i = 0; i < list.size(); i++) this.extensions.add(list.getString(i));
    }
    // Getter/Setterなどを定義
}