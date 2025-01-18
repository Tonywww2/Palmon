package com.tonywww.palmon.curios;

import com.google.common.collect.Multimap;
import com.tonywww.palmon.registeries.ModItems;
import com.tonywww.palmon.utils.CuriosUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.UUID;


public class LaborContractCuriosWrapper implements ICurio {

    private final ItemStack stack;

    public LaborContractCuriosWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public SoundInfo getEquipSound(SlotContext slotContext) {
        return new SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return canEquip(slotContext);
    }

    @Override
    public boolean canEquip(SlotContext slotContext) {
        return CuriosUtils.noSameCurio(slotContext.entity(),
                itemStack -> itemStack.is(ModItems.LABOR_CONTRACT.get()));
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void curioTick(SlotContext slotContext) {
        ICurio.super.curioTick(slotContext);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        return ICurio.super.getAttributeModifiers(slotContext, uuid);
    }
}
