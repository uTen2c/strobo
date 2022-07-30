package dev.uten2c.strobo.mixin.packet;

import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.network.packet.s2c.play.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin({EntitySpawnS2CPacket.class, ExperienceOrbSpawnS2CPacket.class, PlayerSpawnS2CPacket.class, EntityAnimationS2CPacket.class, StatisticsS2CPacket.class,PlayerActionResponseS2CPacket.class,BlockBreakingProgressS2CPacket.class,BlockEntityUpdateS2CPacket.class,BlockEventS2CPacket.class,BlockUpdateS2CPacket.class,BossBarS2CPacket.class,DifficultyS2CPacket.class,ChatPreviewS2CPacket.class,ClearTitleS2CPacket.class,CommandSuggestionsS2CPacket.class,CommandTreeS2CPacket.class,CloseScreenS2CPacket.class,InventoryS2CPacket.class,ScreenHandlerPropertyUpdateS2CPacket.class,ScreenHandlerSlotUpdateS2CPacket.class,CooldownUpdateS2CPacket.class,CustomPayloadS2CPacket.class,PlaySoundIdS2CPacket.class,DisconnectS2CPacket.class,EntityStatusS2CPacket.class,ExplosionS2CPacket.class,UnloadChunkS2CPacket.class,GameStateChangeS2CPacket.class,OpenHorseScreenS2CPacket.class,WorldBorderInitializeS2CPacket.class,KeepAliveS2CPacket.class,ChunkDataS2CPacket.class,WorldEventS2CPacket.class,ParticleS2CPacket.class,LightUpdateS2CPacket.class,GameJoinS2CPacket.class,MapUpdateS2CPacket.class,SetTradeOffersS2CPacket.class,EntityS2CPacket.MoveRelative.class, EntityS2CPacket.RotateAndMoveRelative.class, EntityS2CPacket.Rotate.class, VehicleMoveS2CPacket.class,OpenWrittenBookS2CPacket.class,OpenScreenS2CPacket.class,SignEditorOpenS2CPacket.class,PlayPingS2CPacket.class,CraftFailedResponseS2CPacket.class,PlayerAbilitiesS2CPacket.class,ChatMessageS2CPacket.class,EndCombatS2CPacket.class,EnterCombatS2CPacket.class,DeathMessageS2CPacket.class,PlayerListS2CPacket.class,LookAtS2CPacket.class,PlayerPositionLookS2CPacket.class,UnlockRecipesS2CPacket.class,EntitiesDestroyS2CPacket.class,RemoveEntityStatusEffectS2CPacket.class,ResourcePackSendS2CPacket.class,PlayerRespawnS2CPacket.class,EntitySetHeadYawS2CPacket.class,ChunkDeltaUpdateS2CPacket.class,SelectAdvancementTabS2CPacket.class,ServerMetadataS2CPacket.class,OverlayMessageS2CPacket.class,WorldBorderCenterChangedS2CPacket.class,WorldBorderInterpolateSizeS2CPacket.class,WorldBorderSizeChangedS2CPacket.class,WorldBorderWarningTimeChangedS2CPacket.class,WorldBorderWarningBlocksChangedS2CPacket.class,SetCameraEntityS2CPacket.class,UpdateSelectedSlotS2CPacket.class,ChunkRenderDistanceCenterS2CPacket.class,ChunkLoadDistanceS2CPacket.class,PlayerSpawnPositionS2CPacket.class,ChatPreviewStateChangeS2CPacket.class,ScoreboardDisplayS2CPacket.class,EntityTrackerUpdateS2CPacket.class,EntityAttachS2CPacket.class,EntityVelocityUpdateS2CPacket.class,EntityEquipmentUpdateS2CPacket.class,ExperienceBarUpdateS2CPacket.class,HealthUpdateS2CPacket.class,ScoreboardObjectiveUpdateS2CPacket.class,EntityPassengersSetS2CPacket.class,TeamS2CPacket.class,ScoreboardPlayerUpdateS2CPacket.class,SimulationDistanceS2CPacket.class,SubtitleS2CPacket.class,WorldTimeUpdateS2CPacket.class,TitleS2CPacket.class,TitleFadeS2CPacket.class,PlaySoundFromEntityS2CPacket.class,PlaySoundS2CPacket.class,StopSoundS2CPacket.class,GameMessageS2CPacket.class,PlayerListHeaderS2CPacket.class,NbtQueryResponseS2CPacket.class,ItemPickupAnimationS2CPacket.class,EntityPositionS2CPacket.class,AdvancementUpdateS2CPacket.class,EntityAttributesS2CPacket.class,EntityStatusEffectS2CPacket.class,SynchronizeRecipesS2CPacket.class,SynchronizeTagsS2CPacket.class})
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
