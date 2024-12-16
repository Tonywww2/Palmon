package com.tonywww.item;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.DataKeys;
import com.tonywww.registeries.ModItems;
import com.tonywww.utils.StatsHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class LaborContract extends Item {
    public LaborContract(Properties arg) {
        super(arg);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (level instanceof ServerLevel serverLevel) {
            if (player instanceof ServerPlayer serverPlayer) {
                if (serverPlayer.isShiftKeyDown()) {
                    ItemStack itemStack = serverPlayer.getItemInHand(hand);
                    CompoundTag tag = itemStack.getTag();
                    if (tag != null) {
                        Pokemon pokemon = Pokemon.Companion.loadFromNBT(tag.getCompound("Pokemon"));
                        Cobblemon.INSTANCE.getStorage().getParty(serverPlayer).add(pokemon);
                        itemStack.setTag(new CompoundTag());
                        itemStack.shrink(1);

                        player.getInventory().placeItemBackInInventory(ModItems.EMPTY_CONTRACT.get().getDefaultInstance());

                    }

                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("text.palmon.labor_contract_not_shift"));

                }

            }
            player.getCooldowns().addCooldown(this, 10);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> toolTips, TooltipFlag flag) {
        CompoundTag nbt = itemStack.getTag();
        if (nbt != null) {
            String species = nbt.getCompound("Pokemon")
                    .getString(DataKeys.POKEMON_SPECIES_IDENTIFIER).replace(":", ".species.") + ".name";
            toolTips.add(Component.translatable(species));

            Pokemon pokemon = Pokemon.Companion.loadFromNBT(nbt.getCompound("Pokemon"));
            toolTips.add(Component.literal("Lv: ").append(String.valueOf(pokemon.getLevel())));

            toolTips.add(Component.translatable("cobblemon.ui.stats.ivs")
                    .append(" | ").append(Component.translatable("cobblemon.ui.stats.base")));
            IVs ivs = pokemon.getIvs();
            Map<Stat, Integer> base = pokemon.getForm().getBaseStats();
            toolTips.add(Component.translatable("cobblemon.ui.stats.hp").append(": ")
                    .append("§b" + ivs.get(Stats.HP) + "  |  §d" + base.get(Stats.HP)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.atk").append(": ")
                    .append("§b" + ivs.get(Stats.ATTACK) + "  |  §d" + base.get(Stats.ATTACK)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.def").append(": ")
                    .append("§b" + ivs.get(Stats.DEFENCE) + "  |  §d" + base.get(Stats.DEFENCE)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.sp_atk").append(": ")
                    .append("§b" + ivs.get(Stats.SPECIAL_ATTACK) + "  |  §d" + base.get(Stats.SPECIAL_ATTACK)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.sp_def").append(": ")
                    .append("§b" + ivs.get(Stats.SPECIAL_DEFENCE) + "  |  §d" + base.get(Stats.SPECIAL_DEFENCE)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.speed").append(": ")
                    .append("§b" + ivs.get(Stats.SPEED) + "  |  §d" + base.get(Stats.SPEED)));
        }
        super.appendHoverText(itemStack, level, toolTips, flag);
    }
}
