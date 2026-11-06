#!/bin/bash
# RunnersHi Android e2e (Maestro). 사용: e2e/run.sh [flow파일 ...]  (없으면 flows/ 전부)
#   NO_BUILD=1      빌드·설치 건너뜀
#   NO_MOCKWIRE=1   mockwire 안 띄움(20번대 오류 흐름은 실패한다)
#   UPSTREAM=...    mockwire가 넘겨줄 서버(기본 개발 서버)
set -euo pipefail
cd "$(dirname "$0")/.."
export PATH="$HOME/.maestro/bin:$HOME/Library/Android/sdk/platform-tools:$PATH"
UPSTREAM="${UPSTREAM:-https://runnershi-dev-api.oguriworks.com}"

if [ -z "${NO_BUILD:-}" ]; then
  ./gradlew :app:installDebug -q
fi

# 헬스커넥트 권한(홈·기록 화면이 안내 화면에서 멈추지 않게)
for p in READ_STEPS READ_DISTANCE READ_EXERCISE READ_HEART_RATE READ_ACTIVE_CALORIES_BURNED READ_TOTAL_CALORIES_BURNED READ_EXERCISE_ROUTES; do
  adb shell pm grant com.runnersHi "android.permission.health.$p" >/dev/null 2>&1 || true
done
adb shell pm grant com.runnersHi android.permission.POST_NOTIFICATIONS >/dev/null 2>&1 || true

MW_PID=""
if [ -z "${NO_MOCKWIRE:-}" ]; then
  if ! curl -fs http://127.0.0.1:8899/__mockwire/health >/dev/null 2>&1; then
    npx --yes github:kangraemin/mockwire start --upstream "$UPSTREAM" --port 8899 >/tmp/mockwire.log 2>&1 &
    MW_PID=$!
    for _ in $(seq 1 30); do curl -fs http://127.0.0.1:8899/__mockwire/health >/dev/null 2>&1 && break; sleep 1; done
  fi
fi
trap '[ -n "$MW_PID" ] && kill "$MW_PID" 2>/dev/null || true' EXIT

mkdir -p e2e/results
if [ $# -gt 0 ]; then TARGETS=("$@"); else TARGETS=(e2e/maestro/flows); fi
maestro test --format junit --output e2e/results/report.xml "${TARGETS[@]}"
