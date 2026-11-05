#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if ! java -version 2>&1 | grep -q '17\\.'; then
  echo "WARNING: Java 17 is required; check java -version" >&2
fi
if [ -x ./gradlew ]; then
  ./gradlew --no-daemon clean build
elif command -v gradle >/dev/null 2>&1; then
  gradle --no-daemon clean build
else
  echo "ERROR: No Gradle wrapper or installed Gradle. Install Gradle 8.x or add the official Gradle wrapper." >&2
  exit 2
fi
echo "Build outputs:"
find build/libs -maxdepth 1 -name '*.jar' -type f -print
