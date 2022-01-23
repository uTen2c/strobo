package dev.uten2c.strobo.command

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import net.minecraft.advancement.Advancement
import net.minecraft.block.pattern.CachedBlockPosition
import net.minecraft.command.argument.*
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
import net.minecraft.util.math.*
import java.util.*
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
    fun getBlockPredicate(name: String): Predicate<CachedBlockPosition> = BlockPredicateArgumentType.getBlockPredicate(context, name)
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
    fun getEntityAnchor(name: String): EntityAnchorArgumentType.EntityAnchor = EntityAnchorArgumentType.getEntityAnchor(context, name)
    fun getEntitySummon(name: String): Identifier = EntitySummonArgumentType.getEntitySummon(context, name)
    fun getFloat(name: String): Float = FloatArgumentType.getFloat(context, name)
    fun getFunctionOrTag(name: String): Pair<Identifier, Either<CommandFunction, Tag<CommandFunction>>> = CommandFunctionArgumentType.getFunctionOrTag(context, name)
    fun getFunctions(name: String): MutableCollection<CommandFunction> = CommandFunctionArgumentType.getFunctions(context, name)
    fun getGameProfile(name: String): MutableCollection<GameProfile> = GameProfileArgumentType.getProfileArgument(context, name)
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
    fun getScoreHolders(name: String): MutableCollection<String> = ScoreHolderArgumentType.getScoreHolders(context, name)
    fun getScoreHolders(name: String, players: Supplier<Collection<String>>): MutableCollection<String> = ScoreHolderArgumentType.getScoreHolders(context, name, players)
    fun getScoreboardScoreHolders(name: String): MutableCollection<String> = ScoreHolderArgumentType.getScoreboardScoreHolders(context, name)
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
