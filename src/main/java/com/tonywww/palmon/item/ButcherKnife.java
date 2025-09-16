package com.tonywww.palmon.item;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.tonywww.palmon.registeries.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ButcherKnife extends Item {
    public ButcherKnife(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level() instanceof ServerLevel serverLevel &&
                entity instanceof PokemonEntity pokemon &&
        !player.getCooldowns().isOnCooldown(this)) {
            if (pokemon.getOwnerUUID() == null && pokemon.getHealth() > 0) {
                ItemStack food = new ItemStack(ModItems.POKE_FOOD.get(), 1 + (pokemon.labelLevel() / 8));
                ItemEntity itemEntity = new ItemEntity(serverLevel, pokemon.getX(), pokemon.getY(), pokemon.getZ(), food);
                serverLevel.addFreshEntity(itemEntity);
                player.getCooldowns().addCooldown(this, 40);
                pokemon.kill();

            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
