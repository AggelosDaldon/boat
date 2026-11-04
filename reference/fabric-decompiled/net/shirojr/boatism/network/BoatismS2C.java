package net.shirojr.boatism.network;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.class_1297;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.shirojr.boatism.BoatismClient;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.instance.custom.BoatismSoundInstance;
import net.shirojr.boatism.sound.instance.custom.EngineLowFuelSoundInstance;
import net.shirojr.boatism.sound.instance.custom.EngineLowHealthSoundInstance;
import net.shirojr.boatism.sound.instance.custom.EngineOverheatingSoundInstance;
import net.shirojr.boatism.sound.instance.custom.EngineRunningSoundInstance;
import net.shirojr.boatism.sound.instance.custom.EngineSubmergedSoundInstance;
import net.shirojr.boatism.util.LoggerUtil;
import net.shirojr.boatism.util.sound.SoundInstanceIdentifier;

public class BoatismS2C {
   public static void registerClientReceivers() {
      ClientPlayNetworking.registerGlobalReceiver(BoatismNetworkIdentifiers.SOUND_START.getIdentifier(), BoatismS2C::handleSoundInstanceStartPackets);
      ClientPlayNetworking.registerGlobalReceiver(BoatismNetworkIdentifiers.SOUND_END_ALL.getIdentifier(), BoatismS2C::handleClearAllSoundInstancesPackets);
      ClientPlayNetworking.registerGlobalReceiver(BoatismNetworkIdentifiers.SOUND_END_ENGINE.getIdentifier(), BoatismS2C::handleStopTrackingEnginePackets);
      ClientPlayNetworking.registerGlobalReceiver(BoatismNetworkIdentifiers.BOAT_COMPONENT_SYNC.getIdentifier(), BoatismS2C::handleClientEngineComponentsSyncPackets);
   }

   private static void handleSoundInstanceStartPackets(class_310 client, class_634 clientPlayNetworkHandler, class_2540 clientBuf, PacketSender packetSender) {
      int entityId = clientBuf.method_10816();
      class_2960 identifier = clientBuf.method_10810();
      client.execute(() -> {
         if (client.field_1687 != null) {
            class_1297 patt2023$temp = client.field_1687.method_8469(entityId);
            if (patt2023$temp instanceof BoatEngineEntity) {
               BoatEngineEntity boatEngineEntity = (BoatEngineEntity)patt2023$temp;
               SoundInstanceIdentifier.fromIdentifier(identifier).ifPresent((soundInstanceIdentifier) -> {
                  BoatismSoundInstance soundInstance;
                  switch (soundInstanceIdentifier) {
                     case ENGINE_RUNNING:
                        soundInstance = new EngineRunningSoundInstance(boatEngineEntity);
                        break;
                     case ENGINE_RUNNING_UNDERWATER:
                        soundInstance = new EngineSubmergedSoundInstance(boatEngineEntity);
                        break;
                     case ENGINE_LOW_FUEL:
                        soundInstance = new EngineLowFuelSoundInstance(boatEngineEntity);
                        break;
                     case ENGINE_LOW_HEALTH:
                        soundInstance = new EngineLowHealthSoundInstance(boatEngineEntity);
                        break;
                     case ENGINE_OVERHEATING:
                        soundInstance = new EngineOverheatingSoundInstance(boatEngineEntity);
                        break;
                     case NO_SOUND:
                        BoatismClient.soundManager.stopAllSoundInstancesForBoatEngineEntity(boatEngineEntity, true);
                        return;
                     default:
                        LoggerUtil.LOGGER.error(String.format("Failed to play %s SoundInstance", soundInstanceIdentifier.getIdentifier().method_12832()));
                        return;
                  }

                  BoatismClient.soundManager.start(soundInstanceIdentifier, soundInstance);
               });
            }
         }
      });
   }

   private static void handleStopTrackingEnginePackets(class_310 client, class_634 clientPlayNetworkHandler, class_2540 clientBuf, PacketSender packetSender) {
      int entityId = clientBuf.method_10816();
      boolean force = clientBuf.readBoolean();
      client.execute(() -> {
         if (client.field_1687 != null) {
            class_1297 patt3972$temp = client.field_1687.method_8469(entityId);
            if (patt3972$temp instanceof BoatEngineEntity) {
               BoatEngineEntity boatEngine = (BoatEngineEntity)patt3972$temp;
               BoatismClient.soundManager.stopAllSoundInstancesForBoatEngineEntity(boatEngine, force);
            }
         }
      });
   }

   private static void handleClearAllSoundInstancesPackets(class_310 client, class_634 clientPlayNetworkHandler, class_2540 clientBuf, PacketSender packetSender) {
      client.execute(() -> BoatismClient.soundManager.stopAllSoundInstances(true));
   }

   private static void handleClientEngineComponentsSyncPackets(class_310 client, class_634 clientPlayNetworkHandler, class_2540 clientBuf, PacketSender packetSender) {
      int entityId = clientBuf.method_10816();
      int componentListSize = clientBuf.method_10816();
      List<BoatEngineEntity.EngineComponent> componentList = new ArrayList();

      for(int i = 0; i < componentListSize; ++i) {
         componentList.add(new BoatEngineEntity.EngineComponent(clientBuf.method_10816(), clientBuf.method_10819()));
      }

      client.execute(() -> {
         if (client.field_1687 != null) {
            class_638 clientWorld = client.field_1687;
            class_1297 patt5314$temp = clientWorld.method_8469(entityId);
            if (patt5314$temp instanceof BoatEngineEntity) {
               BoatEngineEntity boatEngine = (BoatEngineEntity)patt5314$temp;
               boatEngine.setMountedItemsFromComponentList(componentList);
            }
         }
      });
   }
}
