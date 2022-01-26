package dev.uten2c.strobo.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.argument.AngleArgumentType
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.BlockPredicateArgumentType
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.command.argument.ColumnPosArgumentType
import net.minecraft.command.argument.CommandFunctionArgumentType
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.command.argument.EnchantmentArgumentType
import net.minecraft.command.argument.EntityAnchorArgumentType
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.EntitySummonArgumentType
import net.minecraft.command.argument.GameProfileArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.command.argument.ItemPredicateArgumentType
import net.minecraft.command.argument.ItemSlotArgumentType
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.command.argument.MessageArgumentType
import net.minecraft.command.argument.NbtCompoundArgumentType
import net.minecraft.command.argument.NbtElementArgumentType
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.OperationArgumentType
import net.minecraft.command.argument.ParticleEffectArgumentType
import net.minecraft.command.argument.RotationArgumentType
import net.minecraft.command.argument.ScoreHolderArgumentType
import net.minecraft.command.argument.ScoreboardCriterionArgumentType
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType
import net.minecraft.command.argument.ScoreboardSlotArgumentType
import net.minecraft.command.argument.StatusEffectArgumentType
import net.minecraft.command.argument.SwizzleArgumentType
import net.minecraft.command.argument.TeamArgumentType
import net.minecraft.command.argument.TextArgumentType
import net.minecraft.command.argument.UuidArgumentType
import net.minecraft.command.argument.Vec2ArgumentType
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument as arg
import com.mojang.brigadier.context.CommandContext as BrigadierCommandContext

private typealias Child = CommandBuilder.() -> Unit

@Suppress("unused")
class CommandBuilder(private val builder: ArgumentBuilder<ServerCommandSource, *>) {
    internal var filter: ((ServerCommandSource) -> Boolean)? = null
        private set
    internal var aliases: List<String>? = null
        private set
    internal var executes: (CommandContext.() -> Unit)? = null
        private set

    /**
     * コマンド実行に必要とするOPレベルを設定する
     * @param opLevel op level
     */
    fun requires(opLevel: Int) {
        val requirement: (ServerCommandSource) -> Boolean = { it.hasPermissionLevel(opLevel) }
        builder.requires { requirement(it) }
        this.filter = requirement
    }

    /**
     * コマンド実行に必要な条件を設定する
     * @param filter フィルター処理
     */
    fun requires(filter: (ServerCommandSource) -> Boolean) {
        builder.requires { filter(it) }
        this.filter = filter
    }

    fun aliases(vararg aliases: String) {
        this.aliases = listOf(*aliases)
    }

    /**
     * 任意の文字列
     */
    fun literal(literal: String, child: Child) = next(literal(literal), child)

    /**
     * サンプル: "0", "~", "~-5"
     */
    fun angle(name: String, child: Child) = next(arg(name, AngleArgumentType.angle()), child)

    /**
     * サンプル: "0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5"
     */
    fun blockPos(name: String, child: Child) = next(arg(name, BlockPosArgumentType.blockPos()), child)

    /**
     * サンプル: "stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}"
     */
    fun blockPredicate(name: String, child: Child) = next(arg(name, BlockPredicateArgumentType.blockPredicate()), child)

    /**
     * サンプル: "stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}"
     */
    fun blockStateArg(name: String, child: Child) = next(arg(name, BlockStateArgumentType.blockState()), child)

    /**
     * サンプル: "true", "false"
     */
    fun boolean(name: String, child: Child) = next(arg(name, BoolArgumentType.bool()), child)

    /**
     * サンプル: "red", "green"
     */
    fun color(name: String, child: Child) = next(arg(name, ColorArgumentType.color()), child)

    /**
     * サンプル: "0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0"
     */
    fun columnPos(name: String, child: Child) = next(arg(name, ColumnPosArgumentType.columnPos()), child)

