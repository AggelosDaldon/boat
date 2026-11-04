package net.shirojr.boatism.util.handler;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_1657;
import net.minecraft.class_1690;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.api.BoatEngineComponent;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.mixin.BoatEntityInvoker;
import net.shirojr.boatism.network.BoatismNetworkIdentifiers;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.util.LoggerUtil;
import net.shirojr.boatism.util.sound.SoundInstanceIdentifier;

public class BoatEngineHandler {
   public static final int MAX_POWER_LEVEL = 9;
   public static final int LIMITED_MAX_POWER_LEVEL = 3;
   public static final int MAX_BASE_FUEL;
   public static final int MAX_BASE_OVERHEAT;
   private final BoatEngineEntity boatEngine;
   private boolean canPlayOverheat = true;
   private boolean canPlayLowFuel = true;
   private boolean canPlayLowHealth = true;

   private BoatEngineHandler(BoatEngineEntity boatEngine) {
      this.boatEngine = boatEngine;
   }

   public static BoatEngineHandler create(BoatEngineEntity boatEngine) {
      return new BoatEngineHandler(boatEngine);
   }

   public void incrementTick() {
      if (this.breaksWhenSubmerged() && this.isSubmerged()) {
         this.stopEngine();
      }

      if (!this.handleHealth()) {
         if (!this.handleOverheating()) {
            if (!this.handleFuel()) {
               LoggerUtil.devLogger(String.format("Fuel: %s/%s | Overheat: %s/%s", this.getFuel(), this.getMaxFuelCapacity(), this.getOverheat(), this.getMaxOverHeatCapacity()));
            }
         }
      }
   }

   private boolean handleHealth() {
      if (this.isLowHealth()) {
         this.regulateDownIfLimited();
         if (this.canPlayLowHealth) {
            this.boatEngine.broadcastToAllPlayerPassengers(class_2561.method_43471("warning.boatism.low_health"), true);
            if (this.engineIsRunning()) {
               this.changeSoundState(List.of(SoundInstanceIdentifier.ENGINE_LOW_HEALTH));
            }

            this.canPlayLowHealth = false;
         }
      } else if (!this.canPlayLowHealth) {
         this.canPlayLowHealth = true;
      }

      return false;
   }

   private boolean handleFuel() {
      if (this.isLowOnFuel()) {
         this.regulateDownIfLimited();
         if (this.canPlayLowFuel) {
            this.boatEngine.broadcastToAllPlayerPassengers(class_2561.method_43471("warning.boatism.low_on_fuel"), true);
            if (this.engineIsRunning()) {
               this.changeSoundState(List.of(SoundInstanceIdentifier.ENGINE_LOW_FUEL));
            }

            this.canPlayLowFuel = false;
         }
      } else if (!this.canPlayLowFuel) {
         this.canPlayLowFuel = true;
      }

      if (!this.boatEngine.method_37908().method_8608()) {
         if (this.getFuel() <= 0.0F) {
            this.stopEngine();
            return true;
         }

         if (this.engineIsRunning()) {
            this.consumeFuel(1.0F);
         }
      }

      return false;
   }

   private void regulateDownIfLimited() {
      if (this.getPowerLevel() > this.getMaxPowerLevel() && this.engineIsRunning()) {
         this.setPowerLevel(this.getMaxPowerLevel());
      }

   }

   private boolean handleOverheating() {
      if (!this.boatEngine.method_37908().method_8608()) {
         float currentOverHeat = 0.0F;
         if (this.isExperiencingHeavyLoad()) {
            currentOverHeat += 2.0F;
         } else if (this.getOverheat() > 0.0F) {
            --currentOverHeat;
         }

         this.setOverheat(this.getOverheat() + currentOverHeat);
      }

      if (this.isHeatingUp()) {
         if (this.canPlayOverheat) {
            this.boatEngine.broadcastToAllPlayerPassengers(class_2561.method_43471("warning.boatism.starting_to_overheat"), true);
            this.changeSoundState(List.of(SoundInstanceIdentifier.ENGINE_OVERHEATING));
            this.canPlayOverheat = false;
         }
      } else if (!this.canPlayOverheat) {
         this.canPlayOverheat = true;
      }

      if (this.isOverheated()) {
         this.stopEngine();
         this.boatEngine.onOverheated();
         return true;
      } else {
         return false;
      }
   }

   public void startEngine() {
      if (!this.engineCanStart()) {
         this.playSoundEvent(BoatismSounds.BOAT_ENGINE_START_FAIL);
      } else {
         this.playSoundEvent(BoatismSounds.BOAT_ENGINE_START);
         this.boatEngine.setIsRunning(true);
         this.initiateSoundState();
      }
   }

