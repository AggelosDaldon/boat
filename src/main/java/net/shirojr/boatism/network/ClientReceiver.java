package net.shirojr.boatism.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientReceiver {
    private static final Map<Integer, CompoundTag> STATES = new ConcurrentHashMap<>();
    private static net.minecraft.client.multiplayer.ClientLevel lastLevel;
    private ClientReceiver() {}

    public static void receive(EngineSync.State packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (lastLevel != mc.level) {
            STATES.clear();
            lastLevel = mc.level;
        }
        if (mc.level.getEntity(packet.id()) instanceof net.minecraft.world.entity.vehicle.Boat) {
            STATES.put(packet.id(), packet.data().copy());
        }
    }

    public static CompoundTag get(int id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != lastLevel) {
            STATES.clear();
            lastLevel = mc.level;
        }
        return STATES.get(id);
    }
}
