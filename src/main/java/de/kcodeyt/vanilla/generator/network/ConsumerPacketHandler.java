/*
 * Copyright 2022 KCodeYT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kcodeyt.vanilla.generator.network;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

/**
 * @author Kevims KCodeYT
 * @version 1.0-SNAPSHOT
 */
@AllArgsConstructor
public class ConsumerPacketHandler implements BedrockPacketHandler {

    private final Consumer<BedrockPacket> consumer;
    
    @Override
    public boolean handle(AdventureSettingsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AnimatePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AnvilDamagePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AvailableEntityIdentifiersPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(BlockEntityDataPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(BlockPickRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(BookEditPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ClientCacheBlobStatusPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ClientCacheMissResponsePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ClientCacheStatusPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ClientToServerHandshakePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CommandBlockUpdatePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CommandRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CompletedUsingItemPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ContainerClosePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CraftingEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EducationSettingsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EmotePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EntityEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EntityFallPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EntityPickRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(FilterTextPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(InteractPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ItemFrameDropItemPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LabTablePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LecternUpdatePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LevelEventGenericPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LevelSoundEvent1Packet packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LevelSoundEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LoginPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MapInfoRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MobArmorEquipmentPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ModalFormResponsePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MoveEntityAbsolutePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MultiplayerSettingsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(NetworkStackLatencyPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PhotoTransferPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerActionPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerAuthInputPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerHotbarPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerInputPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerSkinPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PurchaseReceiptPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(RequestChunkRadiusPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ResourcePackChunkRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ResourcePackClientResponsePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(RiderJumpPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ServerSettingsRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetDefaultGameTypePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetLocalPlayerAsInitializedPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SubClientLoginPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddBehaviorTreePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddHangingEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddItemEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddPaintingPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AvailableCommandsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(BlockEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(BossEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CameraPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ChangeDimensionPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ChunkRadiusUpdatedPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ClientboundMapItemDataPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CommandOutputPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ContainerOpenPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ContainerSetDataPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CraftingDataPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ExplodePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LevelChunkPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(GameRulesChangedPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(GuiDataPickItemPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(HurtArmorPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AutomationClientConnectPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LevelEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MapCreateLockedCopyPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MobEffectPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ModalFormRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MoveEntityDeltaPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(NetworkSettingsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(NpcRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(OnScreenTextureAnimationPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerListPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlaySoundPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(RemoveObjectivePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ResourcePackChunkDataPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ResourcePackDataInfoPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ScriptCustomEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ServerSettingsResponsePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetCommandsEnabledPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetDifficultyPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetDisplayObjectivePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetEntityDataPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetEntityLinkPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetEntityMotionPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetLastHurtByPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetScoreboardIdentityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetScorePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SettingsCommandPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SetTitlePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ShowCreditsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ShowProfilePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ShowStoreOfferPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SimpleEventPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SpawnExperienceOrbPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SpawnParticleEffectPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(StopSoundPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(StructureBlockUpdatePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(StructureTemplateDataRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(StructureTemplateDataResponsePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(TakeItemEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(TextPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(TickSyncPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(TransferPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateBlockPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateBlockPropertiesPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateBlockSyncedPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateEquipPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateSoftEnumPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateTradePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(BiomeDefinitionListPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(LevelSoundEvent2Packet packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(NetworkChunkPublisherUpdatePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(VideoStreamConnectPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CodeBuilderPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EmoteListPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ItemStackRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ItemStackResponsePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerArmorDamagePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerEnchantOptionsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CreativeContentPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdatePlayerGameTypePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PositionTrackingDBServerBroadcastPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PositionTrackingDBClientRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PacketViolationWarningPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(DebugInfoPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(MotionPredictionHintsPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AnimateEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CameraShakePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CorrectPlayerMovePredictionPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerFogPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ItemComponentPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ClientboundDebugRendererPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SyncEntityPropertyPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(AddVolumeEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(RemoveVolumeEntityPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(NpcDialoguePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SimulationTypePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(EduUriResourcePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CreatePhotoPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(UpdateSubChunkBlocksPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SubChunkPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(SubChunkRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PhotoInfoRequestPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(PlayerStartItemCooldownPacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(ScriptMessagePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

    @Override
    public boolean handle(CodeBuilderSourcePacket packet) {
        this.consumer.accept(packet);
        return true;
    }

}
