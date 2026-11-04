package net.shirojr.boatism.sound.instance;

import java.util.List;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.util.sound.SoundInstanceIdentifier;

public interface SoundInstanceState {
   boolean isMainSound();

   default List<SoundInstanceIdentifier> unsupportedInstances() {
      return List.of();
   }

   default boolean canRunIfEngineIsTurnedOff(BoatEngineEntity boatEngine) {
      return false;
   }
}
