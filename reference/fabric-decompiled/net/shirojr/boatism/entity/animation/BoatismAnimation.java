package net.shirojr.boatism.entity.animation;

import net.minecraft.class_7179;
import net.minecraft.class_7184;
import net.minecraft.class_7186;
import net.minecraft.class_7187;
import net.minecraft.class_7179.class_7181;
import net.minecraft.class_7179.class_7183;
import net.minecraft.class_7184.class_7185;

public class BoatismAnimation {
   public static float SPIN_DURATION_IN_SEC = 1.0F;
   public static final class_7184 SPIN_RIGHT;
   public static final class_7184 SPIN_LEFT;

   private BoatismAnimation() {
   }

   static {
      SPIN_RIGHT = class_7185.method_41818(SPIN_DURATION_IN_SEC).method_41817().method_41820("propeller", new class_7179(class_7183.field_37887, new class_7186[]{new class_7186(0.0F, class_7187.method_41829(0.0F, 0.0F, 0.0F), class_7181.field_37884), new class_7186(1.0F, class_7187.method_41829(0.0F, 0.0F, -360.0F), class_7181.field_37884)})).method_41821();
      SPIN_LEFT = class_7185.method_41818(SPIN_DURATION_IN_SEC).method_41817().method_41820("propeller", new class_7179(class_7183.field_37887, new class_7186[]{new class_7186(0.0F, class_7187.method_41829(0.0F, 0.0F, 0.0F), class_7181.field_37884), new class_7186(1.0F, class_7187.method_41829(0.0F, 0.0F, 360.0F), class_7181.field_37884)})).method_41821();
   }
}
