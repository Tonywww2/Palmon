package com.tonywww.palmon;
import net.minecraftforge.common.ForgeConfigSpec;

public final class PalmonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeHpMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeAttackMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeDefenceMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeSpAttackMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeSpDefenceMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeSpeedMultiplier;

    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeSpAttackMultiplierConfluence;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeSpDefenceMultiplierConfluence;
    public static final ForgeConfigSpec.ConfigValue<Double> contractAttributeSpeedMultiplierConfluence;

    static {
        contractAttributeHpMultiplier = BUILDER.comment("\nThe final multiplier of the HP.")
                .defineInRange("contractAttributeHpMultiplier", 0.5d, 0d, 1000.0d);

        contractAttributeAttackMultiplier = BUILDER.comment("\nThe final multiplier of the ATTACK.")
                .defineInRange("contractAttributeAttackMultiplier", 0.5d, 0d, 1000.0d);

        contractAttributeDefenceMultiplier = BUILDER.comment("\nThe final multiplier of the DEFENCE.")
                .defineInRange("contractAttributeDefenceMultiplier", 0.75d, 0d, 1000.0d);

        contractAttributeSpAttackMultiplier = BUILDER.comment("\nThe final multiplier of the SP ATTACK.")
                .defineInRange("contractAttributeSpAttackMultiplier", 1.0d, 0d, 1000.0d);

        contractAttributeSpDefenceMultiplier = BUILDER.comment("\nThe final multiplier of the SP DEFENCE.")
                .defineInRange("contractAttributeSpDefenceMultiplier", 0.75d, 0d, 1000.0d);

        contractAttributeSpeedMultiplier = BUILDER.comment("\nThe final multiplier of the SPEED.")
                .defineInRange("contractAttributeSpeedMultiplier", 0.75d, 0d, 1000.0d);

        contractAttributeSpAttackMultiplierConfluence = BUILDER.comment("\nThe final multiplier of the SP ATTACK with mod Confluence loaded.")
                .defineInRange("contractAttributeSpAttackMultiplierConfluence", 8.0d, 0d, 1000.0d);

        contractAttributeSpDefenceMultiplierConfluence = BUILDER.comment("\nThe final multiplier of the SP DEFENCE with mod Confluence loaded.")
                .defineInRange("contractAttributeSpDefenceMultiplierConfluence", 0.75d, 0d, 1000.0d);

        contractAttributeSpeedMultiplierConfluence = BUILDER.comment("\nThe final multiplier of the SPEED with mod Confluence loaded.")
                .defineInRange("contractAttributeSpeedMultiplierConfluence", 0.1d, 0d, 1000.0d);



        COMMON_CONFIG = BUILDER.build();
    }

}
