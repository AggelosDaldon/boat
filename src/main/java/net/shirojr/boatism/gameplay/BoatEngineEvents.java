package net.shirojr.boatism.gameplay;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.shirojr.boatism.registry.BoatismSounds;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.registry.BoatismItems;

/**
 * Experimental Forge boat propulsion bridge. Not a replacement for the original
 * BoatEngineEntity, GUI, inventory, animations, or Fabric mixins.
 */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BoatEngineEvents {
    private static final String ROOT = "BoatismForgeEngine";
    private BoatEngineEvents() {}

    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Boat boat)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        CompoundTag data = boat.getPersistentData();
        if (held.isEmpty()) {
            // Sneak + empty hand on an installed motor toggles its ignition.
            // Normal right-click remains available for entering the boat.
            if (!event.getEntity().isShiftKeyDown() || !data.getBoolean(ROOT + "Installed")) return;
            if (!boat.level().isClientSide) {
                boolean enabled = !data.getBoolean(ROOT + "Enabled");
                if (enabled && data.getInt(ROOT + "Fuel") <= 0) {
                    levelSound(boat, BoatismSounds.BOAT_ENGINE_START_FAIL.get());
                    event.getEntity().displayClientMessage(Component.literal("Add fuel before starting the engine"), true);
                } else {
                    data.putBoolean(ROOT + "Enabled", enabled);
                    if (!enabled) data.putBoolean(ROOT + "Running", false);
                    event.getEntity().displayClientMessage(Component.literal(enabled ? "Boat engine switched on" : "Boat engine switched off"), true);
                    if (!enabled) levelSound(boat, BoatismSounds.BOAT_ENGINE_STOP.get());
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
                data.putInt(ROOT + "Fuel", 0);
                levelSound(boat, BoatismSounds.BOAT_ENGINE_EQUIP.get());
                if (!event.getEntity().getAbilities().instabuild) held.shrink(1);
                event.getEntity().displayClientMessage(Component.literal("Engine installed! Fuel it, then sneak-right-click with an empty hand to turn it on."), true);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(boat.level().isClientSide));
            event.setCanceled(true);
        } else if (held.is(BoatismItems.FUEL_BUCKET.get()) && data.getBoolean(ROOT + "Installed")) {
            if (!boat.level().isClientSide) {
                data.putInt(ROOT + "Fuel", Math.min(fuelCapacity(data), data.getInt(ROOT + "Fuel") + 6000));
                levelSound(boat, BoatismSounds.BOAT_ENGINE_POUR.get());
                data.putBoolean(ROOT + "LowFuelWarned", false);
                if (!event.getEntity().getAbilities().instabuild) {
                    held.shrink(1);
                    ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                    if (!event.getEntity().getInventory().add(emptyBucket)) event.getEntity().drop(emptyBucket, false);
                }
                event.getEntity().displayClientMessage(Component.literal("Fuel: " + data.getInt(ROOT + "Fuel") + "/" + fuelCapacity(data)), true);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(boat.level().isClientSide));
            event.setCanceled(true);
        } else if (data.getBoolean(ROOT + "Installed")) {
            String upgrade = null;
            if (held.is(BoatismItems.COMPONENT_EXHAUST.get())) upgrade = "Exhaust";
            else if (held.is(BoatismItems.COMPONENT_CANISTER.get())) upgrade = "Canister";
            else if (held.is(BoatismItems.COMPONENT_CANISTER_STRAPPED.get())) upgrade = "StrappedCanister";
            else if (held.is(BoatismItems.COMPONENT_PLATES.get())) upgrade = "Plates";
            if (upgrade != null && !data.getBoolean(ROOT + upgrade)) {
                if (!boat.level().isClientSide) {
                    data.putBoolean(ROOT + upgrade, true);
                    // In the original Boatism, placing a normal canister
                    // displays the strapped item model on the motor.
                    if (upgrade.equals("Canister")) data.putBoolean(ROOT + "StrappedCanister", true);
                    if (upgrade.equals("Canister") || upgrade.equals("StrappedCanister")) {
                        event.getEntity().displayClientMessage(Component.literal("Fuel capacity: " + fuelCapacity(data)), true);
                    }
                    levelSound(boat, BoatismSounds.BOAT_ENGINE_EQUIP.get());
                    if (!event.getEntity().getAbilities().instabuild) held.shrink(1);
                    event.getEntity().displayClientMessage(Component.literal("Installed " + upgrade), true);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(boat.level().isClientSide));
                event.setCanceled(true);
            }
        }
    }

    private static void displayStatus(Boat boat, CompoundTag data, boolean running) {
        String mode = running ? "RUNNING"
            : data.getInt(ROOT + "Fuel") <= 0 ? "NO FUEL"
            : data.getBoolean(ROOT + "Enabled") ? "READY" : "OFF";
        int fuelPercent = Math.round(100.0f * data.getInt(ROOT + "Fuel") / fuelCapacity(data));
        double blocksPerSecond = Math.round(boat.getDeltaMovement().horizontalDistance() * 200.0) / 10.0;
        String upgrades = (data.getBoolean(ROOT + "Exhaust") ? " +Exhaust" : "")
            + (data.getBoolean(ROOT + "StrappedCanister") || data.getBoolean(ROOT + "Canister") ? " +Canister" : "")
            + (data.getBoolean(ROOT + "Plates") ? " +Plates" : "");
        Component text = Component.literal("Motor: " + mode + "  |  Fuel: " + fuelPercent
            + "%  |  Speed: " + blocksPerSecond + " blocks/s" + upgrades);
        for (Entity rider : boat.getPassengers()) {
            if (rider instanceof ServerPlayer player) player.displayClientMessage(text, true);
        }
    }

    private static int fuelCapacity(CompoundTag data) {
        if (data.getBoolean(ROOT + "StrappedCanister") || data.getBoolean(ROOT + "Canister")) return 36000;
        return 24000;
    }

    private static void levelSound(Boat boat, net.minecraft.sounds.SoundEvent sound) {
        boat.level().playSound(null, boat.getX(), boat.getY(), boat.getZ(), sound, SoundSource.NEUTRAL, 0.7f, 1.0f);
    }

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Boat boat)) continue;
            CompoundTag data = boat.getPersistentData();
            if (!data.getBoolean(ROOT + "Installed")) continue;
            if (data.getInt(ROOT + "Fuel") <= 0) {
                if (data.getBoolean(ROOT + "Running")) {
                    data.putBoolean(ROOT + "Running", false);
                    levelSound(boat, BoatismSounds.BOAT_ENGINE_STOP.get());
                }
                if (level.getGameTime() % 20 == 0) displayStatus(boat, data, false);
                continue;
            }
            // Boat.getStatus() is private in 1.20.1 Mojmap. Check the
            // fluid immediately under the hull as well as immersion.
            boolean inWater = boat.isInWaterOrBubble()
                || boat.level().getFluidState(boat.blockPosition().below()).is(FluidTags.WATER);
            boolean running = data.getBoolean(ROOT + "Enabled")
                && !boat.getPassengers().isEmpty() && inWater;
            boolean wasRunning = data.getBoolean(ROOT + "Running");
            if (running != wasRunning) {
                data.putBoolean(ROOT + "Running", running);
                levelSound(boat, running ? BoatismSounds.BOAT_ENGINE_START.get() : BoatismSounds.BOAT_ENGINE_STOP.get());
            }
            if (!running) {
                if (level.getGameTime() % 20 == 0) displayStatus(boat, data, false);
                continue;
            }
            if (level.getGameTime() % 35 == 0) levelSound(boat, BoatismSounds.BOAT_ENGINE_DEFAULT.get());
            if (data.getInt(ROOT + "Fuel") <= 200 && !data.getBoolean(ROOT + "LowFuelWarned")) {
                data.putBoolean(ROOT + "LowFuelWarned", true);
                levelSound(boat, BoatismSounds.BOAT_ENGINE_LOW_FUEL.get());
            }
            Vec3 velocity = boat.getDeltaMovement();
            Vec3 heading = boat.getLookAngle();
            double acceleration = data.getBoolean(ROOT + "Exhaust") ? 0.055 : 0.045;
            double maxSpeed = data.getBoolean(ROOT + "Exhaust") ? 0.72 : 0.60;
            Vec3 horizontal = new Vec3(velocity.x + heading.x * acceleration, 0, velocity.z + heading.z * acceleration);
            if (horizontal.horizontalDistanceSqr() > maxSpeed * maxSpeed) horizontal = horizontal.normalize().scale(maxSpeed);
            boat.setDeltaMovement(horizontal.x, velocity.y, horizontal.z);
            boat.hurtMarked = true;
            data.putInt(ROOT + "Fuel", data.getInt(ROOT + "Fuel") - 1);
            if (level.getGameTime() % 20 == 0) displayStatus(boat, data, true);
            if (data.getInt(ROOT + "Fuel") == 0) {
                data.putBoolean(ROOT + "Running", false);
                data.putBoolean(ROOT + "Enabled", false);
                levelSound(boat, BoatismSounds.BOAT_ENGINE_STOP.get());
            }
        }
    }
}
