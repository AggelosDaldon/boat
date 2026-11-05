package net.shirojr.boatism.item;

import java.util.List;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.class_1761;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.item.custom.BaseEngineItem;
import net.shirojr.boatism.item.custom.FuelBucketItem;
import net.shirojr.boatism.item.custom.upgrade.BoatismEquipmentItem;
import net.shirojr.boatism.item.custom.upgrade.CanisterItem;
import net.shirojr.boatism.item.custom.upgrade.ExhaustItem;
import net.shirojr.boatism.item.custom.upgrade.PlatesItem;
import net.shirojr.boatism.util.LoggerUtil;

public class BoatismItems {
   public static FuelBucketItem FUEL_BUCKET = (FuelBucketItem)register("fuel_bucket", new FuelBucketItem((new FabricItemSettings()).maxCount(1)));
   public static BaseEngineItem BASE_ENGINE = (BaseEngineItem)register("base_engine", new BaseEngineItem((new FabricItemSettings()).maxCount(1).fireproof()));
   public static BoatismEquipmentItem COMPONENT_EXHAUST = (BoatismEquipmentItem)register("component_exhaust", new ExhaustItem((new FabricItemSettings()).maxCount(1)));
   public static CanisterItem COMPONENT_CANISTER = (CanisterItem)register("component_canister", new CanisterItem((new FabricItemSettings()).maxCount(1)));
   public static CanisterItem COMPONENT_CANISTER_STRAPPED = (CanisterItem)register("component_canister_strapped", new CanisterItem((new FabricItemSettings()).maxCount(1)));
   public static BoatismEquipmentItem COMPONENT_PLATES = (BoatismEquipmentItem)register("component_plates", new PlatesItem((new FabricItemSettings()).maxCount(1)));
   public static class_5321<class_1761> BOATISM_ITEM_GROUP;

   private static <T extends class_1792> T register(String name, T item) {
      class_2960 identifier = new class_2960(Boatism.MODID, name);
      return (T)(class_2378.method_10230(class_7923.field_41178, identifier, item));
   }

   private static class_5321<class_1761> registerItemGroup(String name, class_2561 displayName, class_1799 displayItemStack) {
      class_1761 group = FabricItemGroup.builder().method_47320(() -> displayItemStack).method_47321(displayName).method_47324();
      class_2960 groupIdentifier = new class_2960(Boatism.MODID, name);
      class_2378.method_10230(class_7923.field_44687, groupIdentifier, group);
      return class_5321.method_29179(class_7924.field_44688, groupIdentifier);
   }

   private static void initializeItemGroups() {
      ItemGroupEvents.modifyEntriesEvent(BOATISM_ITEM_GROUP).register((ItemGroupEvents.ModifyEntries)(boatismEntries) -> {
         List<class_1799> boatismList = class_7923.field_41178.method_10220().filter((item) -> {
            List<class_1792> ungroupedItems = List.of(COMPONENT_CANISTER_STRAPPED);
            if (!class_7923.field_41178.method_10221(item).method_12836().equals(Boatism.MODID)) {
               return false;
            } else {
               return !ungroupedItems.contains(item);
            }
         }).map(class_1792::method_7854).toList();
         boatismEntries.method_45423(boatismList);
      });
   }

   public static void initialize() {
      initializeItemGroups();
      LoggerUtil.devLogger("initialized items");
   }

   static {
      BOATISM_ITEM_GROUP = registerItemGroup("boatism", class_2561.method_43471("itemgroup.boatism.boatism"), new class_1799(BASE_ENGINE));
   }
}
