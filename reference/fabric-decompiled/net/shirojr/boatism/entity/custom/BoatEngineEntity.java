package net.shirojr.boatism.entity.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1277;
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1538;
import net.minecraft.class_1657;
import net.minecraft.class_1690;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_2379;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_5132;
import net.minecraft.class_5134;
import net.minecraft.class_7094;
import net.minecraft.class_1322.class_1323;
import net.minecraft.class_1937.class_7867;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.api.BoatEngineComponent;
import net.shirojr.boatism.api.BoatEngineCoupler;
import net.shirojr.boatism.entity.BoatismEntities;
import net.shirojr.boatism.entity.animation.BoatismAnimation;
import net.shirojr.boatism.gamerule.BoatismGameRules;
import net.shirojr.boatism.network.BoatismNetworkIdentifiers;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.util.BoatEngineExplosionBehaviour;
import net.shirojr.boatism.util.LoggerUtil;
import net.shirojr.boatism.util.handler.BoatEngineHandler;
import net.shirojr.boatism.util.handler.EntityHandler;
import net.shirojr.boatism.util.nbt.BoatEngineNbtHelper;
import net.shirojr.boatism.util.sound.SoundInstanceIdentifier;
import net.shirojr.boatism.util.tag.BoatismTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoatEngineEntity extends class_1309 {
   private final class_1277 mountedInventory;
   private static final class_2940<Integer> POWER_LEVEL;
   private static final class_2940<Float> OVERHEAT;
   private static final class_2940<class_2379> ARM_ROTATION;
   private static final class_2940<Boolean> SUBMERGED;
   private static final class_2940<Boolean> RUNNING;
   private static final class_2940<Float> FUEL;
   private static final class_2940<Boolean> LOCKED;
   public static final UUID COMPONENT_ARMOR_ID;
   public final class_7094 leftSpinAnimationState;
   public float spinAnimationTimeout;
   private @Nullable UUID hookedBoatEntityUuid;
   private final @NotNull BoatEngineHandler engineHandler;
   private class_243 previousLocation;

   public BoatEngineEntity(class_1299<? extends class_1309> entityType, class_1937 world) {
      super(entityType, world);
      this.leftSpinAnimationState = new class_7094();
      this.spinAnimationTimeout = 0.0F;
      this.previousLocation = class_243.field_1353;
      this.engineHandler = BoatEngineHandler.create(this);
      this.mountedInventory = new class_1277(32);
      this.method_5875(true);
      this.method_49477(0.0F);
   }

   public BoatEngineEntity(class_1937 world, double x, double y, double z) {
      this(BoatismEntities.BOAT_ENGINE, world);
      this.method_5814(x, y, z);
   }

   public BoatEngineEntity(class_1937 world, class_1690 hookedBoatEntity) {
      this(BoatismEntities.BOAT_ENGINE, world);
      this.method_23327(hookedBoatEntity.method_23317(), hookedBoatEntity.method_23318(), hookedBoatEntity.method_23321());
      this.hookOntoBoatEntity(hookedBoatEntity);
      this.previousLocation = hookedBoatEntity.method_19538();
   }

   protected void method_5693() {
      super.method_5693();
      this.field_6011.method_12784(POWER_LEVEL, 0);
      this.field_6011.method_12784(OVERHEAT, 0.0F);
      this.field_6011.method_12784(ARM_ROTATION, new class_2379(0.0F, 5.0F, 0.0F));
      this.field_6011.method_12784(SUBMERGED, false);
      this.field_6011.method_12784(FUEL, 0.0F);
      this.field_6011.method_12784(LOCKED, false);
      this.field_6011.method_12784(RUNNING, false);
   }

   public static class_5132.class_5133 setAttributes() {
      return class_1309.method_26827().method_26868(class_5134.field_23716, (double)Boatism.CONFIG.health).method_26868(class_5134.field_23718, (double)1.0F).method_26868(class_5134.field_23724, (double)0.0F).method_26868(class_5134.field_23725, (double)15.0F);
   }

   private void updateAnimationStates() {
      if (this.getPowerLevel() > 0) {
         if (this.spinAnimationTimeout <= 0.0F) {
            this.spinAnimationTimeout = BoatismAnimation.SPIN_DURATION_IN_SEC * 20.0F;
            this.leftSpinAnimationState.method_41322(this.field_6012);
         }

         --this.spinAnimationTimeout;
      } else {
         this.leftSpinAnimationState.method_41325();
      }

   }

   public void method_5652(class_2487 nbt) {
      super.method_5652(nbt);
      this.getHookedBoatEntityUuid().ifPresent((hookedBoatEntityUuid) -> nbt.method_25927("HookedEntity", hookedBoatEntityUuid));
      BoatEngineNbtHelper.writeItemStacksToNbt(this.mountedInventory.method_54454(), "MountedItems", nbt);
      nbt.method_10556("IsRunning", this.isRunning());
      nbt.method_10569("PowerOutput", this.getPowerLevel());
      nbt.method_10548("Overheat", this.getOverheat());
      nbt.method_10566("EngineRotation", this.getArmRotation().method_10255());
      nbt.method_10556("IsSubmerged", this.isSubmerged());
      nbt.method_10548("Fuel", this.getFuel());
      nbt.method_10556("IsLocked", this.isLocked());
   }

   public void method_5749(class_2487 nbt) {
      super.method_5749(nbt);
      if (nbt.method_10545("HookedEntity")) {
         this.setHookedBoatEntity(nbt.method_25926("HookedEntity"));
      }

      if (nbt.method_10545("MountedItems")) {
         this.mountedInventory.method_5448();
         this.setMountedItemsFromItemStackList(BoatEngineNbtHelper.readItemStacksFromNbt(nbt, "MountedItems"));
         this.syncComponentListToTrackingClients();
      }

      this.setIsRunning(nbt.method_10577("IsRunning"));
      this.setPowerLevel(Math.min(nbt.method_10550("PowerOutput"), 4));
      this.setOverheat(nbt.method_10583("Overheat"));
      this.setArmRotation(new class_2379(nbt.method_10554("EngineRotation", 5)));
      this.setSubmerged(nbt.method_10577("IsSubmerged"));
      this.setFuel(nbt.method_10583("Fuel"));
      this.setLocked(nbt.method_10577("IsLocked"));
   }

   private void syncComponentListToTrackingClients() {
      if (this.method_37908() instanceof class_3218) {
         PlayerLookup.tracking(this).forEach((player) -> {
            class_2540 buf = PacketByteBufs.create();
            buf.method_10804(this.method_5628());
            buf.method_10804(this.getMountedInventory().method_5439());

            for(int i = 0; i < this.getMountedInventory().method_5439(); ++i) {
               buf.method_10804(i);
               buf.method_10793(this.getMountedInventory().method_5438(i));
            }

            ServerPlayNetworking.send(player, BoatismNetworkIdentifiers.BOAT_COMPONENT_SYNC.getIdentifier(), buf);
         });
      }
   }

   public void method_5773() {
      super.method_5773();
      this.method_5875(true);
      if (this.method_37908().method_8608()) {
         this.updateAnimationStates();
      } else {
         if (this.isRunning()) {
            this.getHookedBoatEntity().ifPresent((boatEntity) -> {
               double actualSpeed = Math.max((double)0.0F, this.previousLocation.method_1022(this.method_19538()));
               this.modifyVelocity(boatEntity);
               if (this.getPowerLevel() > 3 && actualSpeed < 0.1) {
                  this.setOverheat(this.getOverheat() + 2.0F);
               }

               if (this.method_24828() && !this.method_5799() && this.engineHandler.engineIsRunning()) {
                  this.setOverheat(this.getOverheat() + 2.0F);
               }

            });
            this.previousLocation = this.method_19538();
            if (this.isSubmerged()) {
               this.method_5855(this.method_5748());
            }
         }

         this.engineHandler.setSubmerged(this.field_6000);
         this.engineHandler.incrementTick();
      }
   }

   private void modifyVelocity(class_1690 boatEntity) {
      if (!(boatEntity.method_5642() instanceof class_1657)) {
         if (this.method_5787()) {
            if (boatEntity.method_24828()) {
               this.setOverheat(this.getOverheat() + 4.0F);
            } else {
               class_243 newVelocity = boatEntity.method_5720().method_18805((double)1.0F, (double)0.0F, (double)1.0F).method_1029().method_1021((double)this.getPowerLevel() * 0.1).method_1021((double)this.engineHandler.calculateThrustModifier(boatEntity));
               if (boatEntity.method_18798().method_37267() < newVelocity.method_37267()) {
                  boatEntity.method_45319(newVelocity);
               }
            }

            boatEntity.field_6037 = true;
         }
      }
   }

   public class_1269 method_5688(class_1657 player, class_1268 hand) {
      class_1799 stack = player.method_5998(hand);
      if (player.method_5715() && stack.method_7909() instanceof BoatEngineComponent) {
         if (this.engineHandler.engineIsRunning()) {
            player.method_7353(class_2561.method_43471("warning.boatism.engine_is_running"), true);
            return class_1269.field_5811;
         }

         if (this.engineHandler.canEquipPart(stack) && !this.mountedInventoryContains(stack)) {
            this.addToMountedInventory(stack);
            if (this.method_37908().method_8608()) {
               return class_1269.field_5812;
            }

            LoggerUtil.devLogger(String.format("Equipped component %s on the engine", stack.method_7964()));
            if (!player.method_7337()) {
               stack.method_7934(1);
            }

            player.method_37908().method_43128((class_1657)null, this.method_23317(), this.method_23318(), this.method_23321(), BoatismSounds.BOAT_ENGINE_EQUIP, class_3419.field_15254, 0.7F, 1.0F);
            return class_1269.field_5812;
         }

         player.method_7353(class_2561.method_43471("warning.boatism.component_is_blocked"), true);
      } else {
         if (player.method_6047().method_7960() && player.method_5715() && this.getMountedInventorySize() > 0) {
            EntityHandler.dropMountedInventory(this, false, true);
            this.syncComponentListToTrackingClients();
            if (this.method_37908().method_8608()) {
               return class_1269.field_5812;
            }

            player.method_37908().method_43128((class_1657)null, this.method_23317(), this.method_23318(), this.method_23321(), BoatismSounds.BOAT_ENGINE_EQUIP, class_3419.field_15254, 0.7F, 1.0F);
            return class_1269.field_5812;
         }

         if (player.method_6047().method_7960()) {
            if (!this.engineHandler.engineIsRunning()) {
               this.engineHandler.startEngine();
            } else {
               this.engineHandler.stopEngine();
            }

            LoggerUtil.devLogger(String.format("Engine is running: %s", this.engineHandler.engineIsRunning()));
            return class_1269.field_5812;
         }
      }

      return super.method_5688(player, hand);
   }

   public void method_5837(class_3222 player) {
      List<SoundInstanceIdentifier> identifierList = new ArrayList();
      this.syncComponentListToTrackingClients();
      this.sendPacketForStoppingAllSoundInstances(player);
      if (this.engineHandler.engineIsRunning()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_RUNNING);
      }

      if (this.engineHandler.isLowHealth()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_LOW_HEALTH);
      }

      if (this.engineHandler.isLowOnFuel()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_LOW_FUEL);
      }

      if (this.engineHandler.isSubmerged()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_RUNNING_UNDERWATER);
      }

      if (this.engineHandler.isHeatingUp()) {
         identifierList.add(SoundInstanceIdentifier.ENGINE_OVERHEATING);
      }

      identifierList.forEach((identifier) -> this.sendPacketForSoundInstance(identifier, player));
      super.method_5837(player);
   }

   public void method_5742(class_3222 player) {
      this.sendPacketForStoppingAllSoundInstances(player);
      super.method_5742(player);
   }

   private void sendPacketForSoundInstance(SoundInstanceIdentifier soundIdentifier, class_3222 player) {
      class_2540 buf = PacketByteBufs.create();
      buf.method_10804(this.method_5628());
      buf.method_10812(soundIdentifier.getIdentifier());
      ServerPlayNetworking.send(player, BoatismNetworkIdentifiers.SOUND_START.getIdentifier(), buf);
      super.method_5742(player);
   }

   private void sendPacketForStoppingAllSoundInstances(class_3222 player) {
      class_2540 buf = PacketByteBufs.create();
      buf.method_10804(this.method_5628());
      buf.method_52964(true);
      ServerPlayNetworking.send(player, BoatismNetworkIdentifiers.SOUND_END_ENGINE.getIdentifier(), buf);
   }

   public Optional<class_1690> getHookedBoatEntity() {
      class_1937 var2 = this.method_37908();
      if (var2 instanceof class_3218 serverWorld) {
         class_1297 entity = serverWorld.method_14190(this.hookedBoatEntityUuid);
         if (entity instanceof class_1690 boatEntity) {
            return Optional.of(boatEntity);
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public Optional<UUID> getHookedBoatEntityUuid() {
      return Optional.ofNullable(this.hookedBoatEntityUuid);
   }

   public void setHookedBoatEntity(UUID uuid) {
      this.hookedBoatEntityUuid = uuid;
   }

   public class_1277 getMountedInventory() {
      return this.mountedInventory;
   }

   public int getMountedInventorySize() {
      int i = 0;

      for(class_1799 entry : this.getMountedInventory().method_54454()) {
         if (entry.method_7909() instanceof BoatEngineComponent) {
            ++i;
         }
      }

      return i;
   }

   public void setMountedItemsFromItemStackList(List<class_1799> mountedItems) {
      if (mountedItems.size() > this.getMountedInventory().method_5439()) {
         LoggerUtil.devLogger("inventory size was bigger than expected", true, (Exception)null);
      } else {
         for(int i = 0; i < mountedItems.size(); ++i) {
            this.getMountedInventory().method_5447(i, (class_1799)mountedItems.get(i));
         }

      }
   }

   public void setMountedItemsFromComponentList(List<EngineComponent> components) {
      this.mountedInventory.method_5448();

      for(EngineComponent component : components) {
         this.mountedInventory.method_5491(component.componentStack);
      }

   }

   public void addToMountedInventory(class_1799 itemStack) {
      class_1792 var3 = itemStack.method_7909();
      if (var3 instanceof BoatEngineComponent component) {
         class_1324 armorAttributeInstance = this.method_5996(class_5134.field_23724);
         if (armorAttributeInstance != null && itemStack != null) {
            this.getMountedInventory().method_5491(component.getMountedItemStack(itemStack));
            if (!this.method_37908().method_8608()) {
               this.changeArmorModifier(armorAttributeInstance, COMPONENT_ARMOR_ID);
            }
         }
      }
   }

   public boolean mountedInventoryContains(class_1799 itemStack) {
      class_1792 var3 = itemStack.method_7909();
      if (var3 instanceof BoatEngineComponent component) {
         return this.getMountedInventory().method_43256((mountedStack) -> mountedStack.method_7909().equals(component.getMountedItemStack(itemStack).method_7909()));
      } else {
         return false;
      }
   }

   public void changeArmorModifier(class_1324 instance, UUID uuid) {
      instance.method_6200(uuid);
      class_1322 COMPONENT_ARMOR_BONUS = new class_1322(uuid, "Component armor bonus", (double)this.engineHandler.getFullArmorValue(), class_1323.field_6328);
      instance.method_26837(COMPONENT_ARMOR_BONUS);
   }

   public class_1799 method_6118(class_1304 slot) {
      return class_1799.field_8037;
   }

   public class_1306 method_6068() {
      return class_1306.field_6183;
   }

   protected @Nullable class_3414 method_6011(class_1282 source) {
      return BoatismSounds.BOAT_ENGINE_HIT;
   }

   protected @Nullable class_3414 method_6002() {
      return BoatismSounds.BOAT_ENGINE_HIT;
   }

   public void method_5673(class_1304 slot, class_1799 stack) {
      LoggerUtil.devLogger("Tried to equip an item on a BoatEngineEntity!", true, (Exception)null);
   }

   public void method_6116(class_1304 slot, class_1799 oldStack, class_1799 newStack) {
      class_1937 var5 = this.method_37908();
      if (var5 instanceof class_3218 serverWorld) {
         serverWorld.method_43128((class_1657)null, this.method_23317(), this.method_23318(), this.method_23321(), BoatismSounds.BOAT_ENGINE_EQUIP, class_3419.field_15254, 0.75F, 1.0F);
      }
   }

   public void hookOntoBoatEntity(class_1690 boatEntity) {
      if (boatEntity.method_5864().method_20210(BoatismTags.Entities.NOT_HOOKABLE)) {
         LoggerUtil.devLogger("Entity was excluded from being able to hook an engine");
      } else {
         ((BoatEngineCoupler)boatEntity).boatism$setBoatEngineEntity(this.method_5667());
         this.hookedBoatEntityUuid = boatEntity.method_5667();
         this.method_5873(boatEntity, true);
      }
   }

   public void broadcastToAllPlayerPassengers(class_2561 text, boolean overlay) {
      this.getHookedBoatEntity().ifPresent((boatEntity) -> boatEntity.method_5685().forEach((entity) -> {
            if (entity instanceof class_1657 player) {
               player.method_7353(text, overlay);
            }

         }));
   }

   public boolean method_18397(class_1799 stack) {
      return stack.method_7909() instanceof BoatEngineComponent;
   }

   protected boolean method_5860(class_1297 entity) {
      return false;
   }

   public boolean method_49108() {
      return super.method_49108();
   }

   public boolean method_30949(class_1297 other) {
      return other instanceof class_1690 ? false : super.method_30949(other);
   }

   public boolean method_6086() {
      return false;
   }

   public boolean method_6102() {
      return false;
   }

   public boolean method_6101() {
      return false;
   }

   public Iterable<class_1799> method_5661() {
      return Collections.singleton(class_1799.field_8037);
   }

   public boolean method_5810() {
      return false;
   }

   protected void method_6087(class_1297 entity) {
   }

   public void method_5800(class_3218 world, class_1538 lightning) {
   }

   public @NotNull BoatEngineHandler getEngineHandler() {
      return this.engineHandler;
   }

   public int getPowerLevel() {
      return (Integer)this.field_6011.method_12789(POWER_LEVEL);
   }

   public void setPowerLevel(int level) {
      this.field_6011.method_12778(POWER_LEVEL, level);
   }

   public float getOverheat() {
      return (Float)this.field_6011.method_12789(OVERHEAT);
   }

   public void setOverheat(float overheat) {
      this.field_6011.method_12778(OVERHEAT, overheat);
   }

   public class_2379 getArmRotation() {
      return (class_2379)this.field_6011.method_12789(ARM_ROTATION);
   }

   public void setArmRotation(class_2379 armRotation) {
      this.field_6011.method_12778(ARM_ROTATION, armRotation);
   }

   public boolean isSubmerged() {
      return (Boolean)this.field_6011.method_12789(SUBMERGED);
   }

   public void setSubmerged(boolean isSubmerged) {
      this.field_6011.method_12778(SUBMERGED, isSubmerged);
   }

   public boolean isRunning() {
      return (Boolean)this.field_6011.method_12789(RUNNING);
   }

   public void setIsRunning(boolean shouldRun) {
      this.field_6011.method_12778(RUNNING, shouldRun);
   }

   public boolean isLocked() {
      return (Boolean)this.field_6011.method_12789(LOCKED);
   }

   public void setLocked(boolean resting) {
      this.field_6011.method_12778(LOCKED, resting);
   }

   public float getFuel() {
      return (Float)this.field_6011.method_12789(FUEL);
   }

   public void setFuel(float fuel) {
      this.field_6011.method_12778(FUEL, fuel);
   }

   public boolean hasLowHealth() {
      return this.method_6032() <= Boatism.CONFIG.lowHealth;
   }

   public void onOverheated() {
      class_1937 var2 = this.method_37908();
      if (var2 instanceof class_3218 serverWorld) {
         this.getHookedBoatEntity().ifPresent((boatEntity) -> {
            for(class_1297 entry : boatEntity.method_5685()) {
               if (entry instanceof class_1657 player) {
                  if (!player.method_7337()) {
                     player.method_6092(new class_1293(class_1294.field_5916, 240, 1, false, false, true));
                     player.method_6092(new class_1293(class_1294.field_5920, 80, 1, false, false, true));
                  }
               }
            }

         });
         boolean var7 = serverWorld.method_8450().method_8355(BoatismGameRules.DESTRUCTIVE_ENGINE_EXPLOSION);

         for(class_1799 stack : this.getMountedInventory().method_54454()) {
            class_1792 var6 = stack.method_7909();
            if (var6 instanceof BoatEngineComponent component) {
               if (component.getAdditionalArmor() > 0.0F) {
                  var7 = false;
               }
            }
         }

         class_1937.class_7867 explosionSourceType = var7 ? class_7867.field_40891 : class_7867.field_40888;
         serverWorld.method_8454(this, class_1927.method_55108(serverWorld, this), new BoatEngineExplosionBehaviour(), this.method_23317(), this.method_23318(), this.method_23321(), 3.0F, var7, explosionSourceType, class_2398.field_11236, class_2398.field_11221, class_3417.field_15152);
         this.method_5768();
      }
   }

   public void removeBoatEngine(class_1297 entity) {
      if (entity instanceof class_1690 boatEntity) {
         ((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid().ifPresent((boatEngineEntity) -> ((BoatEngineCoupler)boatEntity).boatism$setBoatEngineEntity((UUID)null));
         this.method_31472();
      }
   }

   public void method_36209() {
      this.getHookedBoatEntity().ifPresent((boatEntity) -> ((BoatEngineCoupler)boatEntity).boatism$setBoatEngineEntity((UUID)null));
      super.method_36209();
   }

   public void method_6078(class_1282 damageSource) {
      this.getHookedBoatEntity().ifPresent((boatEntity) -> ((BoatEngineCoupler)boatEntity).boatism$setBoatEngineEntity((UUID)null));
      super.method_6078(damageSource);
   }

   static {
      POWER_LEVEL = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13327);
      OVERHEAT = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13320);
      ARM_ROTATION = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13316);
      SUBMERGED = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13323);
      RUNNING = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13323);
      FUEL = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13320);
      LOCKED = class_2945.method_12791(BoatEngineEntity.class, class_2943.field_13323);
      COMPONENT_ARMOR_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   }

   public static record EngineComponent(int slot, class_1799 componentStack) {
   }
}
