# Boatism — custom Forge 1.20.1 port (Stage 22)

Minecraft **1.20.1**, Forge **47.4.22**, Java **17**.

This is a custom, unfinished Forge adaptation of MIT-licensed [Boatism](https://github.com/JR1811/Boatism) by ShiroJR and collaborators. Original attribution/license are preserved.

## Controls and gameplay

1. Right-click the **boat hull** with the **Boat Engine** item to install a motor.
2. **Aim directly at the outboard motor model** and right-click with a **Fuel Bucket** to refuel it, returning an empty bucket in survival. The motor must be stopped.
3. **Aim directly at the outboard motor model** and right-click with an **empty main hand** to switch it ON/OFF. Sneaking is **not required**. Right-clicking elsewhere on the hull does not start or refuel the motor.
4. Once riding, use the mouse wheel (or customizable Throttle Up/Down keybindings) to set **Throttle 0–10**. Throttle 0 is neutral/idle; the motor idles and the propeller slowly rotates while ON.
5. The small persistent **THROTTLE + FUEL** HUD is to the right of the hotbar while riding with the motor ON.

**Fuel economy:** One bucket fills the 3,000-unit tank. Consumption is proportional to actual horizontal distance and throttle:

- **Throttle 0:** no fuel used, even if the boat drifts.
- **Throttle 1:** one tenth of the full-throttle fuel rate.
- **Throttle 5:** half the full-throttle fuel rate.
- **Throttle 10:** roughly **one bucket every 5,000 blocks** of travel.

When fuel falls below 20%, maximum throttle is limited to 3. Engine ignition, audio, steering and motion otherwise remain as in the prior stage.

## Stage 22 improvements

- Both parts of the propeller's animated group now have a **180° resting rotation** in the world model, while preserving their original cuboids and textures.
- New **Throttle 10**, including controls, speed/acceleration, sound pitch and HUD.
- New boat-yaw-aware **virtual motor hitbox** for clicking the actual outboard. Forge client-side right-click target detection sends a dedicated interaction packet; the server independently verifies the player's line-of-sight before toggling ignition or consuming Fuel Bucket.
- Refueling and ON/OFF **no longer trigger from clicking the general boat hull**.
- Existing custom engine texture/UV conversion, exhaust smoke while running, propeller bubbles and wake remain.

## Deliberate simplification

Only two mod items remain: **Boat Engine** and **Fuel Bucket**. Both Fuel Canisters, overheating, Custom Exhaust and Custom Plating are removed, including recipes and upgrade handling.

## Important limitations / testing

This remains a port-in-progress. The motor uses a **virtual click target** aligned with its renderer, not the original independently interactable engine entity; test aiming at the motor and clicking from different angles and from aboard the boat. Changes are verified by compilation and JAR resource checks; they still need actual Minecraft and multiplayer validation.

A world backup is recommended before upgrading because old canister items no longer exist. Install matching Stage 22 JARs on both server and client (network protocol updated).

## Build and installation

[GitHub Actions](https://github.com/AggelosDaldon/boat/actions) compiles with Gradle 8.8 and Java 17. Download the latest successful `boatism-forge-1.20.1` artifact, extract its JAR, and place it in your Forge `mods` folder. Remove all previous versions of this port first.

The `reference/fabric-decompiled/` directory preserves original reference source that is not compiled.
