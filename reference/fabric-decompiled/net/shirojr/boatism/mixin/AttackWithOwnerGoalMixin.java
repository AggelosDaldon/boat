package net.shirojr.boatism.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_1321;
import net.minecraft.class_1406;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1406.class})
public class AttackWithOwnerGoalMixin {
   @Shadow
   @Final
   private class_1321 field_6666;

   @Inject(
      method = {"canStart"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;getAttacking()Lnet/minecraft/entity/LivingEntity;"
)},
      cancellable = true
   )
   private void boatism$wolfTargetSelectorChange(CallbackInfoReturnable<Boolean> cir) {
      class_1309 owner = this.field_6666.method_35057();
      if (owner != null) {
         class_1309 target = owner.method_6052();
         if (target instanceof BoatEngineEntity) {
            cir.setReturnValue(false);
         }

      }
   }
}
