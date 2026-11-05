package net.shirojr.boatism.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_7260;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_7260.class})
public class WardenEntityMixin {
   @Inject(
      method = {"isValidTarget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void boatism$handleWardenTargetingForEngine(class_1297 entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof BoatEngineEntity boatEngine) {
         if (!boatEngine.getEngineHandler().engineIsRunning()) {
            cir.setReturnValue(false);
         }

      }
   }
}
