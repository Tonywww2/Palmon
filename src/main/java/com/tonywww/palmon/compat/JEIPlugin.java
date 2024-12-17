package com.tonywww.palmon.compat;

import com.tonywww.palmon.Palmon;
import mezz.jei.api.IModPlugin;
import net.minecraft.resources.ResourceLocation;

public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation PID = new ResourceLocation(Palmon.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PID;
    }
}
