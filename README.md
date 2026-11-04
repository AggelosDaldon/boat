# Boatism — experimental Forge 1.20.1 port (Stage 14)

Minecraft **1.20.1**, Forge **47.4.22**, Java **17**.

This is an independent, work-in-progress Forge adaptation of the original MIT-licensed [Boatism](https://github.com/JR1811/Boatism) by ShiroJR and collaborators. The original models, textures and sounds are preserved for now. See LICENSE and the original attribution in `mods.toml`.

## Current playable prototype

- Boat engine and upgrade items with recipes and a creative tab.
- Right-click a boat with the **Boat Engine** item to attach a motor.
- Right-click the motorized boat with the **Fuel Bucket** to refuel; receive an empty bucket back in survival.
- **Sneak + right-click with an empty main hand** on the motorized boat to switch its ignition on/off.
- Enter the boat and drive through water; an enabled, fueled engine supplies extra forward thrust.
- Exhaust upgrade increases power slightly; canisters increase fuel capacity.
- Other component items can be installed but their original gameplay behavior is not yet ported.
- Experimental world-space motor rendering, propeller movement and network state sync; original audio assets are bundled.

## Known missing functionality

**This is NOT yet feature-equivalent to Boatism 0.1.1.** The original separate BoatEngineEntity, detailed power-level controls, overheat and engine damage mechanics, equipment inventory, original animations, specialized sound management and all multiplayer edge cases have not been ported or fully tested. Client and dedicated-server runtime behavior remains unverified.

## Verified build and download

The GitHub Actions **Forge 1.20.1 build** workflow successfully compiles with Gradle 8.8 and Java 17.

1. Open the [Actions tab](https://github.com/AggelosDaldon/boat/actions).
2. Select the latest successful **Forge 1.20.1 build** run.
3. Download the `boatism-forge-1.20.1` artifact ZIP, extract its JAR, and test it with Forge 47.4.22.

For manual builds, install Java 17 and Gradle 8.8, then run `gradle build` from the repository root. The output JAR is in `build/libs/`.

## Source reference

The `reference/fabric-decompiled/` folder is a non-compiled reference copy for continuing the port. Its Fabric code cannot be used directly in Forge.
