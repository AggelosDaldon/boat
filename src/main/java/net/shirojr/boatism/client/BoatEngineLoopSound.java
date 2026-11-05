package net.shirojr.boatism.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.network.ClientReceiver;
import net.shirojr.boatism.registry.BoatismSounds;

/**
 * A single spatial, looping engine recording per boat.
 * Original Boatism used a fading MovingSoundInstance; replaying the OGG every
 * 35 ticks causes choppy overlap and is deliberately replaced here.
 */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class BoatEngineLoopSound {
    private static final Map<UUID, EngineLoop> LOOPS = new HashMap<>();
    private static ClientLevel lastLevel;

    private BoatEngineLoopSound() {}

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != lastLevel) {
            LOOPS.clear();
            lastLevel = mc.level;
        }
        if (mc.level == null || mc.isPaused()) return;
        Iterator<EngineLoop> it = LOOPS.values().iterator();
        while (it.hasNext()) {
            if (it.next().finished) it.remove();
        }
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Boat boat)) continue;
            CompoundTag state = ClientReceiver.get(boat.getId());
            if (state == null || !state.getBoolean("Installed") || !state.getBoolean("Running")) continue;
            if (!LOOPS.containsKey(boat.getUUID())) {
                EngineLoop sound = new EngineLoop(boat);
                LOOPS.put(boat.getUUID(), sound);
                mc.getSoundManager().play(sound);
            }
        }
    }

    private static final class EngineLoop extends AbstractTickableSoundInstance {
        private final Boat boat;
        private float fade;
        private boolean finished;

        private EngineLoop(Boat boat) {
            super(BoatismSounds.BOAT_ENGINE_DEFAULT.get(), SoundSource.NEUTRAL,
                SoundInstance.createUnseededRandom());
            this.boat = boat;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.0f;
            this.pitch = 0.8f;
            this.x = boat.getX();
            this.y = boat.getY();
            this.z = boat.getZ();
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            CompoundTag state = ClientReceiver.get(boat.getId());
            boolean running = !boat.isRemoved() && state != null && state.getBoolean("Running");
            fade = Mth.clamp(fade + (running ? 0.01f : -0.0125f), 0.0f, 1.0f);
            if (!running && fade <= 0f) {
                finished = true;
                stop();
                return;
            }

            this.x = boat.getX();
            this.y = boat.getY();
            this.z = boat.getZ();
            int power = state == null ? 0 : Mth.clamp(state.getInt("Power"), 0, 10);
            // Fade in/out, then dynamically track load and boat speed.
            float speed = Mth.clamp((float) boat.getDeltaMovement().horizontalDistance(), 0f, 0.9f);
            this.volume = fade * Mth.clamp(0.45f + speed * 0.35f, 0.0f, 0.8f);
            this.pitch = Mth.clamp(0.8f + power * 0.035f + speed * 0.07f, 0.7f, 1.3f);
        }
    }
}
