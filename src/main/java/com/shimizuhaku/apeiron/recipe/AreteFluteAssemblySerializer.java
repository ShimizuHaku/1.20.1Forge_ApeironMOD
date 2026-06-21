package com.shimizuhaku.apeiron.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AreteFluteAssemblySerializer implements RecipeSerializer<AreteFluteAssemblyRecipe> {

    @Override
    public AreteFluteAssemblyRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
        // パラメータ無しのレシピ（条件はJavaコード側のmatches()で固定判定するため、JSON側に追加項目は不要）
        return new AreteFluteAssemblyRecipe(id);
    }

    @Override
    public AreteFluteAssemblyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return new AreteFluteAssemblyRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, AreteFluteAssemblyRecipe recipe) {
        // 状態を持たないので何も書き込まない
    }
}