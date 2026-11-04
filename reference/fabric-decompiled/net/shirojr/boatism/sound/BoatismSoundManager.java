package net.shirojr.boatism.sound;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.shirojr.boatism.BoatismClient;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.sound.instance.SoundInstanceState;
import net.shirojr.boatism.sound.instance.custom.BoatismSoundInstance;
import net.shirojr.boatism.util.LoggerUtil;
import net.shirojr.boatism.util.sound.SoundInstanceIdentifier;

@Environment(EnvType.CLIENT)
public class BoatismSoundManager {
   private final class_310 client = class_310.method_1551();
   private final List<SoundInstanceEntry> activeSoundInstances = new ArrayList();

   public List<SoundInstanceEntry> getActiveSoundInstances() {
      return this.activeSoundInstances;
   }

   public void start(SoundInstanceIdentifier soundInstanceIdentifier, BoatismSoundInstance soundInstance) {
      if (soundInstance instanceof SoundInstanceState newInstanceState) {
         ArrayList unsupportedSoundInstances = new ArrayList();

         for(SoundInstanceEntry activeInstance : this.activeSoundInstances) {
            LoggerUtil.devLogger(activeInstance.instance.toString());
            BoatismSoundInstance var8 = activeInstance.instance;
            if (var8 instanceof SoundInstanceState activeInstanceState) {
               if (soundInstance.getBoatEngineEntity().equals(activeInstance.instance.getBoatEngineEntity())) {
                  if (newInstanceState.isMainSound() && activeInstanceState.isMainSound()) {
                     unsupportedSoundInstances.add(activeInstance);
                  }

                  if (soundInstance.equals(activeInstance.instance)) {
                     unsupportedSoundInstances.add(activeInstance);
                  }

                  for(SoundInstanceIdentifier unsupportedInstance : newInstanceState.unsupportedInstances()) {
                     if (activeInstance.identifier.equals(unsupportedInstance)) {
                        unsupportedSoundInstances.add(activeInstance);
                     }
                  }
               }

               if (activeInstance.instance.isFinished()) {
                  unsupportedSoundInstances.add(activeInstance);
               }
            }
         }

         unsupportedSoundInstances.forEach(this::stop);
         this.activeSoundInstances.add(new SoundInstanceEntry(soundInstanceIdentifier, soundInstance));
         this.client.method_1483().method_4873(soundInstance);

         for(SoundInstanceEntry entry : BoatismClient.soundManager.getActiveSoundInstances()) {
            if (entry.identifier.equals(SoundInstanceIdentifier.ENGINE_OVERHEATING)) {
               BoatEngineEntity var10000 = entry.instance().getBoatEngineEntity();
               LoggerUtil.devLogger(var10000 + " | " + entry.identifier().toString());
            }
         }

      }
   }

   public void stop(SoundInstanceEntry soundInstanceEntry) {
      for(SoundInstanceEntry entry : this.activeSoundInstances) {
         if (soundInstanceEntry.identifier == entry.identifier) {
            soundInstanceEntry.instance.finishSoundInstance();
         }
      }

      this.removeEntriesFromList(List.of(soundInstanceEntry));
   }

   public void stopAllSoundInstancesForBoatEngineEntity(BoatEngineEntity boatEngine, boolean force) {
      for(SoundInstanceEntry entry : this.activeSoundInstances) {
         if (entry.instance.getBoatEngineEntity().equals(boatEngine)) {
            BoatismSoundInstance var6 = entry.instance;
            if (var6 instanceof SoundInstanceState) {
               SoundInstanceState activeInstanceState = (SoundInstanceState)var6;
               if (force || !activeInstanceState.canRunIfEngineIsTurnedOff(boatEngine)) {
                  this.client.method_1483().method_4870(entry.instance);
               }
            }
         }
      }

      this.activeSoundInstances.clear();
   }

   public void stopAllSoundInstances(boolean sendInformationText) {
      for(SoundInstanceEntry entry : this.activeSoundInstances) {
         this.client.method_1483().method_4870(entry.instance);
         if (this.client.field_1724 != null && sendInformationText) {
            this.client.field_1724.method_43496(class_2561.method_43470("removed %s for: %s".formatted(entry.identifier, entry.instance.getBoatEngineEntity().toString())));
         }
      }

      this.activeSoundInstances.clear();
   }

   private void removeEntriesFromList(List<SoundInstanceEntry> unsupportedSoundInstances) {
      for(SoundInstanceEntry entry : unsupportedSoundInstances) {
         this.activeSoundInstances.remove(entry);
      }

   }

   public static record SoundInstanceEntry(SoundInstanceIdentifier identifier, BoatismSoundInstance instance) {
   }
}
