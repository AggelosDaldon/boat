# Stage 11 — networking hardening (unbuilt)
- Register engine state packet as PLAY_TO_CLIENT using Forge SimpleChannel messageBuilder.
- Dispatch client-only receiver through DistExecutor, avoiding eager client initialization on dedicated servers.
- Replace entity-ID-only snapshot hash cache with periodic tracking broadcasts, avoiding cross-world stale hashes.
- Ignore packets for entity IDs not currently representing boats and copy incoming NBT.
- No ForgeGradle compilation or Minecraft runtime validation has been performed.
- Original BoatEngineEntity port and exact Fabric behavior remain incomplete.
