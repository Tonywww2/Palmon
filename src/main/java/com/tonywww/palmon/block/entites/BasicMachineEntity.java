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
     * @param be
     * @param tick
     */
    public static void tick(BasicMachineEntity be, int tick) {
        be.currentTick += tick;

    }

    /**
     * Return if is the working tick
     *
     * @param be
     * @return
     */
    public static boolean isWorkingTick(BasicMachineEntity be) {
        return be.currentTick >= be.tickPerOperation;
    }

    /**
     * Reset the ticker after a working tick
     *
     * @param be
     */
    public static void resetTicker(BasicMachineEntity be) {
        be.currentTick = 0;

    }

}
