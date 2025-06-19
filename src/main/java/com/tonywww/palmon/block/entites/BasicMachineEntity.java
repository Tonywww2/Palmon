package com.tonywww.palmon.block.entites;

import com.tonywww.palmon.block.BoostFrame;
import com.tonywww.palmon.utils.PokemonNBTUtils;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;

public class BasicMachineEntity extends SyncedBlockEntity {

    protected int executeTick = 0;
    protected int tickPerOperation = 4;

    protected final int FOOD_PER_WORKING_TICK = 4;
    protected final double FOOD_CONSUME_CHANCE = 0.05;

    // 通用 multiplier 相关常量
    public static final double ACCURACY = 100d;
    public static final int RADIUS = 2;
    public static final int HEIGHT = 4;

    protected double boostMultiplier = 1.0;
    protected double levelMultiplier = 1.0;
    protected double individualMultiplier = 1.0;
    protected double shinyMultiplier = 1.0;
    protected double focusMultiplier = 0.0;
    protected double efficiency = 0.0;

    protected double currentTick = 0.0;
    protected double targetTick = 0.0;

    public BasicMachineEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /**
     * Run on every tick
     *
     * @param tick
     */
    public void tickBase(int tick) {
        this.executeTick += tick;

    }

    /**
     * Return if is the working tick
     *
     * @return
     */
    public boolean isWorkingTick() {
        return this.executeTick >= this.tickPerOperation;
    }

    /**
     * Reset the ticker after a working tick
     */
    public void resetTicker() {
        this.executeTick = 0;

    }

    private WorkingStationEntity getWorkingStation() {
        int x = 0, z = 0;
        switch (this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH:
                z--;
                break;

            case EAST:
                x++;
                break;

            case SOUTH:
                z++;
                break;

            case WEST:
                x--;
                break;
        }
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(x, 0, z));
            if (blockEntity instanceof WorkingStationEntity workingStationEntity) {
                return workingStationEntity;
            }
        }
        return null;
    }

    public CompoundTag getPokemonNBT() {
        WorkingStationEntity workingStation = this.getWorkingStation();
        if (workingStation != null) {
            return workingStation.getPokemonNBT();
        }

        return null;
    }

    public int getFood() {
        WorkingStationEntity workingStation = this.getWorkingStation();
        if (workingStation != null) {
            return workingStation.food;
        }
        return 0;
    }

    public void setFood(int i) {
        WorkingStationEntity workingStation = this.getWorkingStation();
        if (workingStation != null) {
            workingStation.food = i;

        }
    }

    // 通用 multiplier 方法
    public double getBoostMultiplier() {
        BlockState state = null;
        if (this.level != null) {
            state = this.level.getBlockState(this.getBlockPos().above());
        }
        if (state != null && state.getBlock() instanceof BoostFrame boostFrame) {
            return boostFrame.efficiency;
        }
        return 1.0d;
    }

    public double getLevelMultiplier(int level) {
        return (0.00015d * level * level) + 1;
    }

    public double getFocusMultiplier(int ev, int iv) {
        return Math.max(1, (Math.pow(ev, 3) + (1000d * Math.pow(iv - 1, 3))) / 4000000d);
    }

    public double getIndividualMultiplier(CompoundTag ivs) {
        int sumIvs = PokemonNBTUtils.getIVFromNBT(ivs, Stats.HP) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.ATTACK) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.DEFENCE) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_ATTACK) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPECIAL_DEFENCE) +
                PokemonNBTUtils.getIVFromNBT(ivs, Stats.SPEED);

        return Math.max(1, (Math.pow(sumIvs - 5, 3) / 1000000d) + 0.3d);
    }

    /**
     * 通用 multiplier 更新方法，适用于 Processing/Production 等子类
     */
    protected void updateMultipliers(
            CompoundTag pokemonNBT,
            CompoundTag ivs,
            boolean hasRecipe,
            int focusEv,
            int focusIv,
            double tick
    ) {
        this.boostMultiplier = getBoostMultiplier();
        this.levelMultiplier = getLevelMultiplier(PokemonNBTUtils.getLevelFromNBT(pokemonNBT));
        this.individualMultiplier = getIndividualMultiplier(ivs);

        this.shinyMultiplier = PokemonNBTUtils.getShinyFromNBT(pokemonNBT) ? 2.0d : 1.0d;

        if (hasRecipe) {
            this.focusMultiplier = getFocusMultiplier(focusEv, focusIv);
            this.efficiency = boostMultiplier * levelMultiplier * individualMultiplier * focusMultiplier * shinyMultiplier;
            this.targetTick = tick;
        } else {
            this.focusMultiplier = 0;
            this.efficiency = 0;
            this.targetTick = 0;
        }
    }

    public ItemStackHandler getAreaBlocks(Block thisBlock) {
        HashMap<Block, Integer> map = new HashMap<>();
        if (this.getLevel() == null) return null;
        for (int i = 0; i <= HEIGHT; i++) {
            for (int j = -RADIUS; j <= RADIUS; j++) {
                for (int k = -RADIUS; k <= RADIUS; k++) {
                    if (i == 0 && j == 0 && k == 0) continue;
                    Block cur = this.getLevel().getBlockState(this.getBlockPos().offset(j, i, k)).getBlock();

                    if (cur.equals(thisBlock)) return null;

                    map.put(cur, map.getOrDefault(cur, 0) + 1);
                }
            }
        }

        ItemStackHandler stackHandler = new ItemStackHandler(map.size());
        int i = 0;
        for (Block key : map.keySet()) {
            stackHandler.setStackInSlot(i++, new ItemStack(key, map.get(key)));
        }
        return stackHandler;
    }

    // 掉落物获取
    public NonNullList<ItemStack> getDroppableInventory(ItemStackHandler handler) {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < handler.getSlots(); ++i) {
            drops.add(handler.getStackInSlot(i));
        }
        return drops;
    }

}
