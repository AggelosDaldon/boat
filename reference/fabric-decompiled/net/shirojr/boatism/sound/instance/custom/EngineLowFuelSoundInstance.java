package net.shirojr.boatism.sound.instance.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.sound.instance.SoundInstanceState;

@Environment(EnvType.CLIENT)
public class EngineLowFuelSoundInstance extends BoatismSoundInstance implements SoundInstanceState {
   public EngineLowFuelSoundInstance(BoatEngineEntity entity) {
      super(entity, BoatismSounds.BOAT_ENGINE_LOW_FUEL, 100, 80);
   }

   public boolean method_26273() {
      return super.method_26273() && this.boatEngineEntity.getEngineHandler().isLowOnFuel();
   }

   public void method_16896() {
      super.method_16896();
      boolean shouldStop = !this.boatEngineEntity.getEngineHandler().isLowOnFuel() || !this.boatEngineEntity.isRunning();
      if (shouldStop && !this.transitionState.equals(BoatismSoundInstance.TransitionState.FINISHING)) {
         this.finishSoundInstance();
      } else {
         BoatismSoundInstance.defaultSoundHandling(this);
         transformSoundForTransition(this.field_5442, this.field_5441, this, false, true);
      }
   }

   public boolean isMainSound() {
      return true;
   }
}
