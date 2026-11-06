# 행동 로그 이벤트표 (Android)

공통 규칙
- 모든 이벤트에 파라미터와 사용자 속성으로 `app_platform=android`, `build=release|staging|debug`가 붙는다. 코드는 `FirebaseAnalyticsTracker`.
- 수집은 release만 켠다. debug·staging은 꺼 두고, 디버그 메뉴의 분석 토글로만 켠다. 크래시 기록도 release만.
- user_id는 서버 회원 숫자 id만. 이메일·닉네임·좌표는 보내지 않는다.
- 앱 코드는 `AnalyticsHub`만 부른다. 위치는 `core/common/.../analytics/Analytics.kt`.

| 이름 | 언제 | 파라미터 | 코드 위치 |
|---|---|---|---|
| app_open | 첫 화면이 그려질 때 한 번 | is_first_launch, first_screen_ms, screen_name | AnalyticsHub.screen |
| screen_view | 화면 진입 | screen_name, prev_screen, dwell_ms(앞 화면 머문 ms) | RunnersHiNavHost, LauncherContentImpl, TermsAgreementViewModel |
| login_start | 로그인 시작 | method(kakao/google/apple/dev) | LauncherViewModel |
| login_success | 로그인 성공 | method, is_new_user, duration_ms | LauncherViewModel |
| login_fail | 로그인 실패 | method, reason, duration_ms | LauncherViewModel |
| login_click | 로그인 버튼 | button(login_kakao 등) | LauncherViewModel |
| mypage_click | 마이페이지 행 | button(region/terms/account/notices/notifications/feedback/avatar) | MyPageViewModel |
| terms_agree | 약관 동의 완료 | optional_marketing, optional_sns, optional_location | TermsAgreementViewModel |
| region_select | 지역 선택 | sido, sigungu | RegionSelectionViewModel |
| health_permission | 헬스 권한 결과 | granted | MainViewModel |
| run_sync | 러닝 기록 동기화 | count | SyncRunningRecordsUseCaseImpl |
| notice_open | 공지 열기 | notice_id | RunnersHiNavHost |
| profile_photo_change | 프로필 사진 변경 성공 | - | MyPageViewModel |
| push_toggle | 푸시 켜고 끔 | enabled | NotificationViewModel |
| logout / withdraw | 로그아웃 / 탈퇴 | - | AccountManagementViewModel |
| api_error | 네트워크 오류(한 곳) | path(숫자 id는 :id), status, kind(offline/timeout/server/client) | core:network ApiErrorLogInterceptor |

screen_name 값: splash, login, terms, terms_detail, region, home, ranking, records, record_detail, mission, notice_list, notice_detail, mypage, profile_edit, notification_settings, notifications, withdraw.

아직 없는 이벤트(화면이 없어서): ranking_tab, mission_open, marketing_toggle, nickname_change.

크래시 기록(Firebase Crashlytics)
- 사용자 id = 서버 회원 숫자 id, custom key = screen, app_platform, build, version.
- 이벤트는 breadcrumb 로그로 남고, 네트워크 IOException·HTTP 5xx는 non-fatal로 기록한다.
- release 빌드는 Crashlytics Gradle 플러그인이 mapping 파일을 자동 업로드한다.
