package dev.uten2c.strobo.toast

import net.minecraft.advancement.AdvancementFrame
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

data class Toast(val title: Text, val frame: AdvancementFrame, val icon: ItemStack)
