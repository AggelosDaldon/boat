package net.shirojr.boatism.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.network.ClientReceiver;
import net.shirojr.boatism.network.EngineSync;
import org.lwjgl.glfw.GLFW;

/**
 * Fabric Boatism's throttle is persistent: scrolling changes power 0-9,
 * and releasing W does not reset it. Level 0 is neutral/idle.
 */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class BoatEngineClientControl {
    public static final KeyMapping POWER_UP = new KeyMapping(
        "key.boatism.power_level_up", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.boatism.group");
    public static final KeyMapping POWER_DOWN = new KeyMapping(
        "key.boatism.power_level_down", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.boatism.group");

    private BoatEngineClientControl() {}

    @Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class KeyRegistration {
        @SubscribeEvent
        public static void register(RegisterKeyMappingsEvent event) {
            event.register(POWER_UP);
            event.register(POWER_DOWN);
        }
    }

    @SubscribeEvent
    public static void scroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || !(mc.player.getVehicle() instanceof Boat boat)) return;
        CompoundTag state = ClientReceiver.get(boat.getId());
        if (state == null || !state.getBoolean("Running") || !state.getBoolean("Installed")) return;
        int change = (int) Math.signum(event.getScrollDelta());
        if (change == 0) return;
        changePower(boat, change);
        event.setCanceled(true);
    }

    private static void changePower(Boat boat, int delta) {
        CompoundTag state = ClientReceiver.get(boat.getId());
        if (state == null || !state.getBoolean("Running")) return;
        int limit = state.getFloat("Fuel") < (state.getBoolean("Canister") || state.getBoolean("StrappedCanister") ? 1300f : 200f)
            ? 3 : 9;
        int oldPower = state.getInt("Power");
        int newPower = Mth.clamp(oldPower + Integer.signum(delta), 0, limit);
        if (newPower == oldPower) return;
        // Local prediction keeps movement and the engine sound responsive.
        state.putInt("Power", newPower);
        EngineSync.CHANNEL.sendToServer(new EngineSync.PowerDelta(Integer.signum(delta)));
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        if (mc.player.getVehicle() instanceof Boat controlledBoat) {
            while (POWER_UP.consumeClick()) changePower(controlledBoat, 1);
            while (POWER_DOWN.consumeClick()) changePower(controlledBoat, -1);

            CompoundTag state = ClientReceiver.get(controlledBoat.getId());
            if (state != null && state.getBoolean("Running") && state.getFloat("Fuel") > 0) {
                int power = Mth.clamp(state.getInt("Power"), 0, 9);
                if (power > 0) {
                    Vec3 movement = controlledBoat.getDeltaMovement();
                    Vec3 heading = controlledBoat.getLookAngle();
                    double acceleration = power * 0.011;
                    double maxSpeed = power * 0.10;
                    Vec3 horizontal = new Vec3(
                        movement.x + heading.x * acceleration, 0.0,
                        movement.z + heading.z * acceleration
                    );
                    if (horizontal.horizontalDistanceSqr() > maxSpeed * maxSpeed) {
                        horizontal = horizontal.normalize().scale(maxSpeed);
                    }
                    controlledBoat.setDeltaMovement(horizontal.x, movement.y, horizontal.z);
                }
            }
        }

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Boat boat)) continue;
            CompoundTag state = ClientReceiver.get(boat.getId());
            if (state == null || !state.getBoolean("Installed") || !state.getBoolean("Running")) continue;

            double attachmentY = boat.getBbHeight() / 3.0;
            double attachmentZ = 1.32;
            if (boat.getVariant() == Boat.Type.BAMBOO) {
                attachmentY = boat.getBbHeight() * 0.7;
                attachmentZ = 1.2;
            }
            double yaw = Math.toRadians(180.0 - boat.getYRot());
            double cos = Math.cos(yaw), sin = Math.sin(yaw);

            // The highest point comes directly from the element named
            // "smoke particle here", so future model changes stay aligned.
            // Smoke is only enabled while the engine is RUNNING, including idle.
            BoatEngineGeometry.SmokeOutlet outlet = BoatEngineGeometry.get().smokeOutlet();
            if (outlet != null && mc.level.random.nextInt(3) == 0) {
                double modelX = (outlet.x() - 8.0) / 16.0 * 1.5;
                double modelZ = attachmentZ + (outlet.z() - 8.0) / 16.0 * 1.5;
                double x = boat.getX() + modelX * cos + modelZ * sin;
                double z = boat.getZ() - modelX * sin + modelZ * cos;
                double y = boat.getY() + attachmentY + outlet.y() / 16.0 * 1.5 + 0.025;
                mc.level.addParticle(ParticleTypes.SMOKE,
                    x + (mc.level.random.nextDouble() - 0.5) * 0.035,
                    y,
                    z + (mc.level.random.nextDouble() - 0.5) * 0.035,
                    (mc.level.random.nextDouble() - 0.5) * 0.005,
                    0.012 + mc.level.random.nextDouble() * 0.009,
                    (mc.level.random.nextDouble() - 0.5) * 0.005);
            }

            int power = Mth.clamp(state.getInt("Power"), 0, 9);
            if (power <= 0 || !boat.isInWaterOrBubble()) continue;

            // Visible prop wash at the lowered propeller group's actual pivot
            // (8, -4, 6). More throttle means denser bubbles and splashes.
            double propZ = attachmentZ + (6.0 - 8.0) / 16.0 * 1.5;
            double propX = boat.getX() + propZ * sin;
            double propWorldZ = boat.getZ() + propZ * cos;
            double propY = boat.getY() + attachmentY - 4.0 / 16.0 * 1.5;
            if (mc.level.random.nextInt(2) == 0) {
                int bubbles = 1 + Math.min(3, power / 3);
                for (int i = 0; i < bubbles; i++) {
                    double dx = (mc.level.random.nextDouble() - 0.5) * 0.28;
                    double dz = (mc.level.random.nextDouble() - 0.5) * 0.28;
                    mc.level.addParticle(ParticleTypes.BUBBLE,
                        propX + dx, propY + (mc.level.random.nextDouble() - 0.5) * 0.12,
                        propWorldZ + dz,
                        dx * 0.03, 0.035 + power * 0.004, dz * 0.03);
                    if (mc.level.random.nextInt(3) == 0) {
                        mc.level.addParticle(ParticleTypes.BUBBLE_POP,
                            propX + dx, boat.getY() + 0.08, propWorldZ + dz,
                            0, 0.012, 0);
                    }
                }
            }
            if (mc.level.random.nextInt(4) == 0) {
                mc.level.addParticle(ParticleTypes.SPLASH,
                    propX + (mc.level.random.nextDouble() - 0.5) * 0.35,
                    boat.getY() + 0.09,
                    propWorldZ + (mc.level.random.nextDouble() - 0.5) * 0.35,
                    0, 0.045 + power * 0.006, 0);
            }
        }
    }
}
