# RunnersHi Android e2e (Maestro)

## 실행
- `e2e/run.sh` : 디버그 앱을 빌드·설치하고 `e2e/maestro/flows` 전체를 돌린다.
- `e2e/run.sh e2e/maestro/flows/01_server_picker_dev.yaml` : 한 흐름만.
- `NO_BUILD=1`(설치 건너뜀), `NO_MOCKWIRE=1`(프록시 안 띄움), `UPSTREAM=<서버>`(프록시가 넘길 서버, 기본 개발).
- 오류 흐름(20번대)은 로컬 mockwire(`npx github:kangraemin/mockwire`, 포트 8899, 에뮬레이터 주소 `10.0.2.2:8899`)로 오류를 만든다. 호스팅 프록시는 오프라인이 502로 보여 이 용도엔 로컬을 쓴다.
- 앱 안 접근성 id: `server_picker`, `server_prod`, `server_dev`, `server_mock`, `server_custom`, `server_badge`, `error_view`, `retry_button`, `empty_view`, `dev_login_button`, `login_kakao` 등.

## 테스터: 개발 버전 설치와 서버 바꾸기 (컴퓨터 연결 없이)
- Firebase App Distribution 초대 메일(또는 링크)을 폰에서 열고 "러너스하이 개발"을 설치한다(앱 ID `com.runnersHi.dev`, 상용 앱과 따로 설치된다).
- 처음 열면 개발 서버가 골라져 있다. 화면 맨 위 작은 배지(DEV/PROD/MOCK/CUSTOM)를 누르면 서버 선택 화면이 열린다.
- 상용·개발·모의 서버(`runnershi-mock.oguriworks.com/s/<세션>`, 세션 이름 기본값은 기기 모델)·직접 입력 URL 중에서 고른다.
- 서버를 바꾸면 로그아웃하고 앱이 처음부터 다시 시작한다.
- 선택 화면 아래 "행동 로그 수집" 스위치는 Firebase DebugView 확인용이다. 평소엔 꺼져 있다.
- 상용(스토어) 앱에는 서버 선택이 없고 항상 상용 서버를 쓴다.
