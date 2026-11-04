package net.shirojr.boatism.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.shirojr.boatism.Boatism;

/** Registry-only migration: item behaviors are NOT ported yet. */
public final class BoatismItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Boatism.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Boatism.MOD_ID);
    public static final RegistryObject<Item> FUEL_BUCKET = ITEMS.register("fuel_bucket", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BASE_ENGINE = ITEMS.register("base_engine", () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
    public static final RegistryObject<Item> COMPONENT_EXHAUST = ITEMS.register("component_exhaust", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COMPONENT_CANISTER = ITEMS.register("component_canister", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COMPONENT_CANISTER_STRAPPED = ITEMS.register("component_canister_strapped", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COMPONENT_PLATES = ITEMS.register("component_plates", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<CreativeModeTab> BOATISM_TAB = TABS.register("boatism", () ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.boatism"))
            .icon(() -> new ItemStack(BASE_ENGINE.get()))
            .displayItems((params, output) -> {
                output.accept(FUEL_BUCKET.get());
                output.accept(BASE_ENGINE.get());
                output.accept(COMPONENT_EXHAUST.get());
                output.accept(COMPONENT_CANISTER.get());
                output.accept(COMPONENT_CANISTER_STRAPPED.get());
                output.accept(COMPONENT_PLATES.get());
            }).build());
    private BoatismItems() {}
}
