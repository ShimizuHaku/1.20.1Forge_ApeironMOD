package com.shimizuhaku.apeiron.item;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * areteId（文字列）から対応する AreteItem インスタンスを引くためのレジストリ。
 * ModItems の登録完了後に build() を呼んで構築する。
 */
public class AreteRegistry {
    private static final Map<String, AreteItem> BY_ARETE_ID = new HashMap<>();

    private AreteRegistry() {}

    /**
     * ForgeRegistries.ITEMS に登録済みの全アイテムを走査し、
     * AreteItem であるものを areteId をキーにして登録する。
     * FMLCommonSetupEvent など、登録完了後のタイミングで一度呼べばよい。
     */
    public static void build() {
        BY_ARETE_ID.clear();
        for (var item : ForgeRegistries.ITEMS) {
            if (item instanceof AreteItem areteItem) {
                String id = areteItem.getAreteId();
                if (BY_ARETE_ID.containsKey(id)) {
                    throw new IllegalStateException(
                            "AreteItem の areteId が重複しています: " + id +
                                    " (" + BY_ARETE_ID.get(id) + " と " + areteItem + ")");
                }
                BY_ARETE_ID.put(id, areteItem);
            }
        }
    }

    public static Optional<AreteItem> get(String areteId) {
        return Optional.ofNullable(BY_ARETE_ID.get(areteId));
    }
}