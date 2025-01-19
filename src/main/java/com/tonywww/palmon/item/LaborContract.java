package com.tonywww.palmon.item;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.tonywww.palmon.Palmon;
import com.tonywww.palmon.curios.LaborContractCapProvider;
import com.tonywww.palmon.registeries.ModItems;
import com.tonywww.palmon.utils.PokemonNBTUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;
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

                        serverLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                serverPlayer.getX(),
                                serverPlayer.getY() + 0.5d,
                                serverPlayer.getZ(),
                                16,
                                0.5d,
                                0.5d,
                                0.5d,
                                0.1
                        );
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1f, 1f);

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
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
//        Palmon.getLogger().atDebug().log("PALMON_DEBUG: " + ModList.get().isLoaded("curios"));
        return ModList.get().isLoaded("curios") ? new LaborContractCapProvider(stack) : super.initCapabilities(stack, nbt);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> toolTips, TooltipFlag flag) {
        toolTips.add(Component.translatable("tooltip.palmon.labor_contract"));

        CompoundTag pokemonTag = getPokemonNBT(itemStack);
        if (!pokemonTag.isEmpty()) {
            Species species = PokemonNBTUtils.getSpeciesFromNBT(pokemonTag);
            toolTips.add(species.getTranslatedName().setStyle(species.getTranslatedName().getStyle().withColor(ChatFormatting.AQUA)));

            toolTips.add(Component.literal("Lv: " + PokemonNBTUtils.getLevelFromNBT(pokemonTag)));
            toolTips.add(Component.translatable("cobblemon.ui.info.type")
                    .append(": ").append(species.getPrimaryType().getDisplayName()).append(" ")
                    .append(species.getSecondaryType() == null ? Component.empty() : species.getSecondaryType().getDisplayName()));

            CompoundTag ivs = PokemonNBTUtils.getAllIVsFromNBT(pokemonTag);
            Map<Stat, Integer> base = species.getBaseStats();
            toolTips.add(Component.translatable("cobblemon.ui.stats.ivs")
                    .append(" | ").append(Component.translatable("cobblemon.ui.stats.base")));

            toolTips.add(Component.translatable("cobblemon.ui.stats.hp").append("  : ")
                    .append("§b" + PokemonNBTUtils.getIVFromNBT(ivs, Stats.HP) + "  |  §d" + base.get(Stats.HP)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.atk").append(": ")
                    .append("§b" + PokemonNBTUtils.getIVFromNBT(ivs, Stats.ATTACK) + "  |  §d" + base.get(Stats.ATTACK)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.def").append(": ")
                    .append("§b" + PokemonNBTUtils.getIVFromNBT(ivs, Stats.DEFENCE) + "  |  §d" + base.get(Stats.DEFENCE)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.sp_atk").append(": ")
                    .append("§b" + PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_ATTACK) + "  |  §d" + base.get(Stats.SPECIAL_ATTACK)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.sp_def").append(": ")
                    .append("§b" + PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE) + "  |  §d" + base.get(Stats.SPECIAL_DEFENCE)));
            toolTips.add(Component.translatable("cobblemon.ui.stats.speed").append(": ")
                    .append("§b" + PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPEED) + "  |  §d" + base.get(Stats.SPEED)));


            super.appendHoverText(itemStack, level, toolTips, flag);
        }
    }

    public static CompoundTag getPokemonNBT(ItemStack itemStack) {
        if (itemStack != null && !itemStack.isEmpty() &&
                itemStack.is(ModItems.LABOR_CONTRACT.get())) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null) {
                return nbt.getCompound("Pokemon");
            }
        }
        return null;
    }

}


