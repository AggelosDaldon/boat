package net.shirojr.boatism.sound.instance.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1101;
import net.minecraft.class_1113;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.util.handler.BoatEngineHandler;

@Environment(EnvType.CLIENT)
public class BoatismSoundInstance extends class_1101 {
   protected BoatEngineEntity boatEngineEntity;
   protected BoatEngineHandler engineHandler;
   protected final int startTransitionTicks;
   protected final int endTransitionTicks;
   protected TransitionState transitionState;
   protected int currentTick = 0;
   protected int transitionTick = 0;
   protected float distance = 0.0F;
   protected boolean isFinished = false;

   public BoatismSoundInstance(BoatEngineEntity boatEngineEntity, class_3414 soundEvent, int startTransitionTicks, int endTransitionTicks) {
      super(soundEvent, class_3419.field_15254, class_1113.method_43221());
      this.boatEngineEntity = boatEngineEntity;
      this.engineHandler = boatEngineEntity.getEngineHandler();
      this.field_5446 = true;
      this.field_5451 = 0;
      this.startTransitionTicks = startTransitionTicks;
      this.endTransitionTicks = endTransitionTicks;
      this.transitionState = BoatismSoundInstance.TransitionState.STARTING;
      this.field_5442 = 0.0F;
      this.field_5439 = boatEngineEntity.method_23317();
      this.field_5450 = boatEngineEntity.method_23318();
      this.field_5449 = boatEngineEntity.method_23321();
   }

   public void method_16896() {
      if (this.boatEngineEntity.method_37908().method_54719().method_54751()) {
         ++this.currentTick;
         if (this.boatEngineEntity.method_31481() || this.boatEngineEntity.method_29504()) {
            this.finishSoundInstance();
         }

         if (this.transitionState.equals(BoatismSoundInstance.TransitionState.STARTING)) {
            ++this.transitionTick;
            if (this.transitionTick >= this.startTransitionTicks) {
               this.transitionState = BoatismSoundInstance.TransitionState.IDLE;
               this.transitionTick = 0;
            }
         }

         if (this.transitionState.equals(BoatismSoundInstance.TransitionState.FINISHING)) {
            if (this.transitionTick < this.endTransitionTicks) {
               ++this.transitionTick;
            } else {
               this.isFinished = true;
            }
         }

         this.field_5439 = this.boatEngineEntity.method_23317();
         this.field_5450 = this.boatEngineEntity.method_23318();
         this.field_5449 = this.boatEngineEntity.method_23321();
         if (this.isFinished()) {
            this.transitionState = BoatismSoundInstance.TransitionState.STARTING;
            this.currentTick = 0;
            this.transitionTick = 0;
            this.method_24876();
         }

      }
   }

   public boolean method_26273() {
      return !this.boatEngineEntity.method_5701() && this.boatEngineEntity.method_5805();
   }

   public boolean method_4785() {
      return true;
   }

   protected static void defaultSoundHandling(BoatismSoundInstance soundInstance) {
      boolean shouldTick = soundInstance.boatEngineEntity.method_37908().method_54719().method_54751();
      if (!shouldTick) {
         soundInstance.distance = 0.0F;
         soundInstance.field_5442 = 0.0F;
         soundInstance.field_5441 = 1.0F;
      } else {
         transformSoundForDistance(soundInstance);
      }

   }

   protected static void transformSoundForDistance(BoatismSoundInstance soundInstance) {
      soundInstance.boatEngineEntity.getHookedBoatEntity().ifPresentOrElse((boatEntity) -> {
         double horizontalVelocity = soundInstance.boatEngineEntity.method_18798().method_37267();
         soundInstance.distance = class_3532.method_15363(soundInstance.distance + 0.0025F, 0.0F, 1.0F);
         float velocityClamp = (float)class_3532.method_15350(horizontalVelocity, (double)0.0F, (double)0.5F);
         soundInstance.field_5442 = class_3532.method_16439(velocityClamp, 0.0F, 0.7F);
         soundInstance.field_5441 = class_3532.method_16439(velocityClamp, 0.9F, 1.2F);
      }, () -> {
         soundInstance.field_5442 = 0.7F;
         soundInstance.field_5441 = 1.0F;
      });
   }

   protected static void transformSoundForTransition(float originalVolume, float originalPitch, BoatismSoundInstance soundInstance) {
      transformSoundForTransition(originalVolume, originalPitch, soundInstance, true, true);
   }

   protected static void transformSoundForTransition(float originalVolume, float originalPitch, BoatismSoundInstance soundInstance, boolean includeStarting, boolean includeStopping) {
      float normalizedStartTransitionTick = (float)soundInstance.transitionTick / (float)soundInstance.startTransitionTicks;
      float normalizedEndTransitionTick = (float)soundInstance.transitionTick / (float)soundInstance.endTransitionTicks;
      switch (soundInstance.transitionState) {
         case STARTING:
            if (includeStarting) {
               soundInstance.field_5442 = class_3532.method_16439(normalizedStartTransitionTick, 0.0F, originalVolume);
               soundInstance.field_5441 = class_3532.method_16439(normalizedStartTransitionTick, originalPitch - 0.2F, originalPitch);
            }
            break;
         case FINISHING:
            if (includeStopping) {
               soundInstance.field_5442 = class_3532.method_16439(normalizedEndTransitionTick, originalVolume, 0.0F);
               soundInstance.field_5441 = class_3532.method_16439(normalizedEndTransitionTick, originalPitch, originalPitch - 0.2F);
            }
      }

   }

   protected static void transformSoundForEngineLoad(float originalVolume, float originalPitch, BoatismSoundInstance soundInstance) {
      BoatEngineHandler boatEngineHandler = soundInstance.engineHandler;
      float normalizedPowerLevel = (float)boatEngineHandler.getPowerLevel() * 0.1F;
      soundInstance.field_5442 = class_3532.method_16439(normalizedPowerLevel, originalVolume - 0.1F, originalVolume);
      soundInstance.field_5441 = class_3532.method_16439(normalizedPowerLevel, originalPitch - 0.2F, originalPitch + 0.1F);
   }

   public void finishSoundInstance() {
      this.transitionState = BoatismSoundInstance.TransitionState.FINISHING;
   }

   public boolean isFinished() {
      return this.isFinished;
   }

   public BoatEngineEntity getBoatEngineEntity() {
      return this.boatEngineEntity;
   }

   public boolean equals(Object obj) {
      if (obj instanceof BoatismSoundInstance soundInstance) {
         if (!soundInstance.getBoatEngineEntity().equals(this.getBoatEngineEntity())) {
            return false;
         } else {
            return soundInstance.method_4775().equals(this.method_4775());
         }
      } else {
         return false;
      }
   }

   protected static enum TransitionState {
      STARTING,
      FINISHING,
      IDLE;

      // $FF: synthetic method
      private static TransitionState[] $values() {
         return new TransitionState[]{STARTING, FINISHING, IDLE};
      }
   }
}
