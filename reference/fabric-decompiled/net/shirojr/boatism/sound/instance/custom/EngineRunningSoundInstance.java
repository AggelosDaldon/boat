package net.shirojr.boatism.sound.instance.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.sound.instance.SoundInstanceState;

@Environment(EnvType.CLIENT)
public class EngineRunningSoundInstance extends BoatismSoundInstance implements SoundInstanceState {
   public EngineRunningSoundInstance(BoatEngineEntity entity) {
      super(entity, BoatismSounds.BOAT_ENGINE_DEFAULT, 100, 80);
   }

   public boolean method_26273() {
      return super.method_26273();
   }

   public void method_16896() {
      super.method_16896();
      if (!this.boatEngineEntity.isRunning() && !this.transitionState.equals(BoatismSoundInstance.TransitionState.FINISHING)) {
         this.finishSoundInstance();
      } else {
         BoatismSoundInstance.defaultSoundHandling(this);
         BoatismSoundInstance.transformSoundForTransition(this.field_5442, this.field_5441, this);
         BoatismSoundInstance.transformSoundForEngineLoad(this.field_5442, this.field_5441, this);
      }
   }

   public boolean isMainSound() {
      return true;
   }
}
