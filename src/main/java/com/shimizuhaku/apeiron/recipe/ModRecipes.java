package com.shimizuhaku.apeiron.recipe;

import com.shimizuhaku.apeiron.Apeiron;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Apeiron.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Apeiron.MOD_ID);

    public static final RegistryObject<RecipeSerializer<AreteFluteAssemblyRecipe>> ARETE_FLUTE_ASSEMBLY_SERIALIZER =
            SERIALIZERS.register("arete_flute_assembly", AreteFluteAssemblySerializer::new);

    public static final RegistryObject<RecipeType<AreteFluteAssemblyRecipe>> ARETE_FLUTE_ASSEMBLY_TYPE =
            TYPES.register("arete_flute_assembly", () -> new RecipeType<AreteFluteAssemblyRecipe>() {
                @Override
                public String toString() {
                    return Apeiron.MOD_ID + ":arete_flute_assembly";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}