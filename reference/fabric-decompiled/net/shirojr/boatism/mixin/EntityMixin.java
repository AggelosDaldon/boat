package net.shirojr.boatism.mixin;

import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1690;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.util.handler.EntityHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1297.class})
public abstract class EntityMixin {
   @Inject(
      method = {"remove"},
      at = {@At("HEAD")}
   )
   private void boatism$removeHookedBoatEngineEntries(CallbackInfo ci) {
      EntityHandler.removePossibleBoatEngineEntry((class_1297)this);
   }

   @Inject(
      method = {"discard"},
      at = {@At("HEAD")}
   )
   private void boatism$decoupleBoatEngineEntity(CallbackInfo ci) {
      EntityHandler.removePossibleBoatEngineEntry((class_1297)this);
   }

   @Redirect(
      method = {"addPassenger"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
)
   )
   protected boolean boatism$addPassengerMixin(List<class_1297> list, Object passenger) {
      class_1297 var4 = (class_1297)this;
      if (var4 instanceof class_1690 boatEntity) {
         if (boatEntity.method_5782()) {
            for(int i = 0; i < boatEntity.method_5685().size(); ++i) {
               if (boatEntity.method_5685().get(i) instanceof BoatEngineEntity) {
                  list.add(i, (class_1297)passenger);
                  return true;
               }
            }
         }
      }

      return list.add((class_1297)passenger);
   }

   @Inject(
      method = {"removeAllPassengers"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void boatism$engineRemovalCancel(CallbackInfo ci) {
      class_1297 var3 = (class_1297)this;
      if (var3 instanceof class_1690 boatEntity) {
         if (boatEntity.method_5782()) {
            if (boatEntity.method_5685().stream().noneMatch((entity) -> entity instanceof BoatEngineEntity)) {
               return;
            }

            for(int i = 0; i < boatEntity.method_5685().size(); ++i) {
               class_1297 passenger = (class_1297)boatEntity.method_5685().get(i);
               if (!(passenger instanceof BoatEngineEntity)) {
                  passenger.method_5848();
               }
            }

            ci.cancel();
         }
      }

   }
}
