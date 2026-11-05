package net.shirojr.boatism.entity.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1921;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.class_630;
import net.shirojr.boatism.entity.animation.BoatismAnimation;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;

public class BoatEngineEntityModel<T extends BoatEngineEntity> extends class_5597<T> {
   private final List<class_630> parts = new ArrayList();
   private final class_630 root;
   private final class_630 top;
   private final class_630 rod;
   private final class_630 propeller;

   public BoatEngineEntityModel(class_630 base) {
      super(class_1921::method_23578);
      this.root = base.method_32086("root");
      this.top = this.root.method_32086("top");
      this.rod = this.root.method_32086("rod");
      this.propeller = this.rod.method_32086("propeller");
      this.parts.addAll(List.of(this.root, this.top, this.rod, this.propeller));
   }

   public static class_5607 getTexturedModelData() {
      class_5609 modelData = new class_5609();
      class_5610 modelPartData = modelData.method_32111();
      class_5610 root = modelPartData.method_32117("root", class_5606.method_32108(), class_5603.method_32090(0.0F, 23.0F, -5.0F));
      root.method_32117("top", class_5606.method_32108().method_32101(0, 0).method_32098(-3.0F, -7.0F, -4.0F, 6.0F, 3.0F, 9.0F, new class_5605(0.0F)).method_32101(11, 16).method_32098(-1.0F, -4.0F, -4.0F, 2.0F, 1.0F, 8.0F, new class_5605(0.0F)).method_32101(0, 14).method_32098(-2.0F, -6.0F, -5.0F, 4.0F, 4.0F, 5.0F, new class_5605(0.0F)).method_32101(24, 18).method_32098(-2.0F, -9.0F, 5.0F, 1.0F, 4.0F, 1.0F, new class_5605(0.0F)), class_5603.method_32090(0.0F, 2.0F, 5.0F));
      class_5610 rod = root.method_32117("rod", class_5606.method_32108().method_32101(0, 0).method_32098(-1.0F, -2.0F, -5.0F, 2.0F, 7.0F, 2.0F, new class_5605(0.0F)), class_5603.method_32090(0.0F, 2.0F, 5.0F));
      class_5610 propeller = rod.method_32117("propeller", class_5606.method_32108().method_32101(0, 10).method_32098(0.0F, -3.0F, -1.0F, 0.0F, 6.0F, 2.0F, new class_5605(0.0F)), class_5603.method_32091(0.0F, 3.0F, -2.0F, 0.0F, 0.0F, 0.7854F));
      propeller.method_32117("cube_r1", class_5606.method_32108().method_32101(0, 10).method_32098(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 2.0F, new class_5605(0.0F)), class_5603.method_32091(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.5708F));
      return class_5607.method_32110(modelData, 64, 64);
   }

   public void setAngles(BoatEngineEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
      this.root.method_32088().forEach(class_630::method_41923);
      float baseSpinSpeed = 3.0F;
      float powerLevelMultiplier = (float)entity.getPowerLevel() * 0.2F;
      this.method_43782(entity.leftSpinAnimationState, BoatismAnimation.SPIN_LEFT, animationProgress, baseSpinSpeed * powerLevelMultiplier);
      if (entity.isLocked()) {
         this.root.field_3654 = 0.7F;
      } else {
         this.root.field_3654 = 0.0F;
      }

   }

   public void method_2828(class_4587 matrices, class_4588 vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      float scaleFactor = 1.0F;
      matrices.method_46416(0.0F, 0.0F, 0.1F);
      matrices.method_22905(scaleFactor * 1.3F, scaleFactor, scaleFactor);
      this.root.method_22699(matrices, vertices, light, overlay, red, green, blue, alpha);
   }

   public class_630 method_32008() {
      return this.root;
   }
}
