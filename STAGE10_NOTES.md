# Stage 10 — compile-readiness pass (unverified)

Target: Minecraft 1.20.1 / Forge 47.4.22 / Java 17.

## Changes
- Fixed four literal `\\n` sequences embedded between Java statements/imports in
  `Boatism.java` and `BoatEngineWorldRenderer.java`. These were definite Java syntax errors.
- Kept the Stage 9 networking and experimental world renderer intact.
- Preserved assets, original decompiled reference, MIT license, and credits.

## Important limitations
- **No successful Gradle build has been performed.** Gradle executable/wrapper
  and resolved ForgeGradle dependencies are not present in this environment.
- This is not a tested Forge mod jar.
- Stage 9 sync uses a process-wide entity-ID hash cache. IDs can be reused between
  worlds, so lifecycle handling and further synchronization tests remain necessary.
- No original BoatEngineEntity port, exact gameplay parity, GUI, or complete animation port.
- Run `gradle build` with Java 17 and internet access, then fix any compiler errors.
- Verify dedicated-server classloading and multiplayer behavior before release.
