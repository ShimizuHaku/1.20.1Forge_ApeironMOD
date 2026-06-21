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
    private final List<String> extensions = new ArrayList<>();

    public String getAreteId() { return areteId; }
    public void setAreteId(String id) { this.areteId = id; }
    public List<String> getExtensions() { return extensions; }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Arete", areteId);
        ListTag list = new ListTag();
        for (String s : extensions) list.add(StringTag.valueOf(s));
        tag.put("Extensions", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.areteId = tag.getString("Arete");
        ListTag list = tag.getList("Extensions", Tag.TAG_STRING);
        this.extensions.clear();
        for (int i = 0; i < list.size(); i++) this.extensions.add(list.getString(i));
    }
}