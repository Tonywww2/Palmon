package com.tonywww.palmon.api;

import com.cobblemon.mod.common.api.types.ElementalType;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ProductionInput extends RecipeWrapper {

    private final int level;

    private ElementalType type;

    private int baseHP;
    private int baseATK;
    private int baseDEF;
    private int baseSPA;
    private int baseSPD;
    private int baseSPE;

    public ProductionInput(IItemHandlerModifiable inv, int level, ElementalType type,
                           int baseHP, int baseATK, int baseDEF, int baseSPA, int baseSPD, int baseSPE) {
        super(inv);
        this.level = level;
        this.type = type;
        this.baseHP = baseHP;
        this.baseATK = baseATK;
        this.baseDEF = baseDEF;
        this.baseSPA = baseSPA;
        this.baseSPD = baseSPD;
        this.baseSPE = baseSPE;
    }

    public int getLevel() {
        return level;
    }

    public ElementalType getType() {
        return type;
    }

    public void setType(ElementalType type) {
        this.type = type;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseATK() {
        return baseATK;
    }

    public int getBaseDEF() {
        return baseDEF;
    }

    public int getBaseSPA() {
        return baseSPA;
    }

    public int getBaseSPD() {
        return baseSPD;
    }

    public int getBaseSPE() {
        return baseSPE;
    }
}
