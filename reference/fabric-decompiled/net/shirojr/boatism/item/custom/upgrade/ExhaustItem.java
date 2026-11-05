package net.shirojr.boatism.item.custom.upgrade;

import net.minecraft.class_1792;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;

public class ExhaustItem extends BoatismEquipmentItem {
   public ExhaustItem(class_1792.class_1793 settings) {
      super(settings);
   }

   public float addedOverheatTolerance() {
      return 2000.0F;
   }

   public class_4587 itemRenderTransform(BoatEngineEntity boatEngineEntity, class_4587 matrixStack) {
      matrixStack.method_22903();
      matrixStack.method_22905(0.82F, 0.82F, 0.82F);
      matrixStack.method_22904((double)0.0F, 1.4, (double)0.0F);
      matrixStack.method_22907(class_7833.field_40714.rotationDegrees(180.0F));
      matrixStack.method_22907(class_7833.field_40716.rotationDegrees(180.0F));
      return matrixStack;
   }
}
