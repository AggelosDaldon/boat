package net.shirojr.boatism.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;

public interface BoatEngineComponent {
   default List<class_1792> getConflictingParts() {
      return new ArrayList();
   }

   default float addedThrust() {
      return 0.0F;
   }

   default float addedOverheatTolerance() {
      return 0.0F;
   }

   default float addedConsumedFuel() {
      return 0.0F;
   }

   default float addedFuelCapacity() {
      return 0.0F;
   }

   default float getAdditionalArmor() {
      return 0.0F;
   }

   default boolean waterProofsEngine() {
      return false;
   }

   default class_1799 getMountedItemStack(class_1799 originalStack) {
      return originalStack;
   }

   default class_1799 getReturnedItemStack(class_1799 displayedStack) {
      return displayedStack;
   }

   default class_4587 itemRenderTransform(BoatEngineEntity boatEngineEntity, class_4587 matrixStack) {
      matrixStack.method_22903();
      return matrixStack;
   }
}
