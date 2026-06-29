package com.shimizuhaku.apeiron.item;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * eidosModuleId（文字列）から対応する EidosModuleItem インスタンスを引くためのレジストリ。
 * ModItems の登録完了後に build() を呼んで構築する。
 */
public class EidosModuleRegistry {
    private static final Map<String, EidosModuleItem> BY_EIDOS_ID = new HashMap<>();

    private EidosModuleRegistry() {}

    public static void build() {
        BY_EIDOS_ID.clear();
        for (var item : ForgeRegistries.ITEMS) {
            if (item instanceof EidosModuleItem eidosItem) {
                String id = eidosItem.getEidosModuleId();
                if (BY_EIDOS_ID.containsKey(id)) {
                    throw new IllegalStateException(
                            "EidosModuleItem の eidosModuleId が重複しています: " + id +
                                    " (" + BY_EIDOS_ID.get(id) + " と " + eidosItem + ")");
                }
                BY_EIDOS_ID.put(id, eidosItem);
            }
        }
    }

    public static Optional<EidosModuleItem> get(String eidosModuleId) {
        return Optional.ofNullable(BY_EIDOS_ID.get(eidosModuleId));
    }
}