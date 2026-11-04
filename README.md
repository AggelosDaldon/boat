# Boatism Forge 1.20.1 — Stage 4 (experimental)

Target: Forge 47.4.22, Java 17, Minecraft 1.20.1.

## Changes from stage 3
- Added event-driven installation flags for four upgrade components (exhaust, canister, strapped canister, plates).
- Added speed cap to prevent indefinite acceleration, with a small exhaust speed modifier.
- Fuel bucket now returns an empty vanilla bucket in survival.
- Added action-bar feedback for engine installation, refueling and upgrades.

## Important limitations
This is an **experimental approximation**, NOT a faithful port of Boatism 0.1.1.
Upgrade flags other than exhaust have no gameplay effect yet. The original BoatEngineEntity, visuals,
animations, audio playback, inventory, controls, network sync, and exact original behavior remain unported.
The decompiled Fabric code is kept under `reference/` only. Original assets and license are retained.
The project has NOT been compiled or tested; a working mod jar is NOT available.

## Build
Use JDK 17 and a network-enabled ForgeGradle environment:
`gradle build`
The resulting jar, if compilation succeeds, will be under `build/libs/`.
