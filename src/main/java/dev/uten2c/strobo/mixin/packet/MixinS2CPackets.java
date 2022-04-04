package dev.uten2c.strobo.mixin.packet;

import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.network.packet.s2c.play.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin({AdvancementUpdateS2CPacket.class, BlockBreakingProgressS2CPacket.class, BlockEntityUpdateS2CPacket.class, BlockEventS2CPacket.class, BlockUpdateS2CPacket.class, BossBarS2CPacket.class, ChunkDataS2CPacket.class, ChunkDeltaUpdateS2CPacket.class, ChunkLoadDistanceS2CPacket.class, ChunkRenderDistanceCenterS2CPacket.class, ClearTitleS2CPacket.class, CloseScreenS2CPacket.class, CommandSuggestionsS2CPacket.class, CommandTreeS2CPacket.class, CooldownUpdateS2CPacket.class, CraftFailedResponseS2CPacket.class, CustomPayloadS2CPacket.class, DeathMessageS2CPacket.class, DifficultyS2CPacket.class, DisconnectS2CPacket.class, EndCombatS2CPacket.class, EnterCombatS2CPacket.class, EntitiesDestroyS2CPacket.class, EntityAnimationS2CPacket.class, EntityAttachS2CPacket.class, EntityAttributesS2CPacket.class, EntityEquipmentUpdateS2CPacket.class, EntityPassengersSetS2CPacket.class, EntityPositionS2CPacket.class, EntityS2CPacket.class, EntitySetHeadYawS2CPacket.class, EntitySpawnS2CPacket.class, EntityStatusEffectS2CPacket.class, EntityStatusS2CPacket.class, EntityTrackerUpdateS2CPacket.class, EntityVelocityUpdateS2CPacket.class, ExperienceBarUpdateS2CPacket.class, ExperienceOrbSpawnS2CPacket.class, ExplosionS2CPacket.class, GameJoinS2CPacket.class, GameMessageS2CPacket.class, GameStateChangeS2CPacket.class, HealthUpdateS2CPacket.class, InventoryS2CPacket.class, ItemPickupAnimationS2CPacket.class, KeepAliveS2CPacket.class, LightUpdateS2CPacket.class, LookAtS2CPacket.class, MapUpdateS2CPacket.class, MobSpawnS2CPacket.class, NbtQueryResponseS2CPacket.class, OpenHorseScreenS2CPacket.class, OpenScreenS2CPacket.class, OpenWrittenBookS2CPacket.class, OverlayMessageS2CPacket.class, PaintingSpawnS2CPacket.class, ParticleS2CPacket.class, PlayerAbilitiesS2CPacket.class, PlayerActionResponseS2CPacket.class, PlayerListHeaderS2CPacket.class, PlayerListS2CPacket.class, PlayerPositionLookS2CPacket.class, PlayerRespawnS2CPacket.class, PlayerSpawnPositionS2CPacket.class, PlayerSpawnS2CPacket.class, PlayPingS2CPacket.class, PlaySoundFromEntityS2CPacket.class, PlaySoundIdS2CPacket.class, PlaySoundS2CPacket.class, RemoveEntityStatusEffectS2CPacket.class, ResourcePackSendS2CPacket.class, ScoreboardDisplayS2CPacket.class, ScoreboardObjectiveUpdateS2CPacket.class, ScoreboardPlayerUpdateS2CPacket.class, ScreenHandlerPropertyUpdateS2CPacket.class, ScreenHandlerSlotUpdateS2CPacket.class, SelectAdvancementTabS2CPacket.class, SetCameraEntityS2CPacket.class, SetTradeOffersS2CPacket.class, SignEditorOpenS2CPacket.class, SimulationDistanceS2CPacket.class, StatisticsS2CPacket.class, StopSoundS2CPacket.class, SubtitleS2CPacket.class, SynchronizeRecipesS2CPacket.class, SynchronizeTagsS2CPacket.class, TeamS2CPacket.class, TitleFadeS2CPacket.class, TitleS2CPacket.class, UnloadChunkS2CPacket.class, UnlockRecipesS2CPacket.class, UpdateSelectedSlotS2CPacket.class, VehicleMoveS2CPacket.class, VibrationS2CPacket.class, WorldBorderCenterChangedS2CPacket.class, WorldBorderInitializeS2CPacket.class, WorldBorderInterpolateSizeS2CPacket.class, WorldBorderSizeChangedS2CPacket.class, WorldBorderWarningBlocksChangedS2CPacket.class, WorldBorderWarningTimeChangedS2CPacket.class, WorldEventS2CPacket.class, WorldTimeUpdateS2CPacket.class})
public class MixinS2CPackets implements UuidHolder {
    private UUID strobo$uuid;

    @Override
    public @Nullable UUID getUuid() {
        return strobo$uuid;
    }

    @Override
    public void setUuid(@Nullable UUID uuid) {
        strobo$uuid = uuid;
    }
}
