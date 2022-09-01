package test

import dev.uten2c.strobo.util.customModelData
import dev.uten2c.strobo.util.customName
import dev.uten2c.strobo.util.lore
import dev.uten2c.strobo.util.text
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.item.Items
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext

class ItemStackExtensionTest {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 1)
    fun customModelDataTest(context: TestContext) {
        context.addInstantFinalTask {
            val stack = Items.DIAMOND.defaultStack
            stack.customModelData = 10
            if (stack.customModelData != 10) {
                context.throwGameTestException("customModelDataの値が違う")
            }
            stack.customModelData = null
            if (stack.customModelData == null) {
                context.throwGameTestException("customModelDataがnullじゃない")
            }
        }
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 1)
    fun loreTest(context: TestContext) {
        val lore = listOf(text("test"), text("hello"))
        context.addInstantFinalTask {
            val stack = Items.DIAMOND.defaultStack
            stack.lore = lore
            if (stack.lore != lore) {
                context.throwGameTestException("loreの値が違う")
            }
            stack.lore = null
            if (stack.lore == null) {
                context.throwGameTestException("loreがnullじゃない")
            }
        }
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 1)
    fun customNameTest(context: TestContext) {
        context.addInstantFinalTask {
            val stack = Items.DIAMOND.defaultStack
            if (stack.customName != null) {
                context.throwGameTestException("customNameがnullじゃない")
            }
            val testName = text("test")
            stack.customName = testName
            if (stack.customName != testName) {
                context.throwGameTestException("customNameがうまく設定されてない。stack: ${stack.customName}, text: $testName")
            }
            stack.customName = null
            if (stack.customName != null) {
                context.throwGameTestException("customNameにnullを入れられてない")
            }
        }
    }
}
