#!/bin/bash
# staging APK를 Firebase App Distribution에 올리고 테스터에게 배포한다.
# 사용: scripts/upload_staging.sh [--build]   (--build: assembleStaging 먼저)
# 인증: gcloud 로그인 토큰(gcloud auth print-access-token). 비밀·google-services.json은 저장소에 두지 않는다.
# 설정(선택, ~/.claude/.env 또는 환경변수): RH_TESTERS="a@x.com,b@y.com"  RH_RELEASE_NOTES="..."
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f "$HOME/.claude/.env" ] && { set -a; . "$HOME/.claude/.env"; set +a; }

PKG="com.runnersHi.dev"
TESTERS="${RH_TESTERS:-remin1994@gmail.com,handnew04@gmail.com}"
NOTES="${RH_RELEASE_NOTES:-러너스하이 개발 빌드 $(git rev-parse --short HEAD)}"
[ "${1:-}" = "--build" ] && ./gradlew :app:assembleStaging -q
APK=app/build/outputs/apk/staging/app-staging.apk
[ -f "$APK" ] || { echo "APK 없음: $APK (--build 로 먼저 빌드)"; exit 1; }

read -r PROJECT_NUM APP_ID < <(python3 - "$PKG" <<'P'
import json,sys
d=json.load(open('app/google-services.json'))
for c in d['client']:
    if c['client_info']['android_client_info']['package_name']==sys.argv[1]:
        print(d['project_info']['project_number'], c['client_info']['mobilesdk_app_id'])
P
)
TOKEN=$(gcloud auth print-access-token)
H=(-H "Authorization: Bearer $TOKEN" -H "x-goog-user-project: $PROJECT_NUM")
API=https://firebaseappdistribution.googleapis.com

OP=$(curl -fsS "${H[@]}" -X POST -H "X-Goog-Upload-Protocol: raw" -H "X-Goog-Upload-File-Name: app-staging.apk" \
  -H "Content-Type: application/vnd.android.package-archive" --data-binary @"$APK" \
  "$API/upload/v1/projects/$PROJECT_NUM/apps/$APP_ID/releases:upload" | python3 -c 'import json,sys;print(json.load(sys.stdin)["name"])')
echo "업로드 작업: $OP"
for _ in $(seq 1 60); do
  R=$(curl -fsS "${H[@]}" "$API/v1/$OP")
  if python3 -c 'import json,sys;sys.exit(0 if json.loads(sys.argv[1]).get("done") else 1)' "$R"; then break; fi
  sleep 5
done
REL=$(python3 -c 'import json,sys;print(json.loads(sys.argv[1])["response"]["release"]["name"])' "$R")
echo "릴리스: $REL"
curl -fsS "${H[@]}" -X PATCH -H "Content-Type: application/json" \
  -d "{\"releaseNotes\":{\"text\":$(python3 -c 'import json,sys;print(json.dumps(sys.argv[1]))' "$NOTES")}}" \
  "$API/v1/$REL?updateMask=release_notes.text" >/dev/null
EMAILS=$(python3 -c 'import json,sys;print(json.dumps(sys.argv[1].split(",")))' "$TESTERS")
curl -fsS "${H[@]}" -X POST -H "Content-Type: application/json" -d "{\"testerEmails\":$EMAILS}" "$API/v1/$REL:distribute" >/dev/null
echo "배포 완료: $TESTERS"
