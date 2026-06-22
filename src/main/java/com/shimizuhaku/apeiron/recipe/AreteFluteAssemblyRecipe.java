package com.shimizuhaku.apeiron.recipe;

import com.shimizuhaku.apeiron.capability.CapabilityRegistry;
import com.shimizuhaku.apeiron.item.AreteItem;
import com.shimizuhaku.apeiron.item.WoodenFluteItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 笛 + 低級アレテーモジュールアイテム をクラフトテーブルで組み合わせ、
 * アレテーが装着された笛を作る不定形レシピ。
 * 低級モジュールのみがこの方法で装着可能（中級・上級は調整台が必要）。
 */
public class AreteFluteAssemblyRecipe implements CraftingRecipe {

    private final ResourceLocation id;

    public AreteFluteAssemblyRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack flute = ItemStack.EMPTY;
        ItemStack arete = ItemStack.EMPTY;
        int otherCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            System.out.println("[APEIRON DEBUG] slot " + i + ": " + stack.getItem()
                    + " / class=" + stack.getItem().getClass().getName()
                    + " / isWoodenFlute=" + (stack.getItem() instanceof WoodenFluteItem)
                    + " / isAreteItem=" + (stack.getItem() instanceof AreteItem));

            if (stack.getItem() instanceof WoodenFluteItem) {
                if (!flute.isEmpty()) { System.out.println("[APEIRON DEBUG] fail: flute重複"); return false; }
                flute = stack;
            } else if (stack.getItem() instanceof AreteItem areteItem) {
                System.out.println("[APEIRON DEBUG] areteItem tier=" + areteItem.getTier());
                if (areteItem.getTier() != 1) { System.out.println("[APEIRON DEBUG] fail: tier != 1"); return false; }
                if (!arete.isEmpty()) { System.out.println("[APEIRON DEBUG] fail: arete重複"); return false; }
                arete = stack;
            } else {
                otherCount++;
                System.out.println("[APEIRON DEBUG] otherCount++ now=" + otherCount);
            }
        }

        if (flute.isEmpty() || arete.isEmpty() || otherCount > 0) {
            System.out.println("[APEIRON DEBUG] fail: flute.isEmpty=" + flute.isEmpty()
                    + " arete.isEmpty=" + arete.isEmpty() + " otherCount=" + otherCount);
            return false;
        }

        boolean result = flute.getCapability(CapabilityRegistry.INSTRUMENT_DATA)
                .map(cap -> !cap.hasArete())
                .orElse(true);
        System.out.println("[APEIRON DEBUG] final result=" + result);
        return result;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack flute = ItemStack.EMPTY;
        AreteItem areteItem = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof WoodenFluteItem) {
                flute = stack;
            } else if (stack.getItem() instanceof AreteItem ai) {
                areteItem = ai;
            }
        }

        ItemStack result = flute.copy();
        result.setCount(1);

        if (areteItem != null) {
            final AreteItem finalArete = areteItem;
            ItemStack finalFlute = flute;
            result.getCapability(CapabilityRegistry.INSTRUMENT_DATA).ifPresent(cap -> {
                cap.attachArete(finalArete.getAreteId(), finalArete.getTier());
                if (finalFlute.getItem() instanceof WoodenFluteItem fluteItem) {
                    cap.recalculateTabulaRasaSlots(fluteItem.getTabulaRasaSlotsPerArete());
                }
            });
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(com.shimizuhaku.apeiron.item.ModItems.WOODEN_FLUTE.get());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ARETE_FLUTE_ASSEMBLY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}