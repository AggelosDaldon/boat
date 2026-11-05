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

/** Small always-on throttle/fuel HUD beside the hotbar while running. */
@Mod.EventBusSubscriber(modid = Boatism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT)
public final class BoatEngineHud {
    private static final int WIDTH = 108;
    private static final int HEIGHT = 30;

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

        int throttle = Mth.clamp(data.getInt("Power"), 0, 10);
        int capacity = 3000;
        float fuel = Mth.clamp(data.getFloat("Fuel"), 0f, capacity);
        float fuelFraction = fuel / capacity;
        int throttleColor = throttleTint(throttle);
        int fuelColor = fuelFraction > 0.4f ? 0xFF87CF9E
            : fuelFraction > 0.17f ? 0xFFFFCA7A : 0xFFFF7572;

        GuiGraphics gui = event.getGuiGraphics();
        // The normal hotbar spans centre +/- 91 px.
        // Put the HUD just outside its right edge where screen space allows.
        int requestedLeft = gui.guiWidth() / 2 + 97;
        int left = Math.min(gui.guiWidth() - WIDTH - 3, requestedLeft);
        int top = left == requestedLeft ? gui.guiHeight() - 35 : gui.guiHeight() - 71;
        gui.fill(left, top, left + WIDTH, top + HEIGHT, 0xCB151D22);
        gui.fill(left, top, left + 2, top + HEIGHT, throttleColor);

        gui.drawString(mc.font, "THROTTLE", left + 8, top + 5, 0xFFBCC9CC, false);
        String throttleText = Integer.toString(throttle);
        gui.drawString(mc.font, throttleText,
            left + WIDTH - 8 - mc.font.width(throttleText), top + 5, throttleColor, false);

        gui.drawString(mc.font, "FUEL", left + 8, top + 16, 0xFFBCC9CC, false);
        String fuelText = (int) Math.ceil(fuel) + "/" + capacity;
        gui.drawString(mc.font, fuelText,
            left + WIDTH - 6 - mc.font.width(fuelText), top + 16, fuelColor, false);
        gui.fill(left + 6, top + HEIGHT - 3, left + WIDTH - 6, top + HEIGHT - 2,
            0x665E6A6C);
        gui.fill(left + 6, top + HEIGHT - 3,
            left + 6 + Math.round((WIDTH - 12) * fuelFraction), top + HEIGHT - 2, fuelColor);
    }

    private static int throttleTint(int throttle) {
        float ratio = throttle / 10f;
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
