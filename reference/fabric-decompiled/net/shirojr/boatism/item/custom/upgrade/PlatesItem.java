package net.shirojr.boatism.item.custom.upgrade;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.item.BoatismItems;

public class PlatesItem extends BoatismEquipmentItem {
   public PlatesItem(class_1792.class_1793 settings) {
      super(settings);
   }

   public List<class_1792> getConflictingParts() {
      return List.of(BoatismItems.COMPONENT_CANISTER, BoatismItems.COMPONENT_CANISTER_STRAPPED);
   }

   public float getAdditionalArmor() {
      return 20.0F;
   }

   public class_4587 itemRenderTransform(BoatEngineEntity boatEngineEntity, class_4587 matrixStack) {
      matrixStack.method_22903();
      matrixStack.method_22905(0.5F, 0.5F, 0.5F);
      matrixStack.method_22904((double)0.0F, 2.43, (double)0.0F);
      matrixStack.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
      matrixStack.method_22907(class_7833.field_40716.rotationDegrees(180.0F));
      return matrixStack;
   }

   public boolean waterProofsEngine() {
      return true;
   }
}