    /**
     * サンプル: "{}", "{foo=bar}"
     */
    fun nbtCompound(name: String, child: Child) = next(arg(name, NbtCompoundArgumentType.nbtCompound()), child)

    /**
     * サンプル: "foo", "foo.bar.baz", "minecraft:foo"
     */
    fun scoreboardCriteria(name: String, child: Child) = next(
        arg(name, ScoreboardCriterionArgumentType.scoreboardCriterion()),
        child,
    )

    /**
     * サンプル: "world", "nether"
     */
    fun dimension(name: String, child: Child) = next(arg(name, DimensionArgumentType.dimension()), child)

    /**
     * サンプル: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
     */
    fun double(name: String, min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE, child: Child) = next(
        arg(name, DoubleArgumentType.doubleArg(min, max)),
        child,
    )

    /**
     * サンプル: "unbreaking", "silk_touch"
     */
    fun enchantment(name: String, child: Child) = next(arg(name, EnchantmentArgumentType.enchantment()), child)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun entities(name: String, child: Child) = next(arg(name, EntityArgumentType.entities()), child)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun entity(name: String, child: Child) = next(arg(name, EntityArgumentType.entity()), child)

    /**
     * サンプル: "eyes", "feet"
     */
    fun entityAnchor(name: String, child: Child) = next(arg(name, EntityAnchorArgumentType.entityAnchor()), child)

    /**
     * サンプル: "minecraft:pig", "cow"
     */
    fun entitySummon(name: String, child: Child) = next(arg(name, EntitySummonArgumentType.entitySummon()), child)

    /**
     * サンプル: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
     */
    fun float(name: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE, child: Child) = next(
        arg(name, FloatArgumentType.floatArg(min, max)),
        child,
    )

    /**
     * サンプル: "foo", "foo:bar", "#foo"
     */
    fun commandFunction(name: String, child: Child) = next(
        arg(name, CommandFunctionArgumentType.commandFunction()),
        child,
    )

    /**
     * サンプル: "Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e"
     */
    fun gameProfile(name: String, child: Child) = next(arg(name, GameProfileArgumentType.gameProfile()), child)

    /**
     * サンプル: "word", "words with spaces", "\"and symbols\""
     */
    fun greedyString(name: String, child: Child) = next(arg(name, StringArgumentType.greedyString()), child)

    /**
     * サンプル: "foo", "foo:bar", "012"
     */
    fun identifier(name: String, child: Child) = next(arg(name, IdentifierArgumentType.identifier()), child)

    /**
     * サンプル: "0", "123", "-123"
     */
    fun integer(name: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE, child: Child) = next(
        arg(name, IntegerArgumentType.integer(min, max)),
        child,
    )

    /**
     * サンプル: "stick", "minecraft:stick", "#stick", "#stick{foo=bar}"
     */
    fun itemPredicate(name: String, child: Child) = next(arg(name, ItemPredicateArgumentType.itemPredicate()), child)

    /**
     * サンプル: "container.5", "12", "weapon"
     */
    fun itemSlot(name: String, child: Child) = next(arg(name, ItemSlotArgumentType.itemSlot()), child)

    /**
     * サンプル: "stick", "minecraft:stick", "stick{foo=bar}"
     */
    fun itemStack(name: String, child: Child) = next(arg(name, ItemStackArgumentType.itemStack()), child)

    /**
     * サンプル: "0", "123", "-123"
     */
    fun long(name: String, min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE, child: Child) = next(
        arg(name, LongArgumentType.longArg(min, max)),
        child,
    )

    /**
     * サンプル: "Hello world!", "foo", "@e", "Hello @p :)"
     */
    fun message(name: String, child: Child) = next(arg(name, MessageArgumentType.message()), child)

    /**
     * サンプル: "spooky", "effect"
     */
    fun statusEffect(name: String, child: Child) = next(arg(name, StatusEffectArgumentType.statusEffect()), child)

