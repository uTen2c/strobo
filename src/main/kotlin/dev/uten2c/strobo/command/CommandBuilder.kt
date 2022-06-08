package dev.uten2c.strobo.command

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.ArgumentType
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
import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import dev.uten2c.strobo.command.argument.ArgumentGetter
import dev.uten2c.strobo.command.argument.ScoreHoldersArgument
import net.minecraft.block.pattern.CachedBlockPosition
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.AngleArgumentType
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.BlockPredicateArgumentType
import net.minecraft.command.argument.BlockStateArgument
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
import net.minecraft.command.argument.ItemStackArgument
import net.minecraft.command.argument.ItemStackArgumentType
import net.minecraft.command.argument.MessageArgumentType
import net.minecraft.command.argument.NbtCompoundArgumentType
import net.minecraft.command.argument.NbtElementArgumentType
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.OperationArgumentType
import net.minecraft.command.argument.ParticleEffectArgumentType
import net.minecraft.command.argument.PosArgument
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
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.particle.ParticleEffect
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.scoreboard.Team
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.function.CommandFunction
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ColumnPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.util.EnumSet
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import com.mojang.brigadier.context.CommandContext as BrigadierCommandContext

typealias FunctionOrTagType = Pair<Identifier, Either<CommandFunction, Collection<CommandFunction>>>