   public void stopEngine() {
      if (this.engineIsRunning()) {
         this.playSoundEvent(BoatismSounds.BOAT_ENGINE_STOP, 0.7F, 1.0F);
         this.setPowerLevel(0);
         this.boatEngine.setIsRunning(false);
      }
   }

   public boolean engineIsRunning() {
      return this.boatEngine.isRunning();
   }

   public boolean engineCanStart() {
      if (this.getFuel() < 5.0F) {
         return false;
      } else if (this.isSubmerged() && this.breaksWhenSubmerged()) {
         return false;
      } else if (this.isOverheated()) {
         return false;
      } else if (this.boatEngine.isLocked()) {
         return false;
      } else {
         return !this.boatEngine.isRunning();
      }
   }

   public float getFuel() {
      return this.boatEngine.getFuel();
   }

   public void setFuel(float fuel) {
      fuel = Math.min(this.getMaxFuelCapacity(), fuel);
      this.boatEngine.setFuel(fuel);
   }

   public float getMaxFuelCapacity() {
      float maxCapacity = (float)MAX_BASE_FUEL;

      for(class_1799 stack : this.getMountedItems()) {
         class_1792 var5 = stack.method_7909();
         if (var5 instanceof BoatEngineComponent component) {
            maxCapacity += component.addedFuelCapacity();
         }
      }

      return maxCapacity;
   }

   public float fillUpFuel(float fuel) {
      float newFuelValue = this.getFuel() + fuel;
      if (fuel <= 0.0F) {
         return 0.0F;
      } else if (newFuelValue == this.getMaxFuelCapacity() + fuel) {
         return fuel;
      } else {
         this.playSoundEvent(BoatismSounds.BOAT_ENGINE_FILL_UP);
         if (newFuelValue > this.getMaxFuelCapacity()) {
            this.setFuel(this.getMaxFuelCapacity());
            return newFuelValue - this.getMaxFuelCapacity();
         } else {
            this.setFuel(fuel);
            return fuel;
         }
      }
   }

   public void consumeFuel(float baseFuelConsumption) {
      float consumedFuel = baseFuelConsumption + this.additionalConsumedFuel();
      this.setFuel(Math.max(this.getFuel() - consumedFuel, 0.0F));
   }

   private float additionalConsumedFuel() {
      float fuelPerTick = 0.0F;

      for(class_1799 entry : this.getMountedItems()) {
         class_1792 var5 = entry.method_7909();
         if (var5 instanceof BoatEngineComponent component) {
            fuelPerTick += component.addedConsumedFuel();
         }
      }

      return fuelPerTick;
   }

   public boolean isLowOnFuel() {
      return (double)this.getFuel() < (double)this.getMaxFuelCapacity() * 0.2;
   }

   public int getPowerLevel() {
      return this.boatEngine.getPowerLevel();
   }

   public void setPowerLevel(int powerLevel) {
      powerLevel = Math.max(powerLevel, 0);
      powerLevel = Math.min(powerLevel, 9);
      this.boatEngine.setPowerLevel(powerLevel);
   }

   public int getMaxPowerLevel() {
      return !this.isLowOnFuel() && !this.isLowHealth() ? 9 : 3;
   }

   public boolean isHeatingUp() {
      return this.getOverheat() > 10.0F;
   }

   public float getOverheat() {
      return this.boatEngine.getOverheat();
   }

   public void setOverheat(float overheat) {
      this.boatEngine.setOverheat(overheat);
   }

   public boolean isOverheated() {
      return this.getOverheat() > this.getMaxOverHeatCapacity();
   }

   public float getMaxOverHeatCapacity() {
      float maxCapacity = (float)MAX_BASE_OVERHEAT;

      for(class_1799 stack : this.getMountedItems()) {
         class_1792 var5 = stack.method_7909();
         if (var5 instanceof BoatEngineComponent component) {
            maxCapacity += component.addedOverheatTolerance();
         }
      }

      return maxCapacity;
   }

   public boolean isExperiencingHeavyLoad() {
      if (!this.engineIsRunning()) {
         return false;
      } else if (this.getPowerLevel() > 3 && (double)this.getPowerLevel() * 0.1 < this.boatEngine.method_18798().method_37267()) {
         return true;
      } else {
         return this.getPowerLevel() >= 7;
      }
   }

   public boolean isSubmerged() {
      return this.boatEngine.isSubmerged();
   }

   public void setSubmerged(boolean isSubmerged) {
      if (isSubmerged != this.isSubmerged()) {
         this.boatEngine.setSubmerged(isSubmerged);
         this.changeSoundState(List.of(SoundInstanceIdentifier.ENGINE_RUNNING_UNDERWATER));
      }
   }

