package net.shirojr.boatism.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.class_1928;
import net.minecraft.class_1928.class_5198;

public class BoatismGameRules {
   public static final class_1928.class_4313<class_1928.class_4310> DESTRUCTIVE_ENGINE_EXPLOSION;

   public static void register() {
   }

   static {
      DESTRUCTIVE_ENGINE_EXPLOSION = GameRuleRegistry.register("engineExplosionDestruction", class_5198.field_24100, GameRuleFactory.createBooleanRule(true));
   }
}