@Suppress("unused")
class CommandBuilder(
    private val builder: ArgumentBuilder<ServerCommandSource, *>,
    private val commandRegistryAccess: CommandRegistryAccess,
) {
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
    fun literal(literal: String, child: CommandBuilder.() -> Unit) {
        val arg = literal<ServerCommandSource>(literal)
        child(CommandBuilder(arg, commandRegistryAccess))
        builder.then(arg)
    }

    /**
     * サンプル: "0", "~", "~-5"
     */
    fun angle(child: CommandBuilder.(angle: ArgumentGetter<Float>) -> Unit) =
        next(child, AngleArgumentType::angle, AngleArgumentType::getAngle)

    /**
     * サンプル: "0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5"
     */
    fun blockPos(child: CommandBuilder.(blockPos: ArgumentGetter<BlockPos>) -> Unit) =
        next(child, BlockPosArgumentType::blockPos, BlockPosArgumentType::getBlockPos)

    /**
     * サンプル: "stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}"
     */
    fun blockPredicate(child: CommandBuilder.(blockPredicate: ArgumentGetter<Predicate<CachedBlockPosition>>) -> Unit) =
        next(
            child,
            { BlockPredicateArgumentType.blockPredicate(commandRegistryAccess) },
            BlockPredicateArgumentType::getBlockPredicate,
        )

    /**
     * サンプル: "stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}"
     */
    fun blockStateArg(child: CommandBuilder.(blockState: ArgumentGetter<BlockStateArgument>) -> Unit) =
        next(child, { BlockStateArgumentType.blockState(commandRegistryAccess) }, BlockStateArgumentType::getBlockState)

    /**
     * サンプル: "true", "false"
     */
    fun boolean(child: CommandBuilder.(flag: ArgumentGetter<Boolean>) -> Unit) =
        next(child, BoolArgumentType::bool, BoolArgumentType::getBool)

    /**
     * サンプル: "red", "green"
     */
    fun color(child: CommandBuilder.(color: ArgumentGetter<Formatting>) -> Unit) =
        next(child, ColorArgumentType::color, ColorArgumentType::getColor)

    /**
     * サンプル: "0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0"
     */
    fun columnPos(child: CommandBuilder.(columnPos: ArgumentGetter<ColumnPos>) -> Unit) =
        next(child, ColumnPosArgumentType::columnPos, ColumnPosArgumentType::getColumnPos)

    /**
     * サンプル: "{}", "{foo=bar}"
     */
    fun nbtCompound(child: CommandBuilder.(nbtCompound: ArgumentGetter<NbtCompound>) -> Unit) =
        next(child, NbtCompoundArgumentType::nbtCompound, NbtCompoundArgumentType::getNbtCompound)

    /**
     * サンプル: "foo", "foo.bar.baz", "minecraft:foo"
     */
    fun scoreboardCriteria(child: CommandBuilder.(scoreboardCriteria: ArgumentGetter<ScoreboardCriterion>) -> Unit) =
        next(child, ScoreboardCriterionArgumentType::scoreboardCriterion, ScoreboardCriterionArgumentType::getCriterion)

    /**
     * サンプル: "world", "nether"
     */
    fun dimension(child: CommandBuilder.(world: ArgumentGetter<ServerWorld>) -> Unit) =
        next(child, DimensionArgumentType::dimension, DimensionArgumentType::getDimensionArgument)

    /**
     * サンプル: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
     */
    fun double(
        min: Double = -1.7976931348623157E308,
        max: Double = 1.7976931348623157E308,
        child: CommandBuilder.(double: ArgumentGetter<Double>) -> Unit,
    ) = next(child, { DoubleArgumentType.doubleArg(min, max) }, DoubleArgumentType::getDouble)

    /**
     * サンプル: "unbreaking", "silk_touch"
     */
    fun enchantment(child: CommandBuilder.(enchantment: ArgumentGetter<Enchantment>) -> Unit) =
        next(child, EnchantmentArgumentType::enchantment, EnchantmentArgumentType::getEnchantment)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun entities(child: CommandBuilder.(entities: ArgumentGetter<Collection<Entity>>) -> Unit) =
        next(child, EntityArgumentType::entities, EntityArgumentType::getEntities)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun entity(child: CommandBuilder.(entity: ArgumentGetter<Entity>) -> Unit) =
        next(child, EntityArgumentType::entity, EntityArgumentType::getEntity)

    /**
     * サンプル: "eyes", "feet"
     */
    fun entityAnchor(
        child: CommandBuilder.(entityAnchor: ArgumentGetter<EntityAnchorArgumentType.EntityAnchor>) -> Unit,
    ) = next(child, EntityAnchorArgumentType::entityAnchor, EntityAnchorArgumentType::getEntityAnchor)

    /**
     * サンプル: "minecraft:pig", "cow"
     */
    fun entitySummon(child: CommandBuilder.(entityId: ArgumentGetter<Identifier>) -> Unit) =
        next(child, EntitySummonArgumentType::entitySummon, EntitySummonArgumentType::getEntitySummon)

    /**
     * サンプル: "0", "1.2", ".5", "-1", "-.5", "-1234.56"
     */
    fun float(
        min: Float = -3.4028235E38f,
        max: Float = 3.4028235E38f,
        child: CommandBuilder.(float: ArgumentGetter<Float>) -> Unit,
    ) = next(child, { FloatArgumentType.floatArg(min, max) }, FloatArgumentType::getFloat)

    /**
     * サンプル: "foo", "foo:bar", "#foo"
     */
    fun functionOrTag(
        child: CommandBuilder.(pair: ArgumentGetter<FunctionOrTagType>) -> Unit,
    ) = next(child, CommandFunctionArgumentType::commandFunction, CommandFunctionArgumentType::getFunctionOrTag)

    /**
     * サンプル: "foo", "foo:bar", "#foo"
     */
    fun functions(child: CommandBuilder.(functions: ArgumentGetter<Collection<CommandFunction>>) -> Unit) =
        next(child, CommandFunctionArgumentType::commandFunction, CommandFunctionArgumentType::getFunctions)

    /**
     * サンプル: "Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e"
     */
    fun gameProfile(child: CommandBuilder.(gameProfile: ArgumentGetter<Collection<GameProfile>>) -> Unit) =
        next(child, GameProfileArgumentType::gameProfile, GameProfileArgumentType::getProfileArgument)

    /**
     * サンプル: "word", "words with spaces", "\"and symbols\""
     */
    fun greedyString(child: CommandBuilder.(greedyString: ArgumentGetter<String>) -> Unit) =
        next(child, StringArgumentType::greedyString, StringArgumentType::getString)

    /**
     * サンプル: "foo", "foo:bar", "012"
     */
    fun identifier(child: CommandBuilder.(identifier: ArgumentGetter<Identifier>) -> Unit) =
        next(child, IdentifierArgumentType::identifier, IdentifierArgumentType::getIdentifier)

    /**
     * サンプル: "0", "123", "-123"
     */
    fun integer(
        min: Int = -2147483648,
        max: Int = 2147483647,
        child: CommandBuilder.(integer: ArgumentGetter<Int>) -> Unit,
    ) = next(child, { IntegerArgumentType.integer(min, max) }, IntegerArgumentType::getInteger)

    /**
     * サンプル: "stick", "minecraft:stick", "#stick", "#stick{foo=bar}"
     */
    fun itemPredicate(child: CommandBuilder.(itemPredicate: ArgumentGetter<Predicate<ItemStack>>) -> Unit) =
        next(
            child,
            { ItemPredicateArgumentType.itemPredicate(commandRegistryAccess) },
            ItemPredicateArgumentType::getItemStackPredicate,
        )

    /**
     * サンプル: "container.5", "12", "weapon"
     */
    fun itemSlot(child: CommandBuilder.(itemSlot: ArgumentGetter<Int>) -> Unit) =
        next(child, ItemSlotArgumentType::itemSlot, ItemSlotArgumentType::getItemSlot)

    /**
     * サンプル: "stick", "minecraft:stick", "stick{foo=bar}"
     */
    fun itemStack(child: CommandBuilder.(itemStack: ArgumentGetter<ItemStackArgument>) -> Unit) =
        next(
            child,
            { ItemStackArgumentType.itemStack(commandRegistryAccess) },
            ItemStackArgumentType::getItemStackArgument,
        )

    /**
     * サンプル: "0", "123", "-123"
     */
    fun long(
        min: Long = -9223372036854775807L,
        max: Long = 9223372036854775807L,
        child: CommandBuilder.(long: ArgumentGetter<Long>) -> Unit,
    ) = next(child, { LongArgumentType.longArg(min, max) }, LongArgumentType::getLong)

    /**
     * サンプル: "Hello world!", "foo", "@e", "Hello @p :)"
     */
    fun message(child: CommandBuilder.(message: ArgumentGetter<Text>) -> Unit) =
        next(child, MessageArgumentType::message, MessageArgumentType::getMessage)

    /**
     * サンプル: "spooky", "effect"
     */
    fun statusEffect(child: CommandBuilder.(statusEffect: ArgumentGetter<StatusEffect>) -> Unit) =
        next(child, StatusEffectArgumentType::statusEffect, StatusEffectArgumentType::getStatusEffect)

    /**
     * サンプル: "foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}"
     */
    fun nbtPath(child: CommandBuilder.(nbtPath: ArgumentGetter<NbtPathArgumentType.NbtPath>) -> Unit) =
        next(child, NbtPathArgumentType::nbtPath, NbtPathArgumentType::getNbtPath)

    /**
     * サンプル: "foo", "*", "012"
     */
    fun scoreboardObjective(child: CommandBuilder.(scoreboardObjective: ArgumentGetter<ScoreboardObjective>) -> Unit) =
        next(child, ScoreboardObjectiveArgumentType::scoreboardObjective, ScoreboardObjectiveArgumentType::getObjective)

    /**
     * サンプル: "foo", "*", "012"
     */
    fun scoreboardWritableObjective(
        child: CommandBuilder.(scoreboardWritableObjective: ArgumentGetter<ScoreboardObjective>) -> Unit,
    ) = next(
        child,
        ScoreboardObjectiveArgumentType::scoreboardObjective,
        ScoreboardObjectiveArgumentType::getWritableObjective,
    )

    /**
     * サンプル: "=", ">", "<"
     */
    fun operation(child: CommandBuilder.(operation: ArgumentGetter<OperationArgumentType.Operation>) -> Unit) =
        next(child, OperationArgumentType::operation, OperationArgumentType::getOperation)

    /**
     * サンプル: "foo", "foo:bar", "particle with options"
     */
    fun particleEffect(child: CommandBuilder.(particleEffect: ArgumentGetter<ParticleEffect>) -> Unit) =
        next(child, ParticleEffectArgumentType::particleEffect, ParticleEffectArgumentType::getParticle)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun player(child: CommandBuilder.(player: ArgumentGetter<ServerPlayerEntity>) -> Unit) =
        next(child, EntityArgumentType::player, EntityArgumentType::getPlayer)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun players(child: CommandBuilder.(players: ArgumentGetter<Collection<ServerPlayerEntity>>) -> Unit) =
        next(child, EntityArgumentType::players, EntityArgumentType::getPlayers)

    /**
     * サンプル: "Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun optionalPlayers(child: CommandBuilder.(players: ArgumentGetter<Collection<ServerPlayerEntity>>) -> Unit) =
        next(child, EntityArgumentType::players, EntityArgumentType::getOptionalPlayers)

    /**
     * サンプル: "0 0", "~ ~", "~-5 ~5"
     */
    fun rotation(child: CommandBuilder.(rotation: ArgumentGetter<PosArgument>) -> Unit) =
        next(child, RotationArgumentType::rotation, RotationArgumentType::getRotation)

    /**
     * サンプル: "Player", "0123", "*", "@e"
     */
    fun scoreHolder(child: CommandBuilder.(scoreHolder: ArgumentGetter<String>) -> Unit) =
        next(child, ScoreHolderArgumentType::scoreHolder, ScoreHolderArgumentType::getScoreHolder)

    /**
     * サンプル: "Player", "0123", "*", "@e"
     */
    fun scoreHolders(child: CommandBuilder.(scoreHolders: ScoreHoldersArgument) -> Unit) =
        nextScoreHolders(child, ScoreHolderArgumentType::scoreHolder, ScoreHolderArgumentType::getScoreHolders)

    /**
     * サンプル: "Player", "0123", "*", "@e"
     */
    fun scoreboardScoreHolders(child: CommandBuilder.(scoreHolders: ArgumentGetter<Collection<String>>) -> Unit) =
        next(child, ScoreHolderArgumentType::scoreHolder, ScoreHolderArgumentType::getScoreboardScoreHolders)

    /**
     * サンプル: "sidebar", "foo.bar"
     */
    fun scoreboardSlot(child: CommandBuilder.(scoreboardSlot: ArgumentGetter<Int>) -> Unit) =
        next(child, ScoreboardSlotArgumentType::scoreboardSlot, ScoreboardSlotArgumentType::getScoreboardSlot)

    /**
     * サンプル: "\"quoted phrase\"", "word", "\"\""
     */
    fun string(child: CommandBuilder.(getString: ArgumentGetter<String>) -> Unit) =
        next(child, StringArgumentType::string, StringArgumentType::getString)

    /**
     * サンプル: "xyz", "x"
     */
    fun swizzle(child: CommandBuilder.(getSwizzle: ArgumentGetter<EnumSet<Direction.Axis>>) -> Unit) =
        next(child, SwizzleArgumentType::swizzle, SwizzleArgumentType::getSwizzle)

    /**
     * サンプル: "0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]"
     */
    fun nbtElement(child: CommandBuilder.(getNbtElement: ArgumentGetter<NbtElement>) -> Unit) =
        next(child, NbtElementArgumentType::nbtElement, NbtElementArgumentType::getNbtElement)

    /**
     * サンプル: "foo", "123"
     */
    fun team(child: CommandBuilder.(getTeam: ArgumentGetter<Team>) -> Unit) =
        next(child, TeamArgumentType::team, TeamArgumentType::getTeam)

    /**
     * サンプル: "\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]"
     */
    fun text(child: CommandBuilder.(getText: ArgumentGetter<Text>) -> Unit) =
        next(child, TextArgumentType::text, TextArgumentType::getTextArgument)

    /**
     * サンプル: "dd12be42-52a9-4a91-a8a1-11c01849e498"
     */
    fun uuid(child: CommandBuilder.(getUUID: ArgumentGetter<UUID>) -> Unit) =
        next(child, UuidArgumentType::uuid, UuidArgumentType::getUuid)

    /**
     * サンプル: "0 0", "~ ~", "0.1 -0.5", "~1 ~-2"
     */
    fun vec2(child: CommandBuilder.(getVec2: ArgumentGetter<Vec2f>) -> Unit) =
        next(child, Vec2ArgumentType::vec2, Vec2ArgumentType::getVec2)

    /**
     * サンプル: "0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5"
     */
    fun vec3(centerIntegers: Boolean = true, child: CommandBuilder.(getVec3: ArgumentGetter<Vec3d>) -> Unit) =
        next(child, { Vec3ArgumentType.vec3(centerIntegers) }, Vec3ArgumentType::getVec3)

    /**
     * サンプル: "word", "words_with_underscores"
     */
    fun word(child: CommandBuilder.(getWord: ArgumentGetter<String>) -> Unit) =
        next(child, StringArgumentType::word, StringArgumentType::getString)

    /**
     * 任意のEnum
     */
    fun <T : Enum<*>> enum(enum: KClass<T>, child: CommandBuilder.(T) -> Unit) {
        enum.java.enumConstants.forEach { item ->
            val arg = literal<ServerCommandSource>(item.name.lowercase())
            child(CommandBuilder(arg, commandRegistryAccess), item)
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
    fun suggests(
        suggests: (BrigadierCommandContext<ServerCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>,
    ) {
        if (builder is RequiredArgumentBuilder<*, *>) {
            builder.suggests { context, builder ->
                suggests(
                    context as BrigadierCommandContext<ServerCommandSource>,
                    builder,
                )
            }
        }
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    private fun <T1, T2> next(
        child: CommandBuilder.(ArgumentGetter<T1>) -> Unit,
        argumentProvider: () -> ArgumentType<T2>,
        factory: (com.mojang.brigadier.context.CommandContext<ServerCommandSource>, String) -> T1,
    ) {
        val name = getterNameToParamName(child.reflect()?.parameters?.get(1)?.name.toString())
        val arg = RequiredArgumentBuilder.argument<ServerCommandSource, T2>(name, argumentProvider())
        child(CommandBuilder(arg, commandRegistryAccess), ArgumentGetter { factory(it, name) })
        builder.then(arg)
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    private fun <T> nextScoreHolders(
        child: CommandBuilder.(ScoreHoldersArgument) -> Unit,
        argumentProvider: () -> ArgumentType<T>,
        factory: (
            com.mojang.brigadier.context.CommandContext<ServerCommandSource>,
            String,
            Supplier<Collection<String>>,
        ) -> Collection<String>,
    ) {
        val getterName = child.reflect()?.parameters?.get(1)?.name.toString()
        val name = getterNameToParamName(getterName)
        val arg = RequiredArgumentBuilder.argument<ServerCommandSource, T>(name, argumentProvider())
        child(
            CommandBuilder(arg, commandRegistryAccess),
            ScoreHoldersArgument { ctx, supplier -> factory(ctx, name, supplier) },
        )
        builder.then(arg)
    }

    private fun getterNameToParamName(getterName: String): String {
        val name = StringBuilder(getterName)
        if (name.startsWith("get") && 3 < name.length) {
            name.delete(0, 3)
            name[0] = name[0].lowercaseChar()
        }
        return name.toString()
    }
}
