package com.tonywww.palmon.utils;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer;
import com.cobblemon.mod.common.util.DataKeys;
import com.cobblemon.mod.common.api.abilities.Abilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ClientPokemon extends Pokemon {

    public ClientPokemon() {
        super();
    }

    @Override
    public void attemptAbilityUpdate() {
        return;
    }

    @Override
    public @NotNull Ability rollAbility() {
        return Abilities.INSTANCE.getDUMMY().create(false);
    }

    public ClientPokemon fromNBT(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) {
            return this;
        }

        if (nbt.hasUUID(DataKeys.POKEMON_UUID)) {
            this.setUuid(nbt.getUUID(DataKeys.POKEMON_UUID));
        }

        // Species
        if (nbt.contains(DataKeys.POKEMON_SPECIES_IDENTIFIER)) {
            String rawID = nbt.getString(DataKeys.POKEMON_SPECIES_IDENTIFIER).replace("pokemonCobblemon", "cobblemon");
            Species species = PokemonSpecies.INSTANCE.getByIdentifier(new ResourceLocation(rawID));
            if (species != null) {
                this.setSpecies(species);
            }
        }

        // Form
        if (nbt.contains(DataKeys.POKEMON_FORM_ID)) {
            String formId = nbt.getString(DataKeys.POKEMON_FORM_ID);
            FormData form = this.getSpecies().getForms().stream()
                    .filter(f -> f.formOnlyShowdownId().equals(formId))
                    .findFirst()
                    .orElse(this.getSpecies().getStandardForm());
            this.setForm(form);
        }

        // Basic stats
        if (nbt.contains(DataKeys.POKEMON_LEVEL)) {
            this.setLevel(nbt.getShort(DataKeys.POKEMON_LEVEL));
        }

        if (nbt.contains(DataKeys.POKEMON_EXPERIENCE)) {
            this.setExperience$common(nbt.getInt(DataKeys.POKEMON_EXPERIENCE));
        }

        if (nbt.contains(DataKeys.POKEMON_FRIENDSHIP)) {
            this.setFriendship(nbt.getShort(DataKeys.POKEMON_FRIENDSHIP), true);
        }

        if (nbt.contains(DataKeys.POKEMON_HEALTH)) {
            this.setCurrentHealth(nbt.getShort(DataKeys.POKEMON_HEALTH));
        }

        // Nickname
        if (nbt.contains(DataKeys.POKEMON_NICKNAME)) {
            String nicknameJson = nbt.getString(DataKeys.POKEMON_NICKNAME);
            if (!nicknameJson.isEmpty()) {
                this.setNickname(Component.Serializer.fromJson(nicknameJson));
            }
        }

        // Gender
        if (nbt.contains(DataKeys.POKEMON_GENDER)) {
            String genderStr = nbt.getString(DataKeys.POKEMON_GENDER);
            if (!genderStr.isEmpty()) {
                this.setGender(Gender.valueOf(genderStr.toUpperCase()));
            }
        }

        // Shiny
        if (nbt.contains(DataKeys.POKEMON_SHINY)) {
            this.setShiny(nbt.getBoolean(DataKeys.POKEMON_SHINY));
        }

        // Scale modifier
        if (nbt.contains(DataKeys.POKEMON_SCALE_MODIFIER)) {
            this.setScaleModifier(nbt.getFloat(DataKeys.POKEMON_SCALE_MODIFIER));
        }

        // IVs
        if (nbt.contains(DataKeys.POKEMON_IVS)) {
            CompoundTag ivs = nbt.getCompound(DataKeys.POKEMON_IVS);
            this.getIvs().set(Stats.HP, ivs.getShort(Stats.HP.getIdentifier().getPath()));
            this.getIvs().set(Stats.ATTACK, ivs.getShort(Stats.ATTACK.getIdentifier().getPath()));
            this.getIvs().set(Stats.DEFENCE, ivs.getShort(Stats.DEFENCE.getIdentifier().getPath()));
            this.getIvs().set(Stats.SPECIAL_ATTACK, ivs.getShort(Stats.SPECIAL_ATTACK.getIdentifier().getPath()));
            this.getIvs().set(Stats.SPECIAL_DEFENCE, ivs.getShort(Stats.SPECIAL_DEFENCE.getIdentifier().getPath()));
            this.getIvs().set(Stats.SPEED, ivs.getShort(Stats.SPEED.getIdentifier().getPath()));
        }

        // EVs
        if (nbt.contains(DataKeys.POKEMON_EVS)) {
            CompoundTag evs = nbt.getCompound(DataKeys.POKEMON_EVS);
            this.getEvs().loadFromNBT(evs);
        }

        // Moveset
        this.getMoveSet().loadFromNBT(nbt);

        // Nature
        if (nbt.contains(DataKeys.POKEMON_NATURE)) {
            String natureStr = nbt.getString(DataKeys.POKEMON_NATURE);
            if (!natureStr.isEmpty()) {
                Nature nature = Natures.INSTANCE.getNature(new ResourceLocation(natureStr));
                if (nature != null) {
                    this.setNature(nature);
                }
            }
        }

        // Minted nature
        if (nbt.contains(DataKeys.POKEMON_MINTED_NATURE)) {
            String mintedNatureStr = nbt.getString(DataKeys.POKEMON_MINTED_NATURE);
            if (!mintedNatureStr.isEmpty()) {
                this.setMintedNature(Natures.INSTANCE.getNature(new ResourceLocation(mintedNatureStr)));
            }
        }

        // Status effects
        if (nbt.contains(DataKeys.POKEMON_STATUS)) {
            CompoundTag statusNBT = nbt.getCompound(DataKeys.POKEMON_STATUS);
            this.setStatus(PersistentStatusContainer.Companion.loadFromNBT(statusNBT));
        }

        // Timers
        if (nbt.contains(DataKeys.POKEMON_FAINTED_TIMER)) {
            this.setFaintedTimer(nbt.getInt(DataKeys.POKEMON_FAINTED_TIMER));
        }

        if (nbt.contains(DataKeys.POKEMON_HEALING_TIMER)) {
            this.setHealTimer(nbt.getInt(DataKeys.POKEMON_HEALING_TIMER));
        }

        // Pokeball
        if (nbt.contains(DataKeys.POKEMON_CAUGHT_BALL)) {
            PokeBall ball = PokeBalls.INSTANCE.getPokeBall(new ResourceLocation(nbt.getString(DataKeys.POKEMON_CAUGHT_BALL)));
            if (ball != null) {
                this.setCaughtBall(ball);
            }
        }

        // Persistent data - 修复方法调用
        if (nbt.contains(DataKeys.POKEMON_PERSISTENT_DATA)) {
            CompoundTag persistentData = nbt.getCompound(DataKeys.POKEMON_PERSISTENT_DATA);
            this.getPersistentData().merge(persistentData);
        }

        // Dynamax
        if (nbt.contains(DataKeys.POKEMON_DMAX_LEVEL)) {
            this.setDmaxLevel(nbt.getInt(DataKeys.POKEMON_DMAX_LEVEL));
        }

        if (nbt.contains(DataKeys.POKEMON_GMAX_FACTOR)) {
            this.setGmaxFactor(nbt.getBoolean(DataKeys.POKEMON_GMAX_FACTOR));
        }

        // Trading
        if (nbt.contains(DataKeys.POKEMON_TRADEABLE)) {
            this.setTradeable(nbt.getBoolean(DataKeys.POKEMON_TRADEABLE));
        }

        // Original trainer - 移除不存在的方法调用
        if (nbt.contains(DataKeys.POKEMON_ORIGINAL_TRAINER)) {
            this.setOriginalTrainer(nbt.getString(DataKeys.POKEMON_ORIGINAL_TRAINER));
        }

        // Update aspects and form
        this.updateAspects();

        return this;
    }

    public static ClientPokemon createFromNBT(CompoundTag nbt) {
        ClientPokemon pokemon = new ClientPokemon();
        return pokemon.fromNBT(nbt);
    }
}
