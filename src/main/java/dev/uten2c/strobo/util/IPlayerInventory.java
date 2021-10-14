package dev.uten2c.strobo.util;

import net.minecraft.item.ItemStack;

public interface IPlayerInventory {

    int canHold(ItemStack stack);
}
