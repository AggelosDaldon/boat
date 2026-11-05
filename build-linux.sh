#!/usr/bin/env bash
set -o pipefail
cd "$(dirname "$0")"
java -version || exit 1
command -v gradle >/dev/null || { echo "Gradle 8.x is required"; exit 1; }
gradle --no-daemon clean build --stacktrace 2>&1 | tee stage13-build.log
