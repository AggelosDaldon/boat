package net.shirojr.boatism.mixin;

import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1690;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.item.BoatismItems;
import net.shirojr.boatism.util.LoggerUtil;
import net.shirojr.boatism.util.handler.BoatEngineHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1792.class})
public class ItemMixin {
   @Inject(
      method = {"useOnEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void boatism$debugBoatCoupling(class_1799 stack, class_1657 user, class_1309 entity, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      class_1937 world = entity.method_37908();
      class_1792 usedItem = stack.method_7909();
      if (entity instanceof BoatEngineEntity boatEngineEntity) {
         BoatEngineHandler engineHandler = boatEngineEntity.getEngineHandler();
         if (!world.method_8608()) {
            if (usedItem.equals(class_1802.field_8600) && user.method_5715()) {
               cir.setReturnValue(this.boatism$engineCoupling(user, boatEngineEntity, world));
            } else {
               if (usedItem.equals(BoatismItems.FUEL_BUCKET)) {
                  float leftOver = engineHandler.fillUpFuel((float)BoatEngineHandler.MAX_BASE_FUEL);
                  if (!user.method_7337()) {
                     stack.method_7934(1);
                     user.method_31548().method_7398(class_1802.field_8550.method_7854());
                  }

                  LoggerUtil.devLogger(String.format("Filled up fuel. %s was left over", leftOver));
                  cir.setReturnValue(class_1269.field_5812);
               }

            }
         }
      }
   }

   @Unique
   private class_1269 boatism$engineCoupling(class_1657 user, BoatEngineEntity boatEngineEntity, class_1937 world) {
      if (user.method_5715()) {
         boatEngineEntity.setLocked(!boatEngineEntity.isLocked());
         return class_1269.field_5811;
      } else {
         int boxSize = 5;
         class_238 box = new class_238(user.method_23317() - (double)boxSize, user.method_23318() - (double)boxSize, user.method_23321() - (double)boxSize, user.method_23317() + (double)boxSize, user.method_23318() + (double)boxSize, user.method_23321() + (double)boxSize);
         List<class_1690> boatList = world.method_18023(class_1299.field_6121, box, (boatEntity) -> true);
         boatList.addAll(world.method_18023(class_1299.field_38096, box, (chestBoatEntity) -> true));
         if (boatList.size() > 0) {
            class_1690 boat = (class_1690)boatList.get(0);
            boatEngineEntity.hookOntoBoatEntity(boat);
            LoggerUtil.devLogger(String.format("hooked engine to %s", boat.method_5477()));
            return class_1269.field_5812;
         } else {
            return class_1269.field_5811;
         }
      }
   }
}
