# Boatism — custom Forge 1.20.1 port (Stage 21)

Minecraft **1.20.1**, Forge **47.4.22**, Java **17**.

This is an independent, work-in-progress Forge adaptation of the MIT-licensed [Boatism](https://github.com/JR1811/Boatism) by ShiroJR and collaborators. The original license and credits are retained.

## Simplified gameplay

Only two items remain: **Boat Engine** and **Fuel Bucket**. Fuel Canister and Fuel Canister Strapped (including crafting, models, textures and upgrades), overheating, Custom Exhaust and Custom Plating are intentionally removed. Previously saved installed canister flags are cleared on server tick.

1. Right-click a boat with the **Boat Engine** item to install the motor.
2. Right-click the installed motorized boat with a **Fuel Bucket** to fill its 3,000-unit tank. This returns an empty bucket in survival. Stop the engine before refueling.
3. Sneak + right-click the boat with an **empty main hand** to switch the motor on/off.
4. Sit in the boat and scroll to set **Gear 0–9** (persistent throttle, where 0 is idle). Optionally bind Power Up/Down keys in Controls.
5. While the motor is running, a small persistent HUD **to the right of the hotbar** displays current Gear and Fuel, with color-coded gear and fuel indicators.
6. Fuel burns based on horizontal distance traveled: **3,000 fuel per 5,000 blocks**, or approximately one full Fuel Bucket per 5,000 blocks under normal movement. It does not burn while stationary. The last 20% of fuel limits maximum gear to 3.

## Stage 21: geometry, hotbar icon and fuel overhaul

- Converted the owner's custom Blockbench model UV coordinates from its 64-unit UV atlas into Minecraft's built-in 16-unit item-model UV coordinate system, without modifying the original 128×128 texture. This corrects the engine item icon in the hotbar; the world renderer reads equivalent normalized UVs.
- Corrected the direction of 90/270-degree per-face UV rotation on the propeller blade. The propeller spins even in idle gear 0 while the engine runs and speeds up with gear.
- Increased bubbles and splashes produced by the propeller at low power, while keeping exhaust smoke at the top of the named `smoke particle here` element **only when running**.
- Removed both canister registrations and associated recipes, resources, rendering, synced state, upgrade interactions and capacity bonuses.
- Replaced the centered speed HUD with a smaller right-side **Gear + Fuel** panel.
- Fuel tank capacity is 3,000, fueled with one bucket, and consumption is distance-based instead of one unit per server tick.

## Source and limitations

The editable source `design/engine.bbmodel` and provided engine texture are preserved. The port still uses hull-attached engine rendering instead of the original separately interactable `BoatEngineEntity`; the original GUI/inventory, hit points and several compatibility features are not yet ported. This build is compiled by CI but still requires **in-game and multiplayer testing**.

The `reference/fabric-decompiled/` directory is original source reference material, **not compiled** into the mod.

## Building and downloading

[GitHub Actions](https://github.com/AggelosDaldon/boat/actions) builds with Gradle 8.8 and Java 17. Download the latest successful `boatism-forge-1.20.1` artifact ZIP, extract the JAR and place it in the Forge `mods` folder. **Remove older port JARs** first and back up a world with previously obtained canister items before updating.
