package net.shirojr.boatism;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.shirojr.boatism.registry.BoatismItems;
import net.shirojr.boatism.registry.BoatismSounds;
import net.shirojr.boatism.network.EngineSync;

/** Forge bootstrap. Gameplay/entity migration remains pending. */
@Mod(Boatism.MOD_ID)
public final class Boatism {
    public static final String MOD_ID = "boatism";
    public Boatism() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BoatismItems.ITEMS.register(bus);
        BoatismItems.TABS.register(bus);
        BoatismSounds.SOUNDS.register(bus);
        EngineSync.register();
    }
}
