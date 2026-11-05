package net.shirojr.boatism.item.custom;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_3419;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.BoatismSounds;

public class FuelBucketItem extends class_1792 {
   public static final float MAX_CAPACITY = 3000.0F;

   public FuelBucketItem(class_1792.class_1793 settings) {
      super(settings);
   }

   public class_1269 method_7847(class_1799 stack, class_1657 user, class_1309 entity, class_1268 hand) {
      if (entity instanceof BoatEngineEntity boatEngine) {
         if (boatEngine.isRunning()) {
            user.method_7353(class_2561.method_43471("warning.boatism.engine_is_running"), true);
            return class_1269.field_5811;
         } else if (user.method_37908().method_8608()) {
            return class_1269.field_5812;
         } else {
            if (!containsBucketFuelNbt(stack)) {
               setFuelForItemStack(stack, 3000.0F);
            }

            boatEngine.getEngineHandler().fillUpFuel(getFuelFromItemStack(stack));
            if (!user.method_7337()) {
               stack.method_7934(1);
               user.method_31548().method_7398(class_1802.field_8550.method_7854());
            }

            user.method_37908().method_43128((class_1657)null, boatEngine.method_23317(), boatEngine.method_23318(), boatEngine.method_23321(), BoatismSounds.BOAT_ENGINE_FILL_UP, class_3419.field_15254, 1.0F, 1.0F);
            return class_1269.field_5812;
         }
      } else {
         return super.method_7847(stack, user, entity, hand);
      }
   }

   public static float getFuelFromItemStack(class_1799 stack) {
      class_2487 nbt = stack.method_7948();
      return !nbt.method_10545("BucketFuel") ? 0.0F : nbt.method_10583("BucketFuel");
   }

   public static void setFuelForItemStack(class_1799 stack, float value) {
      stack.method_7948().method_10548("BucketFuel", value);
   }

   public static boolean containsBucketFuelNbt(class_1799 stack) {
      class_2487 nbt = stack.method_7948();
      return nbt.method_10545("BucketFuel");
   }
}
