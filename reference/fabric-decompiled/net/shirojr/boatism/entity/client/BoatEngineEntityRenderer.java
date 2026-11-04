package net.shirojr.boatism.entity.client;

import net.minecraft.class_1690;
import net.minecraft.class_2398;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_5617;
import net.minecraft.class_5819;
import net.minecraft.class_922;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.BoatismClient;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;

public class BoatEngineEntityRenderer extends class_922<BoatEngineEntity, BoatEngineEntityModel<BoatEngineEntity>> {
   private static final class_2960 TEXTURE;

   public BoatEngineEntityRenderer(class_5617.class_5618 ctx) {
      super(ctx, new BoatEngineEntityModel(ctx.method_32167(BoatismClient.BOAT_ENGINE_LAYER)), 0.4F);
      this.method_4046(new EquipedPartFeatureRenderer(this, ctx.method_32168()));
   }

   public class_2960 getTexture(BoatEngineEntity entity) {
      return TEXTURE;
   }

   public void render(BoatEngineEntity boatEngineEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int light) {
      super.method_4054(boatEngineEntity, f, g, matrixStack, vertexConsumerProvider, light);
      class_5819 random = boatEngineEntity.method_37908().method_8409();
      if (boatEngineEntity.getEngineHandler().isHeatingUp() && random.method_43048(20) == 0) {
         boatEngineEntity.method_37908().method_8406(class_2398.field_11240, boatEngineEntity.method_23317() + (double)(random.method_43057() * 0.1F), boatEngineEntity.method_23318() + (double)0.4F + (double)(random.method_43057() * 0.1F), boatEngineEntity.method_23321() + (double)(random.method_43057() * 0.1F), (double)0.0F, (double)0.0F, (double)0.0F);
      }

      if (boatEngineEntity.isRunning()) {
         if (boatEngineEntity.getPowerLevel() > 0) {
            boatEngineEntity.method_37908().method_8406(class_2398.field_11247, boatEngineEntity.method_23317() + (double)(random.method_43057() * 0.1F), boatEngineEntity.method_23318() - (double)0.4F + (double)(random.method_43057() * 0.1F), boatEngineEntity.method_23321() + (double)(random.method_43057() * 0.1F), (double)0.0F, (double)0.0F, (double)0.0F);
         }

         if (boatEngineEntity.getPowerLevel() > 4 && random.method_43048(20) == 0) {
            boatEngineEntity.method_37908().method_8406(class_2398.field_11251, boatEngineEntity.method_23317() + (double)(random.method_43057() * 0.1F), boatEngineEntity.method_23318() + (double)0.4F + (double)(random.method_43057() * 0.1F), boatEngineEntity.method_23321() + (double)(random.method_43057() * 0.1F), (double)0.0F, (double)0.0F, (double)0.0F);
         }

      }
   }

   public boolean shouldRender(BoatEngineEntity entity, class_4604 frustum, double x, double y, double z) {
      return super.method_3933(entity, frustum, x, y, z);
   }

   protected void renderLabelIfPresent(BoatEngineEntity entity, class_2561 text, class_4587 matrices, class_4597 vertexConsumers, int light) {
   }

   protected void setupTransforms(BoatEngineEntity entity, class_4587 matrices, float animationProgress, float bodyYaw, float tickDelta) {
      float scaleFactor = 1.5F;
      super.method_4058(entity, matrices, animationProgress, bodyYaw, tickDelta);
      if (entity.getHookedBoatEntity().isPresent()) {
         class_1690 var7 = (class_1690)entity.getHookedBoatEntity().get();
      }

      matrices.method_22905(scaleFactor, scaleFactor, scaleFactor);
   }

   static {
      TEXTURE = new class_2960(Boatism.MODID, "textures/entity/boatengine.png");
   }
}
