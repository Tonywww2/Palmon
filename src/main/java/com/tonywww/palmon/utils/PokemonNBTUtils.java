package com.tonywww.palmon.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.DataKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PokemonNBTUtils {

    public static Species getSpeciesFromNBT(CompoundTag nbt) {
        String rawID = nbt.getString(DataKeys.POKEMON_SPECIES_IDENTIFIER).replace("pokemonCobblemon", "cobblemon");
        return PokemonSpecies.INSTANCE.getByIdentifier(new ResourceLocation(rawID));
    }

    public static short getLevelFromNBT(CompoundTag nbt) {
        return nbt.getShort(DataKeys.POKEMON_LEVEL);
    }

    public static FormData getFormFromNBT(CompoundTag nbt, Species species) {
        for (FormData f : species.getForms()) {
            if (f.formOnlyShowdownId().equals(nbt.getString(DataKeys.POKEMON_FORM_ID))) {
                return f;
            }
        }
        return species.getStandardForm();
    }

    public static boolean getShinyFromNBT(CompoundTag nbt) {
        return nbt.getBoolean(DataKeys.POKEMON_SHINY);
    }

    public static CompoundTag getAllIVsFromNBT(CompoundTag nbt) {
        return nbt.getCompound(DataKeys.POKEMON_IVS);
    }

    public static short getIVFromNBT(CompoundTag nbt, Stats stats) {
        return nbt.getShort(stats.getIdentifier().getPath());
    }

    public static ElementalType getType1FromForm(FormData form) {
        return form.getPrimaryType();
    }

    public static ElementalType getType2FromForm(FormData form) {
        return form.getSecondaryType();
    }

    public static Pokemon loadSafePokemon(CompoundTag nbt, boolean isClientSide) {
        if (isClientSide) {
            return new ClientPokemon().fromNBT(nbt);
        } else {
            return Pokemon.Companion.loadFromNBT(nbt);
        }
    }

}
