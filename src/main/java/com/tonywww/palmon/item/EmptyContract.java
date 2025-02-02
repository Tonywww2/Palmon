package com.tonywww.palmon.item;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.registeries.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class EmptyContract extends Item {
    public static final DustParticleOptions PARTICLE = new DustParticleOptions(new Vector3f(1f, 1f, 1f), 2.0F);

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

                    serverLevel.sendParticles(
                            PARTICLE,
                            pokemon.getX(),
                            pokemon.getY() + 0.5d,
                            pokemon.getZ(),
                            16,
                            0.5d,
                            0.5d,
                            0.5d,
                            0.1d
                    );
                    serverLevel.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_TRADE, SoundSource.BLOCKS, 1f, 1f);

                    player.getCooldowns().addCooldown(this, 10);
                    player.getCooldowns().addCooldown(ModItems.LABOR_CONTRACT.get(), 10);

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

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> toolTips, TooltipFlag flag) {

        toolTips.add(Component.translatable("tooltip.palmon.empty_contract"));

        super.appendHoverText(itemStack, level, toolTips, flag);
    }

}
