package net.shirojr.boatism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.network.ClientReceiver;

/**
 * Player-driven boats are simulated on the controlling client in vanilla.
 * Propulsion on the server alone is overwritten by client boat controls.
 * This handler adds thrust to the locally simulated boat and a small wake
 * for all nearby running engines. Fuel and ignition remain server-authoritative.
 */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class BoatEngineClientControl {
    private BoatEngineClientControl() {}

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        if (mc.player.getVehicle() instanceof Boat controlledBoat) {
            CompoundTag state = ClientReceiver.get(controlledBoat.getId());
            if (state != null && state.getBoolean("Running") && state.getInt("Fuel") > 0) {
                Vec3 movement = controlledBoat.getDeltaMovement();
                Vec3 heading = controlledBoat.getLookAngle();
                double acceleration = state.getBoolean("Exhaust") ? 0.055 : 0.045;
                double maxSpeed = state.getBoolean("Exhaust") ? 0.72 : 0.60;
                Vec3 horizontal = new Vec3(
                    movement.x + heading.x * acceleration,
                    0.0,
                    movement.z + heading.z * acceleration
                );
                if (horizontal.horizontalDistanceSqr() > maxSpeed * maxSpeed) {
                    horizontal = horizontal.normalize().scale(maxSpeed);
                }
                controlledBoat.setDeltaMovement(horizontal.x, movement.y, horizontal.z);
            }
        }

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Boat boat)) continue;
            CompoundTag state = ClientReceiver.get(boat.getId());
            if (state == null || !state.getBoolean("Running")) continue;
            if (mc.level.random.nextInt(3) != 0) continue;

            // Mirror the stern offset of the world-space motor renderer.
            double yaw = Math.toRadians(180.0 - boat.getYRot());
            double sternX = boat.getX() + Math.sin(yaw) * 1.5;
            double sternZ = boat.getZ() + Math.cos(yaw) * 1.5;
            double waterY = boat.getY() - 0.2;
            mc.level.addParticle(ParticleTypes.BUBBLE,
                sternX + (mc.level.random.nextDouble() - 0.5) * 0.3,
                waterY,
                sternZ + (mc.level.random.nextDouble() - 0.5) * 0.3,
                0.0, 0.02, 0.0);
            if (mc.level.random.nextInt(4) == 0) {
                mc.level.addParticle(ParticleTypes.SPLASH,
                    sternX, boat.getY() + 0.1, sternZ,
                    0.0, 0.03, 0.0);
            }
        }
    }
}
