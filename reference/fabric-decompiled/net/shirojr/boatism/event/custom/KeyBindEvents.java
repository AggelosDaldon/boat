package net.shirojr.boatism.event.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.class_2540;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import net.minecraft.class_3675.class_307;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.network.BoatismNetworkIdentifiers;

public class KeyBindEvents {
   private static class_304 POWER_LEVEL_UP;
   private static class_304 POWER_LEVEL_DOWN;
   private static final String BOATISM_KEYBINDINGS_GROUP;

   public static void register() {
      POWER_LEVEL_UP = KeyBindingHelper.registerKeyBinding(new class_304("key.%s.power_level_up".formatted(Boatism.MODID), class_307.field_1668, class_3675.field_16237.method_1444(), BOATISM_KEYBINDINGS_GROUP));
      POWER_LEVEL_DOWN = KeyBindingHelper.registerKeyBinding(new class_304("key.%s.power_level_down".formatted(Boatism.MODID), class_307.field_1668, class_3675.field_16237.method_1444(), BOATISM_KEYBINDINGS_GROUP));
      ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick)(client) -> {
         if (client.field_1724 != null) {
            while(POWER_LEVEL_UP.method_1436()) {
               sendPowerLevelChangePacket(1);
            }

            while(POWER_LEVEL_DOWN.method_1436()) {
               sendPowerLevelChangePacket(-1);
            }

         }
      });
   }

   private static void sendPowerLevelChangePacket(int delta) {
      class_2540 buf = PacketByteBufs.create();
      buf.method_52940((double)delta);
      ClientPlayNetworking.send(BoatismNetworkIdentifiers.SCROLLED.getIdentifier(), buf);
   }

   static {
      BOATISM_KEYBINDINGS_GROUP = "key.%s.group".formatted(Boatism.MODID);
   }
}
