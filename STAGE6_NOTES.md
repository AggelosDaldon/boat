# Stage 6 — incremental experimental gameplay fixes

- Canister upgrades now increase maximum fuel capacity: base 24000, canister 36000, strapped canister 48000 ticks.
- Refueling respects the current maximum and reports the correct capacity.
- Stop sound is emitted when the engine has zero fuel but was previously running.
- Low-fuel warning handles thresholds crossed without landing exactly on 200.

This is NOT the original Fabric BoatEngineEntity implementation. No 3D entity renderer, animations, network packets, or faithful engine physics have been ported.
No successful ForgeGradle build has been performed. The ZIP is source only.
