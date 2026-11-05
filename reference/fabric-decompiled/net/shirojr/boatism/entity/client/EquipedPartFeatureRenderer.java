package net.shirojr.boatism.entity.client;

import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_583;
import net.minecraft.class_811;
import net.minecraft.class_918;
import net.shirojr.boatism.api.BoatEngineComponent;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;

public class EquipedPartFeatureRenderer<T extends class_1309, M extends class_583<T>> extends class_3887<T, M> {
   private final class_918 itemRenderer;

   public EquipedPartFeatureRenderer(class_3883<T, M> context, class_918 itemRenderer) {
      super(context);
      this.itemRenderer = itemRenderer;
   }

   public void render(class_4587 matrices, class_4597 vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
      if (entity instanceof BoatEngineEntity boatEngine) {
         for(class_1799 stack : boatEngine.getMountedInventory().method_54454()) {
            if (!stack.method_7960()) {
               class_1792 var15 = stack.method_7909();
               if (var15 instanceof BoatEngineComponent) {
                  BoatEngineComponent component = (BoatEngineComponent)var15;
                  class_1799 displayedStack = component.getMountedItemStack(stack);
                  matrices = component.itemRenderTransform(boatEngine, matrices);
                  this.renderItem(displayedStack, light, matrices, vertexConsumers, boatEngine.method_37908(), boatEngine.method_5628());
                  matrices.method_22909();
               }
            }
         }

      }
   }

   private void renderItem(class_1799 stack, int light, class_4587 matrices, class_4597 vertexConsumer, class_1937 world, int seed) {
      this.itemRenderer.method_23178(stack, class_811.field_4319, light, class_4608.field_21444, matrices, vertexConsumer, world, seed);
   }
}
