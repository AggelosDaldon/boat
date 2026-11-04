package net.shirojr.boatism.gameplay;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.registry.BoatismItems;
import net.shirojr.boatism.registry.BoatismSounds;

/**
 * Forge 1.20.1 gameplay bridge. Original 0.1.1 throttle/upgrade stats are kept
 * here while a full separate engine entity and engine GUI are being ported.
 */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BoatEngineEvents {
    public static final String ROOT = "BoatismForgeEngine";
    // One full Fuel Bucket moves an engine about 5,000 horizontal blocks.
    private static final int BASE_FUEL_CAPACITY = 3000;
    private static final float FUEL_BUCKET_CONTENTS = 3000f;
    private static final float FUEL_PER_BLOCK = FUEL_BUCKET_CONTENTS / 5000f;
    private static final Map<Boat, Vec3> PREVIOUS_POSITIONS = new WeakHashMap<>();
    private BoatEngineEvents() {}

    public static int fuelCapacity(CompoundTag data) {
        return BASE_FUEL_CAPACITY;
    }

    private static void levelSound(Boat boat, SoundEvent sound) {
        boat.level().playSound(null, boat.getX(), boat.getY(), boat.getZ(),
            sound, SoundSource.NEUTRAL, 0.7f, 1.0f);
    }

    public static void adjustPowerLevel(Boat boat, ServerPlayer player, int delta) {
        CompoundTag data = boat.getPersistentData();
        if (!data.getBoolean(ROOT + "Installed") || !data.getBoolean(ROOT + "Running")) return;
        int previous = data.getInt(ROOT + "Power");
        int max = data.getFloat(ROOT + "Fuel") < fuelCapacity(data) * 0.2f ? 3 : 9;
        int requested = Mth.clamp(previous + Integer.signum(delta), 0, max);
        if (requested == previous) return;
        data.putInt(ROOT + "Power", requested);
        levelSound(boat, requested > previous
            ? BoatismSounds.BOAT_ENGINE_POWER_UP.get() : BoatismSounds.BOAT_ENGINE_POWER_DOWN.get());
    }

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Boat boat)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        CompoundTag data = boat.getPersistentData();

        if (held.isEmpty()) {
            // This bridge uses the hull for ignition until the original
            // interactable BoatEngineEntity hitbox has been ported.
            if (!event.getEntity().isShiftKeyDown() || !data.getBoolean(ROOT + "Installed")) return;
            if (!boat.level().isClientSide) {
                boolean enabled = !data.getBoolean(ROOT + "Enabled");
                if (enabled && data.getFloat(ROOT + "Fuel") < 5.0f) {
                    levelSound(boat, BoatismSounds.BOAT_ENGINE_START_FAIL.get());
                    event.getEntity().displayClientMessage(Component.literal("Add fuel before starting the engine"), true);
                } else {
                    data.putBoolean(ROOT + "Enabled", enabled);
                    if (!enabled) {
                        data.putBoolean(ROOT + "Running", false);
                        data.putInt(ROOT + "Power", 0);
                        levelSound(boat, BoatismSounds.BOAT_ENGINE_STOP.get());
                    }
                    event.getEntity().displayClientMessage(
                        Component.literal(enabled ? "Boat engine switched on (power 0)" : "Boat engine switched off"), true);
                }
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(boat.level().isClientSide));
            event.setCanceled(true);
            return;
        }

        if (held.is(BoatismItems.BASE_ENGINE.get()) && !data.getBoolean(ROOT + "Installed")) {
            if (!boat.level().isClientSide) {
                data.putBoolean(ROOT + "Installed", true);
                data.putBoolean(ROOT + "Enabled", false);
                data.putBoolean(ROOT + "Running", false);
                data.putFloat(ROOT + "Fuel", 0f);
                data.putInt(ROOT + "Power", 0);
                levelSound(boat, BoatismSounds.BOAT_ENGINE_EQUIP.get());
                if (!event.getEntity().getAbilities().instabuild) held.shrink(1);
                event.getEntity().displayClientMessage(
                    Component.literal("Engine installed! Fuel it and sneak-right-click the boat to start."), true);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(boat.level().isClientSide));
            event.setCanceled(true);
            return;
        }

        if (held.is(BoatismItems.FUEL_BUCKET.get()) && data.getBoolean(ROOT + "Installed")) {
            if (!boat.level().isClientSide) {
                if (data.getBoolean(ROOT + "Running")) {
                    event.getEntity().displayClientMessage(Component.literal("Stop the engine before refueling"), true);
                } else {
                    float contents = FUEL_BUCKET_CONTENTS;
                    if (held.hasTag() && held.getTag().contains("BucketFuel")) {
                        contents = held.getTag().getFloat("BucketFuel");
                    }
                    float fuel = Math.min(fuelCapacity(data), data.getFloat(ROOT + "Fuel") + contents);
                    data.putFloat(ROOT + "Fuel", fuel);
                    data.putBoolean(ROOT + "LowFuelWarned", false);
                    levelSound(boat, BoatismSounds.BOAT_ENGINE_POUR.get());
                    if (!event.getEntity().getAbilities().instabuild) {
                        held.shrink(1);
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (!event.getEntity().getInventory().add(emptyBucket)) {
                            event.getEntity().drop(emptyBucket, false);
                        }
                    }
                    event.getEntity().displayClientMessage(Component.literal("Fuel: " + (int) fuel
                        + "/" + fuelCapacity(data)), true);
                }
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(boat.level().isClientSide));
            event.setCanceled(true);
            return;
        }

    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Boat boat)) continue;
            CompoundTag data = boat.getPersistentData();
            if (!data.getBoolean(ROOT + "Installed")) continue;
            // Clean old upgrades and heat data from boats saved with earlier builds.
            data.remove(ROOT + "Heat");
            data.remove(ROOT + "Exhaust");
            data.remove(ROOT + "Plates");
            data.remove(ROOT + "Canister");
            data.remove(ROOT + "StrappedCanister");
            float fuel = Math.min(data.getFloat(ROOT + "Fuel"), fuelCapacity(data));
            if (fuel != data.getFloat(ROOT + "Fuel")) data.putFloat(ROOT + "Fuel", fuel);

            boolean running = data.getBoolean(ROOT + "Enabled") && fuel >= 1f;
            boolean wasRunning = data.getBoolean(ROOT + "Running");
            if (running != wasRunning) {
                data.putBoolean(ROOT + "Running", running);
                levelSound(boat, running ? BoatismSounds.BOAT_ENGINE_START.get()
                    : BoatismSounds.BOAT_ENGINE_STOP.get());
            }
            if (!running) {
                data.putInt(ROOT + "Power", 0);
                PREVIOUS_POSITIONS.remove(boat);
                continue;
            }

            int power = data.getInt(ROOT + "Power");
            int maxPower = fuel < fuelCapacity(data) * 0.2f ? 3 : 9;
            if (power > maxPower) {
                power = maxPower;
                data.putInt(ROOT + "Power", power);
            }
            boolean inWater = boat.isInWaterOrBubble()
                || boat.level().getFluidState(boat.blockPosition().below()).is(FluidTags.WATER);

            // Vanilla simulates player-operated boats client-side. Server
            // thrust here is only for boats without a controlling player.
            if (power > 0 && inWater && !(boat.getControllingPassenger() instanceof ServerPlayer)) {
                Vec3 motion = boat.getDeltaMovement();
                Vec3 heading = boat.getLookAngle();
                double acceleration = 0.011 * power;
                double maxSpeed = 0.10 * power;
                Vec3 horizontal = new Vec3(motion.x + heading.x * acceleration, 0,
                    motion.z + heading.z * acceleration);
                if (horizontal.horizontalDistanceSqr() > maxSpeed * maxSpeed) {
                    horizontal = horizontal.normalize().scale(maxSpeed);
                }
                boat.setDeltaMovement(horizontal.x, motion.y, horizontal.z);
                boat.hurtMarked = true;
            }

            // Charge only for actual horizontal distance, not time spent idling.
            // Ignore the first position and abnormal jumps (teleport/dimension).
            Vec3 currentPosition = boat.position();
            Vec3 previousPosition = PREVIOUS_POSITIONS.put(boat, currentPosition);
            double blocks = previousPosition == null ? 0.0
                : Math.hypot(currentPosition.x - previousPosition.x,
                    currentPosition.z - previousPosition.z);
            float cost = blocks <= 8.0 ? (float) blocks * FUEL_PER_BLOCK : 0f;
            float remaining = Math.max(0.0f, fuel - cost);
            data.putFloat(ROOT + "Fuel", remaining);
            if (remaining <= fuelCapacity(data) * 0.2f && !data.getBoolean(ROOT + "LowFuelWarned")) {
                data.putBoolean(ROOT + "LowFuelWarned", true);
                levelSound(boat, BoatismSounds.BOAT_ENGINE_LOW_FUEL.get());
            }
            if (remaining <= 0.0f) {
                data.putBoolean(ROOT + "Running", false);
                data.putBoolean(ROOT + "Enabled", false);
                data.putInt(ROOT + "Power", 0);
                levelSound(boat, BoatismSounds.BOAT_ENGINE_STOP.get());
            }
        }
    }
}
