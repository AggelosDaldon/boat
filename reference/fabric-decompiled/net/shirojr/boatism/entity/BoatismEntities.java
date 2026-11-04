package net.shirojr.boatism.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_4048;
import net.minecraft.class_7923;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.util.LoggerUtil;
import org.jetbrains.annotations.NotNull;

public class BoatismEntities {
   public static final class_1299<BoatEngineEntity> BOAT_ENGINE;

   private static <E extends class_1297, T extends class_1299<E>> T register(@NotNull String name, @NotNull T entityType) {
      return (T)(class_2378.method_10230(class_7923.field_41177, new class_2960(Boatism.MODID, name), entityType));
   }

   public static void initialize() {
      LoggerUtil.devLogger("initialized entity types");
   }

   static {
      BOAT_ENGINE = register("boat_engine", FabricEntityTypeBuilder.create(class_1311.field_17715, BoatEngineEntity::new).dimensions(class_4048.method_18384(0.7F, 0.7F)).trackedUpdateRate(1).spawnableFarFromPlayer().build());
   }
}
