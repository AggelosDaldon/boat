package net.shirojr.boatism.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1690;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2487;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;
import net.minecraft.class_3419;
import net.minecraft.class_4048;
import net.minecraft.class_8836;
import net.minecraft.class_1690.class_1692;
import net.shirojr.boatism.api.BoatEngineCoupler;
import net.shirojr.boatism.api.CustomBoatEngineAttachment;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.item.custom.BaseEngineItem;
import net.shirojr.boatism.sound.BoatismSounds;
import net.shirojr.boatism.util.handler.EntityHandler;
import net.shirojr.boatism.util.nbt.BoatEngineNbtHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1690.class})
public abstract class BoatEntityMixin extends class_8836 implements BoatEngineCoupler, CustomBoatEngineAttachment {
   @Unique
   private static final class_2940<Optional<UUID>> BOAT_ENGINE_UUID;

   public BoatEntityMixin(class_1299<?> entityType, class_1937 world) {
      super(entityType, world);
   }

   @Inject(
      method = {"initDataTracker"},
      at = {@At("TAIL")}
   )
   private void boatism$injectDataTracker(CallbackInfo ci) {
      this.field_6011.method_12784(BOAT_ENGINE_UUID, Optional.empty());
   }

   public void boatism$setBoatEngineEntity(@Nullable UUID boatEngineUuid) {
      this.field_6011.method_12778(BOAT_ENGINE_UUID, Optional.ofNullable(boatEngineUuid));
   }

   public Optional<UUID> boatism$getBoatEngineEntityUuid() {
      return (Optional)this.field_6011.method_12789(BOAT_ENGINE_UUID);
   }

   @Inject(
      method = {"updatePaddles"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/vehicle/BoatEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;"
)}
   )
   private void boatism$controlBoatSpeed(CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef f) {
      class_1690 boatEntity = (class_1690)this;
      ((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid().flatMap((engineUuid) -> EntityHandler.getBoatEngineEntityFromUuid(engineUuid, boatEntity.method_37908(), boatEntity.method_19538(), 10)).ifPresent((boatEngine) -> {
         float baseSpeed = f.get();
         float powerLevel = (float)boatEngine.getPowerLevel() * 0.008F;
         float thrust = baseSpeed + powerLevel * boatEngine.getEngineHandler().calculateThrustModifier(boatEntity);
         f.set(thrust);
      });
   }

   @Inject(
      method = {"interact"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;startRiding(Lnet/minecraft/entity/Entity;)Z",
   shift = Shift.BEFORE
)},
      cancellable = true
   )
   private void boatism$equipEngineEntity(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      class_1799 stack = player.method_6047();
      class_1690 boatEntity = (class_1690)this;
      EntityHandler.engineLinkCleanUp(boatEntity);
      if (!((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid().isPresent()) {
         if (stack.method_7909() instanceof BaseEngineItem) {
            if (!this.method_37908().method_8608()) {
               BoatEngineEntity engineEntity = BoatEngineNbtHelper.getBoatEngineEntityFromItemStack(stack, boatEntity);
               this.method_37908().method_8649(engineEntity);
               this.method_37908().method_43128((class_1657)null, boatEntity.method_23317(), boatEntity.method_23318(), boatEntity.method_23321(), BoatismSounds.BOAT_ENGINE_EQUIP, class_3419.field_15254, 0.9F, 1.0F);
               stack.method_7934(1);
            }

            cir.setReturnValue(class_1269.field_5812);
         }

      }
   }

   @Inject(
      method = {"canCollide"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void boatism$boatEngineCollision(class_1297 entity, class_1297 other, CallbackInfoReturnable<Boolean> cir) {
      if (other instanceof BoatEngineEntity) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"readCustomDataFromNbt"},
      at = {@At("TAIL")}
   )
   private void boatism$readBoatEngineEntry(class_2487 nbt, CallbackInfo ci) {
      class_1690 boatEntity = (class_1690)this;
      if (nbt.method_10545("BoatEngineUuid")) {
         ((BoatEngineCoupler)boatEntity).boatism$setBoatEngineEntity(nbt.method_25926("BoatEngineUuid"));
      }
   }

   @Inject(
      method = {"writeCustomDataToNbt"},
      at = {@At("TAIL")}
   )
   private void boatism$writeBoatEngineEntry(class_2487 nbt, CallbackInfo ci) {
      class_1690 boatEntity = (class_1690)this;
      ((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid().ifPresent((uuid) -> nbt.method_25927("BoatEngineUuid", uuid));
   }

   @Inject(
      method = {"getPassengerAttachmentPos"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void boatism$getPassengerAttachmentPos(class_1297 passenger, class_4048 dimensions, float scaleFactor, CallbackInfoReturnable<Vector3f> info) {
      if (passenger instanceof BoatEngineEntity) {
         info.setReturnValue(this.boatism$attachmentPos(dimensions));
      }

   }

   @Inject(
      method = {"canAddPassenger"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void boatism$canAddPassenger(class_1297 passenger, CallbackInfoReturnable<Boolean> info) {
      class_1690 boatEntity = (class_1690)this;
      if (!((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid().isEmpty()) {
         int maxPassengers = ((BoatEntityInvoker)boatEntity).invokeGetMaxPassenger() + 1;
         if (boatEntity.method_5685().size() < maxPassengers) {
            info.setReturnValue(true);
         }

      }
   }

   public Vector3f boatism$attachmentPos(class_4048 dimensions) {
      return this.method_47885() == class_1692.field_40161 ? new Vector3f(0.0F, dimensions.field_18068 * 0.7F, -1.2F) : new Vector3f(0.0F, dimensions.field_18068 / 3.0F, -1.32F);
   }

   @Shadow
   public abstract class_1690.class_1692 method_47885();

   static {
      BOAT_ENGINE_UUID = class_2945.method_12791(BoatEntityMixin.class, class_2943.field_13313);
   }
}
