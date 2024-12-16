package com.tonywww.item;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.tonywww.Palmon;
import com.tonywww.registeries.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EmptyContract extends Item {
    public EmptyContract(Properties arg) {
        super(arg);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (livingEntity instanceof PokemonEntity pokemon) {
            Level world = player.level();
            if (world instanceof ServerLevel serverLevel) {
                if (pokemon.getOwnerUUID() == null) {
                    player.sendSystemMessage(Component.translatable("text.palmon.contract_on_wild"));
                } else if (!pokemon.getOwnerUUID().equals(player.getUUID())) {
                    player.sendSystemMessage(Component.translatable("text.palmon.contract_on_other"));

                } else {
                    // all passed
                    ItemStack result = pokemonToStack(ModItems.LABOR_CONTRACT.get().getDefaultInstance(), pokemon);
                    if (!player.isCreative()) itemStack.shrink(1);

                    player.getInventory().placeItemBackInInventory(result);
                    player.getCooldowns().addCooldown(this, 10);

                }
            }

        }
        return super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }

    private ItemStack pokemonToStack(ItemStack itemStack, PokemonEntity entity) {
        Pokemon pokemon = entity.getPokemon();
        ServerPlayer player = pokemon.getOwnerPlayer();
        CompoundTag tag = itemStack.getOrCreateTag();

        entity.discard();
        tag.put("Pokemon", pokemon.saveToNBT(new CompoundTag()));

        if (player == null) {
            return itemStack;
        }
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
        if (!party.remove(pokemon)) {
            try {
                Cobblemon.INSTANCE.getStorage().getPC(player.getUUID()).remove(pokemon);
            } catch (NoPokemonStoreException e) {
                Palmon.getLogger().debug(e);

            }
        }

        return itemStack;

    }

}
