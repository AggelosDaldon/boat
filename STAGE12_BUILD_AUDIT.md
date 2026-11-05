# Stage 12 — Build readiness audit

Target: Minecraft 1.20.1 / Forge 47.4.22 / Java 17.

## Verified from project files
- Forge Java source files: 9
- Asset files: 29
- build.gradle pins Forge 1.20.1-47.4.22.
- LICENSE exists: True.
- Original decompiled Fabric code is stored in `reference/`, outside the compiled source set.

## Build outcome
NOT BUILT. Java is available in the execution environment, but Gradle and a Gradle wrapper are not available. No Forge .jar is claimed.

## Open technical risks
- Forge API compatibility of rendering/networking/event handlers has not been compile-tested.
- The original Fabric BoatEngineEntity has not been fully ported.
- Renderer behavior and network sync have not been tested in single-player or multiplayer.
- Existing implementation is experimental and not feature-equivalent to Boatism 0.1.1.

## Reproduce locally
1. Install Java 17 and Gradle 8.x (or generate an official Gradle wrapper).
2. Run `bash build-local.sh` in the project root.
3. Investigate compiler errors, then test client and dedicated server.
4. A successful jar build alone does not prove feature parity.
