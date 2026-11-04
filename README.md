# Boatism — custom Forge 1.20.1 port (Stage 19)

Minecraft **1.20.1**, Forge **47.4.22**, Java **17**.

This is an independent, work-in-progress Forge adaptation of the MIT-licensed [Boatism](https://github.com/JR1811/Boatism) by ShiroJR and collaborators. Remaining motor models, textures and audio originate from the original mod. See LICENSE and the attribution in `mods.toml`.

## Scope of this custom variant

Unlike the original Fabric release, this Forge port **intentionally omits overheating, the Custom Exhaust upgrade, and Custom Plating**. These are not planned gameplay features of this variant.

The only user-installable upgrade is the **Fuel Canister**:
- Install a regular Fuel Canister on an engine by right-clicking the motorized boat.
- The mounted canister renders using the *Fuel Canister Strapped* model automatically.
- Fuel Canister Strapped is an internal, registered render item. It does **not** appear in the Creative inventory and has **no crafting recipe**.

## Current controls

1. Right-click a boat with the **Boat Engine** item to install the motor.
2. Right-click the motorized boat with a **Fuel Bucket** to refuel it. This returns an empty bucket in survival. You must stop the engine first.
3. Sneak + right-click the boat with an **empty main hand** to switch the engine on or off.
4. Sit in the boat and scroll to set **Power Level 0–9**. Level 0 is neutral, and higher levels keep their throttle setting when W is released.
5. The power-up/down keybindings can also be assigned manually in Minecraft Controls.
6. Fuel capacity is 1,000 normally or 6,500 with the canister. Each Fuel Bucket fills up to 3,000 units, capped at current capacity.

Engine audio uses a position-tracked loop with volume fades and dynamic pitch.

## Stage 19: owner's updated engine model

The engine item and stern-mounted world model use the supplied `base_engine-01.json` geometry and its unmodified dark `texture.png` atlas. The engine's independently mapped UV faces are rendered directly in-world; the separately grouped, slightly lowered propeller still spins with throttle power.

A small vanilla smoke particle appears intermittently from the exact top centre of the element named `smoke particle here` (model x=7.5, y=11, z=13.5) when the engine is running, including at idle power 0. The boat and engine gameplay values are unchanged.

## Work in progress

This is **not yet a fully faithful port** of the original Boatism. It still uses a hull-attached renderer instead of a separate, interactable `BoatEngineEntity`; the original engine GUI/inventory, hit points, engine component removal and various compatibility behaviors are pending. Dedicated-server and in-game testing are still required.

## Build and download

The [GitHub Actions build workflow](https://github.com/AggelosDaldon/boat/actions) compiles with Gradle 8.8 and Java 17.

Download the latest successful `boatism-forge-1.20.1` artifact ZIP, extract the JAR and put the JAR in the Minecraft Forge `mods` folder. Remove older port JARs first.

For a local build, use `gradle build` from the repository root; the output is in `build/libs/`.

## Source reference

The `reference/fabric-decompiled/` directory preserves non-compiled upstream source reference material and is not included in the playable Forge mod.
