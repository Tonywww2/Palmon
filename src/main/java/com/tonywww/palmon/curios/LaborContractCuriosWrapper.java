package com.tonywww.palmon.curios;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.common.collect.Multimap;
import com.tonywww.palmon.item.LaborContract;
import com.tonywww.palmon.registeries.ModItems;
import com.tonywww.palmon.utils.CuriosUtils;
import com.tonywww.palmon.utils.PokemonNBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import com.google.common.collect.LinkedHashMultimap;

import java.util.UUID;

import com.tonywww.palmon.PalmonConfig;

public class LaborContractCuriosWrapper implements ICurio {

    private final ItemStack stack;

    public LaborContractCuriosWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public SoundInfo getEquipSound(SlotContext slotContext) {
        return new SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return false;
    }

    @Override
    public boolean canEquip(SlotContext slotContext) {
        return CuriosUtils.noSameCurio(slotContext.entity(),
                itemStack -> itemStack.is(ModItems.LABOR_CONTRACT.get()));
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        ICurio.super.curioTick(slotContext);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();
        if (!slotContext.identifier().equalsIgnoreCase("contract"))
            return atts;
        CompoundTag pokemonTag = LaborContract.getPokemonNBT(this.getStack());
        if (pokemonTag == null) return atts;

        Species species = PokemonNBTUtils.getSpeciesFromNBT(pokemonTag);
        CompoundTag ivs = PokemonNBTUtils.getAllIVsFromNBT(pokemonTag);

        atts.put(Attributes.MAX_HEALTH,
                new AttributeModifier(uuid, "contract_max_health",
                        CuriosUtils.computeMultiplier(
                                PokemonNBTUtils.getIVFromNBT(ivs, Stats.HP),
                                species.getBaseStats().get(Stats.HP),
                                PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                PalmonConfig.contractAttributeHpMultiplier.get()
                        ),
                        AttributeModifier.Operation.MULTIPLY_TOTAL));

        atts.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(uuid, "contract_attack_damage",
                        CuriosUtils.computeMultiplier(
                                PokemonNBTUtils.getIVFromNBT(ivs, Stats.ATTACK),
                                species.getBaseStats().get(Stats.ATTACK),
                                PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                PalmonConfig.contractAttributeAttackMultiplier.get()
                        ),
                        AttributeModifier.Operation.MULTIPLY_TOTAL));

        atts.put(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(uuid, "contract_armor_toughness",
                        CuriosUtils.computeMultiplier(
                                PokemonNBTUtils.getIVFromNBT(ivs, Stats.DEFENCE),
                                species.getBaseStats().get(Stats.DEFENCE),
                                PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                PalmonConfig.contractAttributeDefenceMultiplier.get()
                        ),
                        AttributeModifier.Operation.MULTIPLY_TOTAL));

        if (ModList.get().isLoaded("confluence")) {
            atts.put(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("confluence:armor_pass")),
                    new AttributeModifier(uuid, "contract_armor_pass",
                            CuriosUtils.computeMultiplier(
                                    PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_ATTACK),
                                    species.getBaseStats().get(Stats.SPECIAL_ATTACK),
                                    PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                    PalmonConfig.contractAttributeSpAttackMultiplierConfluence.get()
                            ),
                            AttributeModifier.Operation.ADDITION));

            atts.put(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("confluence:magic_damage")),
                    new AttributeModifier(uuid, "contract_magic_damage",
                            CuriosUtils.computeMultiplier(
                                    PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE),
                                    species.getBaseStats().get(Stats.SPECIAL_DEFENCE),
                                    PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                    PalmonConfig.contractAttributeSpDefenceMultiplierConfluence.get()
                            ),
                            AttributeModifier.Operation.MULTIPLY_TOTAL));

            atts.put(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("confluence:dodge_chance")),
                    new AttributeModifier(uuid, "contract_dodge_chance",
                            CuriosUtils.computeMultiplier(
                                    PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE),
                                    species.getBaseStats().get(Stats.SPECIAL_DEFENCE),
                                    PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                    PalmonConfig.contractAttributeSpeedMultiplierConfluence.get()
                            ),
                            AttributeModifier.Operation.ADDITION));

        } else {
            atts.put(Attributes.ATTACK_KNOCKBACK,
                    new AttributeModifier(uuid, "contract_attack_knockback",
                            CuriosUtils.computeMultiplier(
                                    PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_ATTACK),
                                    species.getBaseStats().get(Stats.SPECIAL_ATTACK),
                                    PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                    PalmonConfig.contractAttributeSpAttackMultiplier.get()
                            ),
                            AttributeModifier.Operation.MULTIPLY_TOTAL));

            atts.put(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(uuid, "contract_knockback_resistance",
                            CuriosUtils.computeMultiplier(
                                    PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE),
                                    species.getBaseStats().get(Stats.SPECIAL_DEFENCE),
                                    PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                    PalmonConfig.contractAttributeSpDefenceMultiplier.get()
                            ),
                            AttributeModifier.Operation.ADDITION));

            atts.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(uuid, "contract_speed",
                            CuriosUtils.computeMultiplier(
                                    PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE),
                                    species.getBaseStats().get(Stats.SPECIAL_DEFENCE),
                                    PokemonNBTUtils.getLevelFromNBT(pokemonTag),
                                    PalmonConfig.contractAttributeSpeedMultiplier.get()
                            ),
                            AttributeModifier.Operation.ADDITION));
        }


        return atts;
    }
}
