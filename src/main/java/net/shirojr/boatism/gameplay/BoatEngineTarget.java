package net.shirojr.boatism.gameplay;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Virtual, boat-local hitbox for the stern motor. */
public final class BoatEngineTarget {
    private BoatEngineTarget() {}

    private static Vec3 local(Boat boat, Vec3 world) {
        double yaw = Math.toRadians(180.0 - boat.getYRot());
        double sin = Math.sin(yaw), cos = Math.cos(yaw);
        double x = world.x - boat.getX();
        double z = world.z - boat.getZ();
        return new Vec3(x * cos - z * sin, world.y - boat.getY(), x * sin + z * cos);
    }

    /** Returns the first motor hit distance along a camera ray, or infinity. */
    public static double hitDistance(Boat boat, Vec3 eyes, Vec3 look, double reach) {
        if (look.lengthSqr() < 1.0e-7) return Double.POSITIVE_INFINITY;
        Vec3 from = local(boat, eyes);
        Vec3 to = local(boat, eyes.add(look.normalize().scale(reach)));
        double attachY = boat.getVariant() == Boat.Type.BAMBOO
            ? boat.getBbHeight() * 0.7 : boat.getBbHeight() / 3.0;
        double attachZ = boat.getVariant() == Boat.Type.BAMBOO ? 1.2 : 1.32;
        AABB bounds = new AABB(-0.48, attachY - 0.69, attachZ - 0.56,
            0.48, attachY + 0.85, attachZ + 0.65);
        return bounds.clip(from, to).map(from::distanceTo).orElse(Double.POSITIVE_INFINITY);
    }
}
