# Stage 13 — reproducible build diagnostics

This is **not** a verified successful Forge port. No `.jar` is included.

## Changes
- Added `build-windows.bat` and `build-linux.sh` to capture complete compiler output in `stage13-build.log`.
- Added `.gitignore` for Gradle output and local run directories.
- Kept source and original assets/license unchanged.

## Build prerequisites
- Java JDK 17
- Gradle 8.x installed on PATH
- Internet access for ForgeGradle and Minecraft dependencies

Run `build-windows.bat` (Windows) or `./build-linux.sh` (Linux/macOS). Send the log if compilation fails.

## Known outstanding issues
- Full original BoatEngineEntity is not ported.
- Rendering, sound behavior, and multiplayer have not been validated in Minecraft.
- Java/Forge API compatibility has not been confirmed by a real compilation.
- Build success alone is not evidence of feature parity.
