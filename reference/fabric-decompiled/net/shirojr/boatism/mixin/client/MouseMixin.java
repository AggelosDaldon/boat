package net.shirojr.boatism.mixin.client;

import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.class_1297;
import net.minecraft.class_1690;
import net.minecraft.class_2540;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_315;
import net.minecraft.class_746;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.api.BoatEngineCoupler;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;
import net.shirojr.boatism.network.BoatismNetworkIdentifiers;
import net.shirojr.boatism.util.handler.EntityHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public class MouseMixin {
   @Shadow
   @Final
   private class_310 field_1779;

   @Inject(
      method = {"onMouseScroll"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"
)},
      cancellable = true
   )
   private void boatism$onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
      if (Boatism.CONFIG.powerLevelScrolling) {
         class_315 options = this.field_1779.field_1690;
         boolean mouseScrolled = (Boolean)options.method_42439().method_41753();
         double delta = (mouseScrolled ? Math.signum(horizontal) : vertical) * (Double)options.method_41806().method_41753();
         class_746 player = this.field_1779.field_1724;
         if (player != null && player.method_5854() != null) {
            class_1297 var14 = player.method_5854();
            if (var14 instanceof class_1690) {
               class_1690 boatEntity = (class_1690)var14;
               if (boatEntity.method_31483() != null && boatEntity.method_31483().equals(player)) {
                  Optional<UUID> boatEngineUuid = ((BoatEngineCoupler)boatEntity).boatism$getBoatEngineEntityUuid();
                  boatEngineUuid.ifPresent((uuid) -> {
                     Optional<BoatEngineEntity> boatEngineEntity = EntityHandler.getBoatEngineEntityFromUuid(uuid, player.method_37908(), player.method_19538(), 10);
                     if (!boatEngineEntity.isEmpty() && ((BoatEngineEntity)boatEngineEntity.get()).isRunning()) {
                        class_2540 buf = PacketByteBufs.create();
                        buf.method_52940(delta);
                        ClientPlayNetworking.send(BoatismNetworkIdentifiers.SCROLLED.getIdentifier(), buf);
                        ci.cancel();
                     }
                  });
               }
            }
         }
      }
   }
}
