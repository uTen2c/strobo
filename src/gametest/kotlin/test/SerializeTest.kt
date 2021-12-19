package test

import dev.uten2c.strobo.util.Location
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext

class SerializeTest {

    @GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
    fun locationSerialize(context: TestContext) {
        context.addInstantFinalTask {
            val decoded = Json.decodeFromString<Location>(SERIALIZED)
            if (DESERIALIZED != decoded) {
                context.throwGameTestException("ちがう1")
            }
            val encoded = Json.encodeToString(DESERIALIZED)
            if (SERIALIZED != encoded) {
                context.throwGameTestException("ちがう2")
            }
        }
    }

    companion object {
        private const val SERIALIZED = """{"x":0.5,"y":0.5,"z":0.5,"yaw":0.5,"pitch":0.5}"""
        private val DESERIALIZED = Location(0.5, 0.5, 0.5, 0.5f, 0.5f)
    }
}
