# Stage 9 — experimental networking

Added SimpleChannel S2C state packet, initial state on StartTracking, and change-detected sync every five server ticks. Renderer reads client-side synchronized state.

**Unverified**: no Gradle build or Minecraft runtime test performed. Not a complete original BoatEngineEntity port. Packet delivery and renderer require testing. Entity-id cache clears on dimension/client-level change, but entity removal cleanup is pending.

Original MIT license and attribution retained.
