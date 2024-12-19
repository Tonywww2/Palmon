package com.tonywww.palmon.api;

import com.cobblemon.mod.common.api.types.ElementalType;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ProductionInput extends RecipeWrapper {

    private final int level;

    private ElementalType type;

    private int evHP;
    private int evATK;
    private int evDEF;
    private  int evSPA;
    private int evSPD;
    private int evSPE;

    public ProductionInput(IItemHandlerModifiable inv, int level, ElementalType type,
                           int evHP, int evATK, int evDEF, int evSPA, int evSPD, int evSPE) {
        super(inv);
        this.level = level;
        this.type = type;
        this.evHP = evHP;
        this.evATK = evATK;
        this.evDEF = evDEF;
        this.evSPA = evSPA;
        this.evSPD = evSPD;
        this.evSPE = evSPE;
    }

    public int getLevel() {
        return level;
    }

    public int getEVHP() {
        return evHP;
    }

    public int getEVATK() {
        return evATK;
    }

    public int getEVDEF() {
        return evDEF;
    }

    public int getEVSPD() {
        return evSPD;
    }

    public int getEVSPA() {
        return evSPA;
    }

    public int getEVSPE() {
        return evSPE;
    }

    public ElementalType getType() {
        return type;
    }

    public void setType(ElementalType type) {
        this.type = type;
    }
}
