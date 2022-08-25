package dev.uten2c.strobo.toast

import dev.uten2c.strobo.util.emptyText
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.AdvancementDisplay
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.ImpossibleCriterion
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

private val toastId = Identifier("strobo", "toast")

/**
 * プレイヤーにトーストを送信します
 */
fun ServerPlayerEntity.sendToast(toast: Toast) {
    val display = AdvancementDisplay(
        toast.icon,
        toast.title,
        emptyText(),
        null,
        toast.frame,
        true,
        false,
        false,
    )
    val advancement = Advancement(
        toastId,
        null,
        display,
        AdvancementRewards.NONE,
        mapOf(toastId.toString() to AdvancementCriterion(ImpossibleCriterion.Conditions())),
        arrayOf(arrayOf(toastId.toString())),
    )
    val progress = AdvancementProgress()
    progress.init(advancement.criteria, advancement.requirements)
    progress.getCriterionProgress(toastId.toString())?.obtain()
    val packet = AdvancementUpdateS2CPacket(
        false,
        listOf(advancement),
        setOf(toastId),
        mapOf(toastId to progress),
    )
    networkHandler.sendPacket(packet)
    val resetPacket = AdvancementUpdateS2CPacket(false, emptyList(), setOf(toastId), emptyMap())
    networkHandler.sendPacket(resetPacket)
}
