package net.shirojr.boatism.util.tag;

import net.minecraft.class_1299;
import net.minecraft.class_2960;
import net.minecraft.class_6862;
import net.minecraft.class_7924;
import net.shirojr.boatism.util.LoggerUtil;

public class BoatismTags {
   public static void register() {
      BoatismTags.Entities.register();
      LoggerUtil.devLogger("initialized tags");
   }

   public static class Entities {
      public static class_6862<class_1299<?>> NOT_HOOKABLE = createTag("not_hookable");

      private static class_6862<class_1299<?>> createTag(String name) {
         return class_6862.method_40092(class_7924.field_41266, new class_2960("boatism", name));
      }

      private static void register() {
         LoggerUtil.devLogger("initialized tags for entities");
      }
   }
}
