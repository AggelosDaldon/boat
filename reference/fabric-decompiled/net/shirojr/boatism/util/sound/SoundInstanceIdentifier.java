package net.shirojr.boatism.util.sound;

import java.util.Optional;
import net.minecraft.class_2960;
import net.shirojr.boatism.Boatism;

public enum SoundInstanceIdentifier {
   ENGINE_RUNNING("engine_running"),
   ENGINE_RUNNING_UNDERWATER("engine_underwater"),
   ENGINE_LOW_FUEL("engine_low_fuel"),
   ENGINE_LOW_HEALTH("engine_low_health"),
   ENGINE_OVERHEATING("engine_overheating"),
   NO_SOUND("cancel_sound_instances");

   private final class_2960 identifier;

   private SoundInstanceIdentifier(String soundInstanceName) {
      this.identifier = new class_2960(Boatism.MODID, soundInstanceName);
   }

   public class_2960 getIdentifier() {
      return this.identifier;
   }

   public static Optional<SoundInstanceIdentifier> fromIdentifier(class_2960 identifier) {
      for(SoundInstanceIdentifier instance : values()) {
         if (instance.identifier.equals(identifier)) {
            return Optional.of(instance);
         }
      }

      return Optional.empty();
   }

   // $FF: synthetic method
   private static SoundInstanceIdentifier[] $values() {
      return new SoundInstanceIdentifier[]{ENGINE_RUNNING, ENGINE_RUNNING_UNDERWATER, ENGINE_LOW_FUEL, ENGINE_LOW_HEALTH, ENGINE_OVERHEATING, NO_SOUND};
   }
}
