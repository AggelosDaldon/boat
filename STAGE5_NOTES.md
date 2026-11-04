# Stage 5 (experimental, unbuilt)

Target: Minecraft 1.20.1, Forge 47.4.22, Java 17.

Changes: integrate registered engine sounds on installation, refueling, upgrades, startup, shutdown, low fuel, and periodic operation. These are provisional event-driven behaviors and not a port of the original engine entity or animations.

Build status: not compiled; no Gradle executable or wrapper in this environment/project. To build on a machine with Gradle and JDK 17: `gradle build`.

Known gaps: original BoatEngineEntity and renderer, networking, inventory, exact controls, client synchronization, sound behavior matching, and gameplay parity. The original decompiled reference is preserved in reference/. MIT attribution is retained.
