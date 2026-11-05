package net.shirojr.boatism.network;

import net.minecraft.class_2960;
import net.shirojr.boatism.Boatism;

public enum BoatismNetworkIdentifiers {
   SOUND_START("custom_sound_start_instance", BoatismNetworkIdentifiers.TargetSide.CLIENT),
   SOUND_END_ENGINE("custom_sound_stop_engine_instances", BoatismNetworkIdentifiers.TargetSide.CLIENT),
   SOUND_END_ALL("custom_sound_stop_all_instances", BoatismNetworkIdentifiers.TargetSide.CLIENT),
   SCROLLED("scrolled", BoatismNetworkIdentifiers.TargetSide.SERVER),
   BOAT_COMPONENT_SYNC("component_sync", BoatismNetworkIdentifiers.TargetSide.CLIENT);

   private final class_2960 identifier;
   private final TargetSide side;

   private BoatismNetworkIdentifiers(String packetName, TargetSide side) {
      this.identifier = new class_2960(Boatism.MODID, packetName);
      this.side = side;
   }

   public class_2960 getIdentifier() {
      return this.identifier;
   }

   public TargetSide getTargetSide() {
      return this.side;
   }

   // $FF: synthetic method
   private static BoatismNetworkIdentifiers[] $values() {
      return new BoatismNetworkIdentifiers[]{SOUND_START, SOUND_END_ENGINE, SOUND_END_ALL, SCROLLED, BOAT_COMPONENT_SYNC};
   }

   static enum TargetSide {
      CLIENT,
      SERVER;

      // $FF: synthetic method
      private static TargetSide[] $values() {
         return new TargetSide[]{CLIENT, SERVER};
      }
   }
}
