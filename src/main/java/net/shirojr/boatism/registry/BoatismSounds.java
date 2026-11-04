package net.shirojr.boatism.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.shirojr.boatism.Boatism;

public final class BoatismSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Boatism.MOD_ID);
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_DEFAULT = SOUNDS.register("boat_engine_default", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_default")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_HIT = SOUNDS.register("boat_engine_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_hit")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_LOW_FUEL = SOUNDS.register("boat_engine_low_fuel", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_low_fuel")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_LOW_HEALTH = SOUNDS.register("boat_engine_low_health", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_low_health")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_UNDERWATER = SOUNDS.register("boat_engine_underwater", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_underwater")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_POWER_UP = SOUNDS.register("boat_engine_power_up", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_power_up")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_POWER_DOWN = SOUNDS.register("boat_engine_power_down", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_power_down")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_START = SOUNDS.register("boat_engine_start", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_start")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_START_FAIL = SOUNDS.register("boat_engine_start_fail", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_start_fail")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_HEAT = SOUNDS.register("boat_engine_heat", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_heat")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_STOP = SOUNDS.register("boat_engine_stop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_stop")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_POUR = SOUNDS.register("boat_engine_pour", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_pour")));
    public static final RegistryObject<SoundEvent> BOAT_ENGINE_EQUIP = SOUNDS.register("boat_engine_equip", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Boatism.MOD_ID, "boat_engine_equip")));
    private BoatismSounds() {}
}
