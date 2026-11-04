package net.shirojr.boatism.network;

import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_1297;
import net.minecraft.class_1690;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3419;
import net.minecraft.server.MinecraftServer;
import net.shirojr.boatism.api.BoatEngineCoupler;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.util.handler.BoatEngineHandler;

public class BoatismC2S {
   public static void registerServerReceivers() {
      ServerPlayNetworking.registerGlobalReceiver(BoatismNetworkIdentifiers.SCROLLED.getIdentifier(), BoatismC2S::handleScrollPackets);
   }

   private static void handleScrollPackets(MinecraftServer server, class_3222 player, class_3244 handler, class_2540 buf, PacketSender responseSender) {
      double delta = buf.readDouble();
      server.execute(() -> {
         class_1297 patt1434$temp = player.method_5854();
         if (patt1434$temp instanceof class_1690 boatEntity) {
            Optional<UUID> boatEngineEntityUuid = ((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid();
            boatEngineEntityUuid.ifPresent((uuid) -> {
               BoatEngineEntity boatEngineEntity = (BoatEngineEntity)((class_3218)player.method_37908()).method_14190(uuid);
               if (boatEngineEntity != null) {
                  BoatEngineHandler engineHandler = boatEngineEntity.getEngineHandler();
                  int newPowerLevel = engineHandler.getPowerLevel() + (int)delta;
                  newPowerLevel = Math.min(newPowerLevel, engineHandler.getMaxPowerLevel());
                  newPowerLevel = Math.max(newPowerLevel, 0);
                  if (newPowerLevel > engineHandler.getPowerLevel()) {
                     player.method_17356(BoatismSounds.BOAT_ENGINE_POWER_UP, class_3419.field_15254, 0.7F, 1.0F);
                  } else if (newPowerLevel < engineHandler.getPowerLevel()) {
                     player.method_17356(BoatismSounds.BOAT_ENGINE_POWER_DOWN, class_3419.field_15254, 0.7F, 1.0F);
                  }

                  engineHandler.setPowerLevel(newPowerLevel);
                  player.method_7353(class_2561.method_43469("mouse.boatism.power_level", new Object[]{newPowerLevel}), true);
               }
            });
         }
      });
   }
}