    /**
     * サンプル: "foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}"
     */
    fun nbtPath(name: String, child: Child) = next(arg(name, NbtPathArgumentType.nbtPath()), child)

    /**
     * サンプル: "foo", "*", "012"
     */
    fun scoreboardObjective(name: String, child: Child) = next(
        arg(name, ScoreboardObjectiveArgumentType.scoreboardObjective()),
        child,
    )

    /**
     * サンプル: "=", ">", "<"
     */
    fun operation(name: String, child: Child) = next(arg(name, OperationArgumentType.operation()), child)

    /**
     * サンプル: "foo", "foo:bar", "particle with options"
     */
    fun particleEffect(name: String, child: Child) = next(arg(name, ParticleEffectArgumentType.particleEffect()), child)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun player(name: String, child: Child) = next(arg(name, EntityArgumentType.player()), child)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun players(name: String, child: Child) = next(arg(name, EntityArgumentType.players()), child)

    /**
     * サンプル: "0 0", "~ ~", "~-5 ~5"
     */
    fun rotation(name: String, child: Child) = next(arg(name, RotationArgumentType.rotation()), child)

    /**
     * サンプル: "Player", "0123", "*", "@e"
     */
    fun scoreHolder(name: String, child: Child) = next(arg(name, ScoreHolderArgumentType.scoreHolder()), child)

    /**
     * サンプル: "Player", "0123", "*", "@e"
     */
    fun scoreHolders(name: String, child: Child) = next(arg(name, ScoreHolderArgumentType.scoreHolders()), child)

    /**
     * サンプル: "sidebar", "foo.bar"
     */
    fun scoreboardSlot(name: String, child: Child) = next(arg(name, ScoreboardSlotArgumentType.scoreboardSlot()), child)

    /**
     * サンプル: "\"quoted phrase\"", "word", "\"\""
     */
    fun string(name: String, child: Child) = next(arg(name, StringArgumentType.string()), child)

    /**
     * サンプル: "xyz", "x"
     */
    fun swizzle(name: String, child: Child) = next(arg(name, SwizzleArgumentType.swizzle()), child)

    /**
     * サンプル: "0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]"
     */
    fun nbtElement(name: String, child: Child) = next(arg(name, NbtElementArgumentType.nbtElement()), child)

    /**
     * サンプル: "foo", "123"
     */
    fun team(name: String, child: Child) = next(arg(name, TeamArgumentType.team()), child)

    /**
     * サンプル: "\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]"
     */
    fun text(name: String, child: Child) = next(arg(name, TextArgumentType.text()), child)

    /**
     * サンプル: "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun uuid(name: String, child: Child) = next(arg(name, UuidArgumentType.uuid()), child)

    /**
     * サンプル: "0 0", "~ ~", "0.1 -0.5", "~1 ~-2"
     */
    fun vec2(name: String, child: Child) = next(arg(name, Vec2ArgumentType.vec2()), child)

    /**
     * サンプル: "0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5"
     */
    fun vec3(name: String, centerIntegers: Boolean = true, child: Child) = next(
        arg(name, Vec3ArgumentType.vec3(centerIntegers)),
        child,
    )

    /**
     * サンプル: "word", "words_with_underscores"
     */
    fun word(name: String, child: Child) = next(arg(name, StringArgumentType.word()), child)

    /**
     * 任意のEnum
     */
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
        this.executes = executes
    }

    @Suppress("UNCHECKED_CAST")
    fun suggests(suggests: (BrigadierCommandContext<ServerCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>) { // ktlint-disable
        if (builder is RequiredArgumentBuilder<*, *>) {
            builder.suggests { context, builder ->
                suggests(
                    context as BrigadierCommandContext<ServerCommandSource>,
                    builder,
                )
            }
        }
    }

    private fun next(arg: ArgumentBuilder<ServerCommandSource, *>, child: Child) {
        child(CommandBuilder(arg))
        builder.then(arg)
    }
}
