package net.shirojr.boatism.sound.instance.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.sound.instance.SoundInstanceState;
import net.shirojr.boatism.util.handler.BoatEngineHandler;

@Environment(EnvType.CLIENT)
public class EngineOverheatingSoundInstance extends BoatismSoundInstance implements SoundInstanceState {
   private static final int COOL_BUFFER_MAX = 3;
   private int coolingBuffer = 0;

   public EngineOverheatingSoundInstance(BoatEngineEntity boatEngineEntity) {
      super(boatEngineEntity, BoatismSounds.BOAT_ENGINE_HEAT, 60, 60);
      this.transitionState = BoatismSoundInstance.TransitionState.STARTING;
   }

   public boolean method_26273() {
      return super.method_26273();
   }

   public boolean isMainSound() {
      return false;
   }

   public boolean canRunIfEngineIsTurnedOff(BoatEngineEntity boatEngine) {
      return true;
   }

   public void method_16896() {
      super.method_16896();
      float normalizedOverheatTicks = this.engineHandler.getOverheat() / (float)BoatEngineHandler.MAX_BASE_OVERHEAT;
      boolean isPotentiallyCooling = !this.engineHandler.isHeatingUp();
      if (isPotentiallyCooling) {
         if (this.coolingBuffer > 3) {
            if (!this.transitionState.equals(BoatismSoundInstance.TransitionState.FINISHING)) {
               this.finishSoundInstance();
               return;
            }
         } else {
            --this.coolingBuffer;
         }
      } else {
         this.coolingBuffer = 0;
      }

      BoatismSoundInstance.defaultSoundHandling(this);
      this.field_5442 = class_3532.method_16439(normalizedOverheatTicks, 0.0F, 0.7F);
   }
}
