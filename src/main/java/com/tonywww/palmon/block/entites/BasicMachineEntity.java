package com.tonywww.palmon.block.entites;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BasicMachineEntity extends SyncedBlockEntity {

    protected int currentTick = 0;
    protected int tickPerOperation = 4;

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
     *
     */
    public void resetTicker() {
        this.currentTick = 0;

    }

}
