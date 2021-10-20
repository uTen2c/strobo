package test

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.entity.Entity
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkSide
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.test.TestContext
import net.minecraft.util.math.BlockPos
import java.util.*

abstract class StroboGameTest : FabricGameTest {

    protected fun TestContext.createMockServerPlayer(): ServerPlayerEntity {
        return object : ServerPlayerEntity(world.server, world, GameProfile(UUID.randomUUID(), "test-mock-player")) {

            init {
                networkHandler = ServerPlayNetworkHandler(world.server, ClientConnection(NetworkSide.SERVERBOUND), this)
                setPos(0.0, 0.0, 0.0)
            }

            override fun isSpectator(): Boolean {
                return false
            }

            override fun isCreative(): Boolean {
                return true
            }
        }
    }

    protected fun TestContext.wrappedFail(message: String) {
        waitAndRun(0) {
            throwGameTestException(message)
        }
    }

    protected fun TestContext.wrappedFail(message: String, pos: BlockPos) {
        waitAndRun(0) {
            throwPositionedException(message, pos)
        }
    }

    protected fun TestContext.wrappedFail(message: String, entity: Entity) {
        waitAndRun(0) {
            throwPositionedException(message, entity)
        }
    }
}
