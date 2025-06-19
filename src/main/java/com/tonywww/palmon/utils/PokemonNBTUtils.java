package com.tonywww.palmon.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.DataKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class PokemonNBTUtils {

    public static Species getSpeciesFromNBT(CompoundTag nbt) {
        String rawID = nbt.getString(DataKeys.POKEMON_SPECIES_IDENTIFIER).replace("pokemonCobblemon", "cobblemon");
        return PokemonSpecies.INSTANCE.getByIdentifier(new ResourceLocation(rawID));
    }

    public static short getLevelFromNBT(CompoundTag nbt) {
        return nbt.getShort(DataKeys.POKEMON_LEVEL);
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

    public static ElementalType getType1FromSpecies(Species species) {
        return species.getPrimaryType();
    }

    public static ElementalType getType2FromSpecies(Species species) {
        return species.getSecondaryType();
    }

}
