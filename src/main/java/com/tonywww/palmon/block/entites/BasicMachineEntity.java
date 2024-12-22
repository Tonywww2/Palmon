package com.tonywww.palmon.block.entites;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BasicMachineEntity extends SyncedBlockEntity {

    protected int currentTick = 0;
    protected int tickPerOperation = 4;

    protected final int FOOD_PER_WORKING_TICK = 5;
    protected final double FOOD_CONSUME_CHANCE = 0.2;

    public BasicMachineEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /**
     * Run on every tick
     *
     * @param tick
     */
    public void tick(int tick) {
        this.currentTick += tick;

    }

    /**
     * Return if is the working tick
     *
     * @return
     */
    public boolean isWorkingTick() {
        return this.currentTick >= this.tickPerOperation;
    }

    /**
     * Reset the ticker after a working tick
     */
    public void resetTicker() {
        this.currentTick = 0;

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

}
