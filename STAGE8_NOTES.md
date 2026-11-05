# Stage 8 — experimental world renderer

Adds a client-side render pass that draws the existing model using the original engine texture and rotates its propeller.

**Critical limitation:** Forge/vanilla does not automatically synchronize `Entity#getPersistentData()` from the server to clients. Therefore installed engines will generally **not be visible** in actual multiplayer or normal client play yet. A proper tracked entity, synced entity data, or networking packet is required. This is NOT a completed boat engine renderer or faithful port.

No verified Gradle build or gameplay test. The model placement is provisional and needs visual adjustment. Original Fabric decompiled sources, assets, MIT license and attribution remain included.
