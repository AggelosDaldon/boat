package net.shirojr.boatism.item.custom.upgrade;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.shirojr.boatism.api.BoatEngineComponent;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.item.BoatismItems;

public class CanisterItem extends BoatismEquipmentItem implements BoatEngineComponent {
   public CanisterItem(class_1792.class_1793 settings) {
      super(settings);
   }

   public class_1799 getMountedItemStack(class_1799 originalStack) {
      class_1799 output = new class_1799(BoatismItems.COMPONENT_CANISTER_STRAPPED);
      output.method_7980(originalStack.method_7969());
      return output;
   }

   public class_1799 getReturnedItemStack(class_1799 displayedStack) {
      class_1799 output = new class_1799(BoatismItems.COMPONENT_CANISTER);
      output.method_7980(displayedStack.method_7969());
      return output;
   }

   public class_4587 itemRenderTransform(BoatEngineEntity boatEngineEntity, class_4587 matrixStack) {
      matrixStack.method_22903();
      float scaleFactor = 0.48F;
      matrixStack.method_22905(scaleFactor, scaleFactor, scaleFactor);
      matrixStack.method_22904(0.05, 2.3, (double)0.0F);
      matrixStack.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
      matrixStack.method_22907(class_7833.field_40716.rotationDegrees(180.0F));
      return matrixStack;
   }

   public List<class_1792> getConflictingParts() {
      return List.of(BoatismItems.COMPONENT_PLATES);
   }

   public float addedFuelCapacity() {
      return 5500.0F;
   }

   public float addedConsumedFuel() {
      return 0.2F;
   }
}
