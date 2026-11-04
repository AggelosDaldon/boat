package net.shirojr.boatism.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shirojr.boatism.Boatism;
import net.shirojr.boatism.network.ClientReceiver;

/** Minimal, always-on speed and fuel readout above the vanilla hotbar. */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT)
public final class BoatEngineHud {
    private static final int WIDTH = 142;
    private static final int HEIGHT = 35;

    private BoatEngineHud() {}

    @SubscribeEvent
    public static void draw(RenderGuiOverlayEvent.Post event) {
        // The Post event is fired for each vanilla overlay; draw only once.
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui
            || !(mc.player.getVehicle() instanceof Boat boat)) return;
        CompoundTag data = ClientReceiver.get(boat.getId());
        if (data == null || !data.getBoolean("Installed") || !data.getBoolean("Running")) return;

        // 1 block is approximately 1 metre: 20 ticks/sec * 3.6 -> km/h.
        // This is an approximate in-game speed, not a physical boat speedometer.
        double speed = boat.getDeltaMovement().horizontalDistance() * 72.0;
        int capacity = data.getBoolean("Canister") || data.getBoolean("StrappedCanister")
            ? 6500 : 1000;
        float fuel = Mth.clamp(data.getFloat("Fuel"), 0f, (float) capacity);
        float fuelFraction = fuel / capacity;
        int speedColor = speedTint(speed);
        int fuelColor = fuelFraction > 0.4f ? 0xFF87CF9E
            : fuelFraction > 0.17f ? 0xFFFFCA7A : 0xFFFF7572;

        GuiGraphics gui = event.getGuiGraphics();
        int left = (gui.guiWidth() - WIDTH) / 2;
        int top = gui.guiHeight() - 83;
        gui.fill(left, top, left + WIDTH, top + HEIGHT, 0xC5151D22);
        gui.fill(left, top, left + 2, top + HEIGHT, speedColor);
        gui.fill(left + 6, top + 20, left + WIDTH - 6, top + 21, 0x665E6A6C);

        gui.drawString(mc.font, "SPEED", left + 9, top + 6, 0xFFBCC9CC, false);
        String speedText = String.format(java.util.Locale.ROOT, "%.1f km/h", speed);
        gui.drawString(mc.font, speedText,
            left + WIDTH - 9 - mc.font.width(speedText), top + 6, speedColor, false);

        gui.drawString(mc.font, "FUEL", left + 9, top + 23, 0xFFBCC9CC, false);
        String fuelText = (int) Math.ceil(fuel) + " / " + capacity;
        gui.drawString(mc.font, fuelText,
            left + WIDTH - 9 - mc.font.width(fuelText), top + 23, fuelColor, false);
        gui.fill(left + 8, top + HEIGHT - 2,
            left + 8 + Math.round((WIDTH - 16) * fuelFraction), top + HEIGHT - 1, fuelColor);
    }

    private static int speedTint(double kmh) {
        float ratio = Mth.clamp((float) (kmh / 48.0), 0.0f, 1.0f);
        if (ratio < 0.5f) return blend(0xFF77D7D1, 0xFFF6D074, ratio * 2f);
        return blend(0xFFF6D074, 0xFFFF746C, (ratio - 0.5f) * 2f);
    }

    private static int blend(int a, int b, float t) {
        int r = Math.round(Mth.lerp(t, (a >> 16) & 255, (b >> 16) & 255));
        int g = Math.round(Mth.lerp(t, (a >> 8) & 255, (b >> 8) & 255));
        int bl = Math.round(Mth.lerp(t, a & 255, b & 255));
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }
}
