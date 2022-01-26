package dev.uten2c.strobo.command

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import net.minecraft.advancement.Advancement
import net.minecraft.block.pattern.CachedBlockPosition
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
import net.minecraft.command.argument.EntityAnchorArgumentType.getEntityAnchor
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
import net.minecraft.recipe.Recipe
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.scoreboard.Team
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.function.CommandFunction
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.Tag
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
import java.util.function.Predicate
import java.util.function.Supplier

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandContext(private val context: CommandContext<ServerCommandSource>) {

    val source: ServerCommandSource = context.source
    val world: ServerWorld get() = source.world
    val player: ServerPlayerEntity get() = source.player

    fun sendFeedback(message: Text, broadcastToOps: Boolean = false) = source.sendFeedback(message, broadcastToOps)

    fun sendError(message: Text) = source.sendError(message)

    fun getAdvancement(name: String): Advancement = IdentifierArgumentType.getAdvancementArgument(context, name)

    fun getAngle(name: String): Float = AngleArgumentType.getAngle(context, name)

    fun getBlockPos(name: String): BlockPos = BlockPosArgumentType.getBlockPos(context, name)

    fun getBlockPredicate(name: String): Predicate<CachedBlockPosition> =
        BlockPredicateArgumentType.getBlockPredicate(context, name)

    fun getBlockStateArg(name: String): BlockStateArgument = BlockStateArgumentType.getBlockState(context, name)

    fun getBoolean(name: String): Boolean = BoolArgumentType.getBool(context, name)

    fun getColor(name: String): Formatting = ColorArgumentType.getColor(context, name)

    fun getColumnPos(name: String): ColumnPos = ColumnPosArgumentType.getColumnPos(context, name)

    fun getNbtCompound(name: String): NbtCompound = NbtCompoundArgumentType.getNbtCompound(context, name)

    fun getCriteria(name: String): ScoreboardCriterion = ScoreboardCriterionArgumentType.getCriterion(context, name)

    fun getDimension(name: String): ServerWorld = DimensionArgumentType.getDimensionArgument(context, name)

    fun getDouble(name: String): Double = DoubleArgumentType.getDouble(context, name)

    fun getEnchantment(name: String): Enchantment = EnchantmentArgumentType.getEnchantment(context, name)

    fun getEntities(name: String): Collection<Entity> = EntityArgumentType.getEntities(context, name)

    fun getEntity(name: String): Entity = EntityArgumentType.getEntity(context, name)

    fun getEntityAnchor(name: String): EntityAnchorArgumentType.EntityAnchor = getEntityAnchor(context, name)

    fun getEntitySummon(name: String): Identifier = EntitySummonArgumentType.getEntitySummon(context, name)

    fun getFloat(name: String): Float = FloatArgumentType.getFloat(context, name)

    fun getFunctionOrTag(name: String): Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> =
        CommandFunctionArgumentType.getFunctionOrTag(context, name)

    fun getFunctions(name: String): Collection<CommandFunction> =
        CommandFunctionArgumentType.getFunctions(context, name)

    fun getGameProfile(name: String): Collection<GameProfile> =
        GameProfileArgumentType.getProfileArgument(context, name)

    fun getIdentifier(name: String): Identifier = IdentifierArgumentType.getIdentifier(context, name)

    fun getInteger(name: String): Int = IntegerArgumentType.getInteger(context, name)

    fun getItemPredicate(name: String): Predicate<ItemStack> = ItemPredicateArgumentType.getItemPredicate(context, name)

    fun getItemSlot(name: String): Int = ItemSlotArgumentType.getItemSlot(context, name)

    fun getItemStack(name: String): ItemStackArgument = ItemStackArgumentType.getItemStackArgument(context, name)

    fun getLong(name: String): Long = LongArgumentType.getLong(context, name)

    fun getMessage(name: String): Text = MessageArgumentType.getMessage(context, name)

    fun getStatusEffect(name: String): StatusEffect = StatusEffectArgumentType.getStatusEffect(context, name)

    fun getNbtPath(name: String): NbtPathArgumentType.NbtPath = NbtPathArgumentType.getNbtPath(context, name)

    fun getObjective(name: String): ScoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, name)

    fun getOperation(name: String): OperationArgumentType.Operation = OperationArgumentType.getOperation(context, name)

    fun getParticle(name: String): ParticleEffect = ParticleEffectArgumentType.getParticle(context, name)

    fun getPlayer(name: String): ServerPlayerEntity = EntityArgumentType.getPlayer(context, name)

    fun getPlayers(name: String): Collection<ServerPlayerEntity> = EntityArgumentType.getPlayers(context, name)

    fun getRecipe(name: String): Recipe<*> = IdentifierArgumentType.getRecipeArgument(context, name)

    fun getRotation(name: String): PosArgument = RotationArgumentType.getRotation(context, name)

    fun getScoreHolder(name: String): String = ScoreHolderArgumentType.getScoreHolder(context, name)

    fun getScoreHolders(name: String): Collection<String> = ScoreHolderArgumentType.getScoreHolders(context, name)

    fun getScoreHolders(name: String, players: Supplier<Collection<String>>): Collection<String> =
        ScoreHolderArgumentType.getScoreHolders(context, name, players)

    fun getScoreboardScoreHolders(name: String): Collection<String> =
        ScoreHolderArgumentType.getScoreboardScoreHolders(context, name)

    fun getScoreboardSlot(name: String) = ScoreboardSlotArgumentType.getScoreboardSlot(context, name)

    fun getString(name: String): String = StringArgumentType.getString(context, name)

    fun getSwizzle(name: String): EnumSet<Direction.Axis> = SwizzleArgumentType.getSwizzle(context, name)

    fun getNbtElement(name: String): NbtElement = NbtElementArgumentType.getNbtElement(context, name)

    fun getTeam(name: String): Team = TeamArgumentType.getTeam(context, name)

    fun getText(name: String): Text = TextArgumentType.getTextArgument(context, name)

    fun getUUID(name: String): UUID = UuidArgumentType.getUuid(context, name)

    fun getVec2(name: String): Vec2f = Vec2ArgumentType.getVec2(context, name)

    fun getVec3(name: String): Vec3d = Vec3ArgumentType.getVec3(context, name)
}
