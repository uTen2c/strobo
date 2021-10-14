package dev.uten2c.strobo.mixin.impl;

import dev.uten2c.strobo.util.IPlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory implements IPlayerInventory {

    private static final int MAX_STACK = 64;

    @Shadow
    @Final
    public DefaultedList<ItemStack> main;

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Shadow
    protected abstract boolean canStackAddMore(ItemStack existingStack, ItemStack stack);

    @Shadow
    @Final
    public DefaultedList<ItemStack> armor;

    @Override
    public int canHold(ItemStack stack) {
        var remains = stack.getCount();
        for (int i = 0; i < main.size(); i++) {
            var stack1 = getStack(i);
            if (stack1.isEmpty()) return stack.getCount();

            if (canStackAddMore(stack1, stack)) {
                remains -= Math.min(stack1.getMaxCount(), MAX_STACK) - stack1.getCount();
            }
            if (remains <= 0) return stack.getCount();
        }
        var offhandItemStack = getStack(main.size() + armor.size());
        if (canStackAddMore(offhandItemStack, stack)) {
            remains -= Math.min(offhandItemStack.getMaxCount(), MAX_STACK) - offhandItemStack.getCount();
        }
        if (remains <= 0) return stack.getCount();

        return stack.getCount() - remains;
    }
}
