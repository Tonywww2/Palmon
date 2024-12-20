package com.tonywww.palmon.api;

import net.minecraftforge.energy.EnergyStorage;

public class IEnergyStorage extends EnergyStorage {
    public IEnergyStorage(int capacity) {
        super(capacity);
    }

    public void setEnergyStored(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }
}
