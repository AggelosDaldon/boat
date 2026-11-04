package net.shirojr.boatism.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_7157;
import net.minecraft.class_1297.class_5529;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.api.BoatEngineCoupler;
import net.shirojr.boatism.entity.BoatismEntities;
import net.shirojr.boatism.network.BoatismNetworkIdentifiers;

public class BoatEngineEntityCommand {
   public static void register(CommandDispatcher<class_2168> dispatcher, class_7157 commandRegistryAccess, class_2170.class_5364 registrationEnvironment) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247(Boatism.MODID).then(class_2170.method_9247("sound").then(class_2170.method_9247("stop").executes(BoatEngineEntityCommand::stopAllSoundInstances)))).then(((LiteralArgumentBuilder)class_2170.method_9247("entities").requires((serverCommandSource) -> serverCommandSource.method_9259(2))).then(class_2170.method_9247("remove").executes(BoatEngineEntityCommand::removeBoatEngineEntities))));
   }

   private static int stopAllSoundInstances(CommandContext<class_2168> context) {
      class_2540 buf = PacketByteBufs.create();
      class_3222 serverPlayerEntity = ((class_2168)context.getSource()).method_44023();
      if (serverPlayerEntity == null) {
         return 0;
      } else {
         ServerPlayNetworking.send(serverPlayerEntity, BoatismNetworkIdentifiers.SOUND_END_ALL.getIdentifier(), buf);
         return 1;
      }
   }

   private static int removeBoatEngineEntities(CommandContext<class_2168> context) throws CommandSyntaxException {
      Iterable<class_3218> serverWorlds = ((class_2168)context.getSource()).method_9211().method_3738();
      serverWorlds.forEach((serverWorld) -> serverWorld.method_18198(BoatismEntities.BOAT_ENGINE, (boatEngine) -> true).forEach((boatEngine) -> {
            ((class_2168)context.getSource()).method_9226(() -> {
               String var10000 = ((class_2168)context.getSource()).method_9214();
               return class_2561.method_43470(var10000 + " removed " + boatEngine.toString());
            }, true);
            boatEngine.getHookedBoatEntity().ifPresent((boatEntity) -> ((BoatEngineCoupler)boatEntity).boatism$setBoatEngineEntity((UUID)null));
            boatEngine.method_5650(class_5529.field_26999);
         }));
      return stopAllSoundInstances(context);
   }
}
