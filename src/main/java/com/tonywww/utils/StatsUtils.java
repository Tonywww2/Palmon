package com.tonywww.utils;

import com.cobblemon.mod.common.util.DataKeys;
import net.minecraft.nbt.CompoundTag;

public class StatsUtils {

    public static int getIVFromNBT(CompoundTag nbt, String tag) {
        return nbt.getCompound(DataKeys.POKEMON_IVS).getShort(tag);

    }

}
