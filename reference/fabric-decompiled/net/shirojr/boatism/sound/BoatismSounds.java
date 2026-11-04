package net.shirojr.boatism.sound;

import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_7923;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.util.LoggerUtil;

public class BoatismSounds {
   public static class_3414 BOAT_ENGINE_DEFAULT = of("boat_engine_default");
   public static class_3414 BOAT_ENGINE_UNDERWATER = of("boat_engine_underwater");
   public static class_3414 BOAT_ENGINE_LOW_FUEL = of("boat_engine_low_fuel");
   public static class_3414 BOAT_ENGINE_LOW_HEALTH = of("boat_engine_low_health");
   public static class_3414 BOAT_ENGINE_HIT = of("boat_engine_hit");
   public static class_3414 BOAT_ENGINE_POWER_UP = of("boat_engine_power_up");
   public static class_3414 BOAT_ENGINE_POWER_DOWN = of("boat_engine_power_down");
   public static class_3414 BOAT_ENGINE_START = of("boat_engine_start");
   public static class_3414 BOAT_ENGINE_START_FAIL = of("boat_engine_start_fail");
   public static class_3414 BOAT_ENGINE_STOP = of("boat_engine_stop");
   public static class_3414 BOAT_ENGINE_HEAT = of("boat_engine_heat");
   public static class_3414 BOAT_ENGINE_FILL_UP = of("boat_engine_pour");
   public static class_3414 BOAT_ENGINE_EQUIP = of("boat_engine_equip");

   static class_3414 of(String id) {
      class_3414 sound = class_3414.method_47908(new class_2960(Boatism.MODID, id));
      return (class_3414)class_2378.method_10230(class_7923.field_41172, new class_2960(Boatism.MODID, id), sound);
   }

   public static void initializeSounds() {
      LoggerUtil.devLogger("Registering " + Boatism.MODID + " Sounds");
   }
}
