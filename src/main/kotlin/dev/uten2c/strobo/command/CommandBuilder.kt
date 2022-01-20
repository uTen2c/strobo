package dev.uten2c.strobo.command

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.argument.*
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument as arg

private typealias Child = CommandBuilder.() -> Unit

@Suppress("unused")
class CommandBuilder(private val builder: ArgumentBuilder<ServerCommandSource, *>) {

    /**
     * コマンド実行に必要とするOPレベルを設定する
     * @param opLevel op level
     */
    fun requires(opLevel: Int) {
        builder.requires { it.hasPermissionLevel(opLevel) }
    }

    /**
     * コマンド実行に必要な条件を設定する
     * @param filter フィルター処理
     */
    fun requires(filter: (ServerCommandSource) -> Boolean) {
        builder.requires { filter(it) }
    }

    fun literal(literal: String, child: Child) = next(literal(literal), child)
    fun angle(name: String, child: Child) = next(arg(name, AngleArgumentType.angle()), child)
    fun blockPos(name: String, child: Child) = next(arg(name, BlockPosArgumentType.blockPos()), child)
    fun blockPredicate(name: String, child: Child) = next(arg(name, BlockPredicateArgumentType.blockPredicate()), child)
    fun blockStateArg(name: String, child: Child) = next(arg(name, BlockStateArgumentType.blockState()), child)
    fun boolean(name: String, child: Child) = next(arg(name, BoolArgumentType.bool()), child)
    fun color(name: String, child: Child) = next(arg(name, ColorArgumentType.color()), child)
    fun columnPos(name: String, child: Child) = next(arg(name, ColumnPosArgumentType.columnPos()), child)
    fun nbtCompound(name: String, child: Child) = next(arg(name, NbtCompoundArgumentType.nbtCompound()), child)
    fun scoreboardCriteria(name: String, child: Child) = next(arg(name, ScoreboardCriterionArgumentType.scoreboardCriterion()), child)
    fun dimension(name: String, child: Child) = next(arg(name, DimensionArgumentType.dimension()), child)
    fun double(name: String, min: Double = -1.7976931348623157E308, max: Double = 1.7976931348623157E308, child: Child) = next(arg(name, DoubleArgumentType.doubleArg(min, max)), child)
    fun enchantment(name: String, child: Child) = next(arg(name, EnchantmentArgumentType.enchantment()), child)
    fun entities(name: String, child: Child) = next(arg(name, EntityArgumentType.entities()), child)
    fun entity(name: String, child: Child) = next(arg(name, EntityArgumentType.entity()), child)
    fun entityAnchor(name: String, child: Child) = next(arg(name, EntityAnchorArgumentType.entityAnchor()), child)
    fun entitySummon(name: String, child: Child) = next(arg(name, EntitySummonArgumentType.entitySummon()), child)
    fun float(name: String, min: Float = -3.4028235E38f, max: Float = 3.4028235E38f, child: Child) = next(arg(name, FloatArgumentType.floatArg(min, max)), child)
    fun commandFunction(name: String, child: Child) = next(arg(name, CommandFunctionArgumentType.commandFunction()), child)
    fun gameProfile(name: String, child: Child) = next(arg(name, GameProfileArgumentType.gameProfile()), child)
    fun greedyString(name: String, child: Child) = next(arg(name, StringArgumentType.greedyString()), child)
    fun identifier(name: String, child: Child) = next(arg(name, IdentifierArgumentType.identifier()), child)
    fun integer(name: String, min: Int = -2147483648, max: Int = 2147483647, child: Child) = next(arg(name, IntegerArgumentType.integer(min, max)), child)
    fun itemPredicate(name: String, child: Child) = next(arg(name, ItemPredicateArgumentType.itemPredicate()), child)
    fun itemSlot(name: String, child: Child) = next(arg(name, ItemSlotArgumentType.itemSlot()), child)
    fun itemStack(name: String, child: Child) = next(arg(name, ItemStackArgumentType.itemStack()), child)
    fun long(name: String, min: Long = -9223372036854775807L, max: Long = 9223372036854775807L, child: Child) = next(arg(name, LongArgumentType.longArg(min, max)), child)
    fun message(name: String, child: Child) = next(arg(name, MessageArgumentType.message()), child)
    fun statusEffect(name: String, child: Child) = next(arg(name, StatusEffectArgumentType.statusEffect()), child)
    fun nbtPath(name: String, child: Child) = next(arg(name, NbtPathArgumentType.nbtPath()), child)
    fun scoreboardObjective(name: String, child: Child) = next(arg(name, ScoreboardObjectiveArgumentType.scoreboardObjective()), child)
    fun operation(name: String, child: Child) = next(arg(name, OperationArgumentType.operation()), child)
    fun particleEffect(name: String, child: Child) = next(arg(name, ParticleEffectArgumentType.particleEffect()), child)
    fun player(name: String, child: Child) = next(arg(name, EntityArgumentType.player()), child)
    fun players(name: String, child: Child) = next(arg(name, EntityArgumentType.players()), child)
    fun rotation(name: String, child: Child) = next(arg(name, RotationArgumentType.rotation()), child)
    fun scoreHolder(name: String, child: Child) = next(arg(name, ScoreHolderArgumentType.scoreHolder()), child)
    fun scoreHolders(name: String, child: Child) = next(arg(name, ScoreHolderArgumentType.scoreHolders()), child)
    fun scoreboardSlot(name: String, child: Child) = next(arg(name, ScoreboardSlotArgumentType.scoreboardSlot()), child)
    fun string(name: String, child: Child) = next(arg(name, StringArgumentType.string()), child)
    fun swizzle(name: String, child: Child) = next(arg(name, SwizzleArgumentType.swizzle()), child)
    fun nbtElement(name: String, child: Child) = next(arg(name, NbtElementArgumentType.nbtElement()), child)
    fun team(name: String, child: Child) = next(arg(name, TeamArgumentType.team()), child)
    fun text(name: String, child: Child) = next(arg(name, TextArgumentType.text()), child)
    fun uuid(name: String, child: Child) = next(arg(name, UuidArgumentType.uuid()), child)
    fun vec2(name: String, child: Child) = next(arg(name, Vec2ArgumentType.vec2()), child)
    fun vec3(name: String, centerIntegers: Boolean = true, child: Child) = next(arg(name, Vec3ArgumentType.vec3(centerIntegers)), child)
    fun word(name: String, child: Child) = next(arg(name, StringArgumentType.word()), child)

    fun <T : Enum<*>> enum(enum: KClass<T>, child: CommandBuilder.(T) -> Unit) {
        enum.java.enumConstants.forEach { item ->
            val arg = literal<ServerCommandSource>(item.name.lowercase())
            child(CommandBuilder(arg), item)
            builder.then(arg)
        }
    }

    fun executes(executes: CommandContext.() -> Unit) {
        builder.executes {
            executes(CommandContext(it))
            0
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun suggests(suggests: (com.mojang.brigadier.context.CommandContext<ServerCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
        if (builder is RequiredArgumentBuilder<*, *>) {
            builder.suggests { context, builder -> suggests(context as com.mojang.brigadier.context.CommandContext<ServerCommandSource>, builder) }
        }
    }

    private fun next(arg: ArgumentBuilder<ServerCommandSource, *>, child: Child) {
        child(CommandBuilder(arg))
        builder.then(arg)
    }
}
