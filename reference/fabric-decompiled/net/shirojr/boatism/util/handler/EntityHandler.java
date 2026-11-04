package net.shirojr.boatism.util.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1690;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.shirojr.boatism.api.BoatEngineCoupler;
import net.shirojr.boatism.entity.BoatismEntities;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.util.nbt.BoatEngineNbtHelper;
import org.jetbrains.annotations.Nullable;

public class EntityHandler {
   private EntityHandler() {
   }

   public static Optional<BoatEngineEntity> getBoatEngineEntityFromUuid(@Nullable UUID uuid, class_1937 world, class_243 pos, int searchSize) {
      if (uuid == null) {
         return Optional.empty();
      } else {
         List<BoatEngineEntity> possibleEntities = world.method_18023(BoatismEntities.BOAT_ENGINE, class_238.method_30048(pos, (double)searchSize, (double)searchSize, (double)searchSize), (boatEngine) -> boatEngine.method_5667().equals(uuid));
         return possibleEntities.size() < 1 ? Optional.empty() : Optional.ofNullable((BoatEngineEntity)possibleEntities.get(0));
      }
   }

   public static void removePossibleBoatEngineEntry(class_1297 entity) {
      if (entity instanceof class_1690 boatEntity) {
         ((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid().flatMap((uuid) -> getBoatEngineEntityFromUuid(uuid, boatEntity.method_37908(), boatEntity.method_19538(), 10)).ifPresent((boatEngineEntity) -> {
            if (boatEngineEntity.method_37908() instanceof class_3218) {
               dropMountedInventory(boatEngineEntity, true, false);
               boatEngineEntity.removeBoatEngine(boatEntity);
            }
         });
      }
   }

   public static void dropItemStackFromMountedInventory(class_1799 itemStack, BoatEngineEntity boatEngineEntity) {
      boatEngineEntity.method_5775(itemStack);
   }

   public static void dropMountedInventory(BoatEngineEntity boatEngineEntity, boolean dropEngine, boolean dropEquipment) {
      List<class_1799> allEngineStacks = new ArrayList();
      if (dropEngine) {
         allEngineStacks.add(BoatEngineNbtHelper.getItemStackFromBoatEngineEntity(boatEngineEntity));
      }

      if (dropEquipment) {
         allEngineStacks.addAll(BoatEngineNbtHelper.getMountedItemsFromBoatEngineEntity(boatEngineEntity));
      }

      for(class_1799 entry : allEngineStacks) {
         boatEngineEntity.method_5775(entry);
      }

      for(int i = 0; i < boatEngineEntity.getMountedInventory().method_5439(); ++i) {
         boatEngineEntity.getMountedInventory().method_5447(i, class_1799.field_8037);
      }

   }

   public static void engineLinkCleanUp(class_1690 boatEntity) {
      if (boatEntity instanceof BoatEngineCoupler boatLink) {
         if (!boatLink.boatism$getBoatEngineEntityUuid().isEmpty()) {
            if (boatEntity.method_5685().stream().noneMatch((entity) -> entity instanceof BoatEngineEntity)) {
               boatLink.boatism$setBoatEngineEntity((UUID)null);
            }

         }
      }
   }
}