   public boolean breaksWhenSubmerged() {
      for(class_1799 entry : this.getMountedItems()) {
         class_1792 var4 = entry.method_7909();
         if (var4 instanceof BoatEngineComponent component) {
            if (component.waterProofsEngine()) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isLowHealth() {
      return (double)this.boatEngine.method_6032() < (double)this.boatEngine.method_6063() * 0.2;
   }

   public float getFullArmorValue() {
      float armor = 0.0F;

      for(class_1799 entry : this.boatEngine.getMountedInventory().method_54454()) {
         class_1792 var5 = entry.method_7909();
         if (var5 instanceof BoatEngineComponent component) {
            armor += component.getAdditionalArmor();
         }
      }

      return armor;
   }

   public float calculateThrustModifier(class_1690 hookedBoatEntity) {
      if (hookedBoatEntity == null) {
         return 0.0F;
      } else {
         List<class_1799> boatComponentStacks = new ArrayList();
         int passengerCount = Math.max(hookedBoatEntity.method_5685().size() - 1, 0);
         int maxPassenger = ((BoatEntityInvoker)hookedBoatEntity).invokeGetMaxPassenger();
         int thrust = 1;
         this.getMountedItems().forEach((stack) -> {
            class_1792 patt10647$temp = stack.method_7909();
            if (patt10647$temp instanceof BoatEngineComponent component) {
               if (component.addedThrust() > 0.0F) {
                  boatComponentStacks.add(stack);
               }
            }

         });

         for(class_1799 thrustModifierStack : boatComponentStacks) {
            thrust = (int)((float)thrust + ((BoatEngineComponent)thrustModifierStack.method_7909()).addedThrust());
         }

         float passengerDeficit = (float)passengerCount / (float)maxPassenger;
         return (float)thrust * class_3532.method_16439(passengerDeficit, 1.0F, 0.7F);
      }
   }

   public boolean canEquipPart(class_1799 stack) {
      if (this.engineIsRunning()) {
         return false;
      } else if (!(stack.method_7909() instanceof BoatEngineComponent)) {
         return false;
      } else {
         List<class_1792> flaggedParts = new ArrayList();

         for(class_1799 entry : this.getMountedItems()) {
            class_1792 var6 = entry.method_7909();
            if (var6 instanceof BoatEngineComponent) {
               BoatEngineComponent component = (BoatEngineComponent)var6;
               flaggedParts.addAll(component.getConflictingParts());
            }
         }

         return !flaggedParts.contains(stack.method_7909());
      }
   }

   public List<class_1799> getMountedItems() {
      return this.boatEngine.getMountedInventory().method_54454();
   }

   public void initiateSoundState() {
      List<SoundInstanceIdentifier> identifierList = new ArrayList();
      if (this.engineIsRunning()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_RUNNING);
      }

      if (this.isLowHealth()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_LOW_HEALTH);
      }

      if (this.isLowOnFuel()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_LOW_FUEL);
      }

      if (this.isSubmerged()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_RUNNING_UNDERWATER);
      }

      if (this.isHeatingUp()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_OVERHEATING);
      }

      this.changeSoundState(identifierList);
   }

   public void changeSoundState(List<SoundInstanceIdentifier> changedSoundList) {
      class_1937 var3 = this.boatEngine.method_37908();
      if (var3 instanceof class_3218 serverWorld) {
         PlayerLookup.around(serverWorld, this.boatEngine.method_19538(), (double)60.0F).forEach((player) -> {
            for(SoundInstanceIdentifier entry : changedSoundList) {
               class_2540 buf = PacketByteBufs.create();
               buf.method_10804(this.boatEngine.method_5628());
               buf.method_10812(entry.getIdentifier());
               ServerPlayNetworking.send(player, BoatismNetworkIdentifiers.SOUND_START.getIdentifier(), buf);
            }

         });
      }
   }

   private void playSoundEvent(class_3414 soundEvent) {
      this.playSoundEvent(soundEvent, 1.0F, 1.0F);
   }

   private void playSoundEvent(class_3414 soundEvent, float volume, float pitch) {
      if (!this.boatEngine.method_37908().method_8608()) {
         this.boatEngine.method_37908().method_8396((class_1657)null, this.boatEngine.method_24515(), soundEvent, class_3419.field_15254, volume, pitch);
      }
   }

   static {
      MAX_BASE_FUEL = Boatism.CONFIG.maxBaseFuel;
      MAX_BASE_OVERHEAT = Boatism.CONFIG.maxBaseOverheat;
   }
}
