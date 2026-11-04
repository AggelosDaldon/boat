package net.shirojr.boatism.util;

import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_5362;
import net.shirojr.boatism.gamerule.BoatismGameRules;

public class BoatEngineExplosionBehaviour extends class_5362 {
   public boolean method_55504(class_1927 explosion, class_1297 entity) {
      return super.method_55504(explosion, entity);
   }

   public boolean method_29554(class_1927 explosion, class_1922 world, class_2338 pos, class_2680 state, float power) {
      if (explosion.method_46406() == null) {
         return super.method_29554(explosion, world, pos, state, power);
      } else {
         class_1937 serverWorld = explosion.method_46406().method_37908();
         return serverWorld.method_8450().method_8355(BoatismGameRules.DESTRUCTIVE_ENGINE_EXPLOSION);
      }
   }
}
