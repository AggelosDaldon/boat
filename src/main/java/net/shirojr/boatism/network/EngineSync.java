package net.shirojr.boatism.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EngineSync {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Boatism.MOD_ID, "engine_sync"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static final String ROOT = "BoatismForgeEngine";
    private EngineSync() {}

    public static void register() {
        CHANNEL.messageBuilder(State.class, 0, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(State::encode).decoder(State::decode).consumerMainThread(State::handle).add();
    }

    public record State(int id, CompoundTag data) {
        static void encode(State packet, FriendlyByteBuf buf) {
            buf.writeVarInt(packet.id);
            buf.writeNbt(packet.data);
        }
        static State decode(FriendlyByteBuf buf) {
            return new State(buf.readVarInt(), buf.readNbt());
        }
        static void handle(State packet, Supplier<NetworkEvent.Context> context) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientReceiver.receive(packet));
            context.get().setPacketHandled(true);
        }
    }

    private static CompoundTag snapshot(Boat boat) {
        CompoundTag original = boat.getPersistentData();
        CompoundTag state = new CompoundTag();
        for (String suffix : new String[]{"Installed", "Running", "Exhaust", "Canister", "StrappedCanister", "Plates"}) {
            state.putBoolean(suffix, original.getBoolean(ROOT + suffix));
        }
        state.putInt("Fuel", original.getInt(ROOT + "Fuel"));
        return state;
    }

    @SubscribeEvent
    public static void tracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof Boat boat) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new State(boat.getId(), snapshot(boat)));
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof net.minecraft.server.level.ServerLevel level)) return;
        if (level.getGameTime() % 5 != 0) return;
        // Send periodic snapshots to tracking players. This also avoids stale
        // entity-ID hash entries across worlds and missed state transitions.
        for (var entity : level.getAllEntities()) {
            if (entity instanceof Boat boat) {
                CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> boat),
                    new State(boat.getId(), snapshot(boat)));
            }
        }
    }
}
