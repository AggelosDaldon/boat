package net.shirojr.boatism.util.nbt;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1690;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2379;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.shirojr.boatism.api.BoatEngineComponent;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.item.BoatismItems;

public class BoatEngineNbtHelper {
   public static void writeItemStacksToNbt(List<class_1799> stacks, String name, class_2487 nbt) {
      class_2499 nbtList = new class_2499();

      for(class_1799 itemStack : stacks) {
         class_2487 nbtCompound = new class_2487();
         if (!itemStack.method_7960()) {
            itemStack.method_7953(nbtCompound);
         }

         nbtList.add(nbtCompound);
      }

      nbt.method_10566(name, nbtList);
   }

   public static List<class_1799> readItemStacksFromNbt(class_2487 nbt, String name) {
      List<class_1799> stacks = new ArrayList();
      if (nbt.method_10573(name, 9)) {
         class_2499 nbtList = nbt.method_10554(name, 10);

         for(int i = 0; i < nbtList.size(); ++i) {
            stacks.add(class_1799.method_7915(nbtList.method_10602(i)));
         }
      }

      return stacks;
   }

   public static class_1799 getItemStackFromBoatEngineEntity(BoatEngineEntity engineEntity) {
      class_1799 engineStack = new class_1799(BoatismItems.BASE_ENGINE);
      class_2487 nbt = engineStack.method_7948();
      List<class_1799> mountedList = engineEntity.getMountedInventory().method_54454().stream().toList();
      engineEntity.getHookedBoatEntityUuid().ifPresent((hookedBoatEntityUuid) -> nbt.method_25927("HookedEntity", hookedBoatEntityUuid));
      writeItemStacksToNbt(mountedList, "MountedItems", nbt);
      nbt.method_10556("IsRunning", engineEntity.isRunning());
      nbt.method_10569("PowerOutput", engineEntity.getPowerLevel());
      nbt.method_10548("Overheat", engineEntity.getOverheat());
      nbt.method_10566("EngineRotation", engineEntity.getArmRotation().method_10255());
      nbt.method_10556("IsSubmerged", engineEntity.isSubmerged());
      nbt.method_10548("Fuel", engineEntity.getFuel());
      nbt.method_10556("IsLocked", engineEntity.isLocked());
      return engineStack;
   }

   public static List<class_1799> getMountedItemsFromBoatEngineEntity(BoatEngineEntity engineEntity) {
      List<class_1799> returnedItemStacks = new ArrayList();
      engineEntity.getMountedInventory().method_54454().forEach((stack) -> {
         class_1792 patt2904$temp = stack.method_7909();
         if (patt2904$temp instanceof BoatEngineComponent component) {
            returnedItemStacks.add(component.getReturnedItemStack(stack));
         }

      });
      return returnedItemStacks;
   }

   public static BoatEngineEntity getBoatEngineEntityFromItemStack(class_1799 stack, class_1690 linkedBoat) {
      BoatEngineEntity boatEngine = new BoatEngineEntity(linkedBoat.method_37908(), linkedBoat);
      class_2487 stackNbt = stack.method_7948();
      if (stackNbt.method_10545("MountedItems")) {
         boatEngine.setMountedItemsFromItemStackList(readItemStacksFromNbt(stackNbt, "MountedItems"));
      }

      boatEngine.setIsRunning(stackNbt.method_10577("IsRunning"));
      boatEngine.setPowerLevel(Math.min(stackNbt.method_10550("PowerOutput"), 4));
      float overheat = stackNbt.method_10583("Overheat");
      if ((double)overheat > (double)boatEngine.getEngineHandler().getMaxOverHeatCapacity() * 0.8) {
         overheat = boatEngine.getEngineHandler().getMaxOverHeatCapacity() * 0.8F;
      }

      boatEngine.setOverheat(overheat);
      boatEngine.setArmRotation(new class_2379(stackNbt.method_10554("EngineRotation", 5)));
      boatEngine.setSubmerged(stackNbt.method_10577("IsSubmerged"));
      boatEngine.setFuel(stackNbt.method_10583("Fuel"));
      boatEngine.setLocked(stackNbt.method_10577("IsLocked"));
      return boatEngine;
   }
}
