# 작업 요청서 - Splash 화면

## 작업 정보
- **작업 유형**: 기능 구현
- **우선순위**: 높음
- **예상 복잡도**: 보통

## 작업 설명
앱 진입 시 표시되는 Splash 화면 구현. Init API 호출 후 버전 체크 및 로그인 상태에 따른 화면 분기 처리.

## 요구사항

### 1. Splash 화면 UI
- [x] Splash Activity 생성
- [x] Splash 화면 Composable 구현
- [x] **Figma 디자인 구현 완료**
  - 배경색: #17191C (BlueGray/90)
  - 로고: Figma 에셋 → Vector Drawable
  - Progress Bar: 둥근 모서리, 애니메이션
- [x] 로딩 인디케이터 표시 (Progress Bar + Loading 텍스트)

### 2. Init API 호출
- [x] Mock Base URL 설정
- [x] Mock Response 구현 (MockAppConfigDataSource)
- [x] Init API 호출 로직 구현 (CheckAppVersionUseCase)

**Mock API 스펙:**
```json
// Request
GET /api/v1/init

// Response
{
  "minVersion": "1.0.0",
  "latestVersion": "1.2.0",
  "maintenanceMode": false,
  "maintenanceMessage": ""
}
```

### 3. 버전 체크 및 강제 업데이트
- [x] 현재 앱 버전과 minVersion 비교
- [x] 버전이 낮으면 강제 업데이트 팝업 표시 (ForceUpdateDialog)
- [x] 팝업에서 "업데이트" 클릭 시 Play Store로 이동
- [x] 팝업 dismiss 불가 (강제 업데이트)

**강제 업데이트 팝업:**
```
┌─────────────────────────────┐
│      업데이트 필요           │
│                             │
│  새로운 버전이 출시되었습니다.  │
│  앱을 업데이트해주세요.        │
│                             │
│      [ 업데이트 하기 ]        │
└─────────────────────────────┘
```

### 4. 로그인 상태 확인 및 분기
- [x] 로컬에 저장된 토큰 확인 (CheckLoginStatusUseCase)
- [x] 토큰 없음 → 로그인 화면으로 이동
- [x] 토큰 있음 → 토큰 갱신 시도

### 5. 토큰 갱신 (로그인된 경우)
- [x] refreshToken으로 accessToken 갱신 API 호출 (Mock)
- [x] 갱신 성공 → 홈 화면으로 이동
- [x] 갱신 실패 (토큰 만료) → 로그인 화면으로 이동

**Mock Token Refresh API 스펙:**
```json
// Request
POST /api/v1/auth/refresh
{
  "refreshToken": "xxx"
}

// Response (성공)
{
  "accessToken": "new_access_token",
  "refreshToken": "new_refresh_token",
  "expiresIn": 3600
}

// Response (실패 - 401)
{
  "error": "TOKEN_EXPIRED",
  "message": "Refresh token has expired"
}
```

## 화면 플로우
```
┌──────────────┐
│   Splash     │
│   화면 진입    │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Init API    │
│    호출      │
└──────┬───────┘
       │
       ▼
┌──────────────┐    minVersion 보다 낮음    ┌──────────────┐
│   버전 체크   │ ────────────────────────▶ │  강제 업데이트  │
└──────┬───────┘                           │    팝업      │
       │ 버전 OK                            └──────┬───────┘
       ▼                                          │
┌──────────────┐                                  ▼
│  토큰 확인    │                           ┌──────────────┐
└──────┬───────┘                           │  Play Store  │
       │                                    └──────────────┘
       ├─── 토큰 없음 ───▶ 로그인 화면
       │
       ▼ 토큰 있음
┌──────────────┐
│ 토큰 갱신 API │
└──────┬───────┘
       │
       ├─── 성공 ───▶ 홈 화면
       │
       └─── 실패 ───▶ 로그인 화면
```

## 참고 파일
- `presentation/ui/splash/` (신규 생성)
- `data/api/InitApi.kt` (신규 생성)
- `data/api/AuthApi.kt` (신규 생성)
- `data/repository/AuthRepository.kt`
- `data/local/TokenManager.kt` (SharedPreferences/DataStore)

## 완료 조건
- [x] Splash 화면 정상 표시
- [x] Init API Mock 호출 성공
- [x] 버전 낮을 시 강제 업데이트 팝업 표시 및 Play Store 이동
- [x] 로그인 안 된 경우 로그인 화면으로 이동
- [x] 로그인 된 경우 토큰 갱신 후 홈 화면으로 이동
- [x] 토큰 갱신 실패 시 로그인 화면으로 이동

## 참고사항
- 현재 API가 없으므로 Mock 구현 필요
- Mock 서버: `https://mock.runnershe.com` 또는 로컬 Mock
- Clean Architecture 패턴 준수
- Hilt/Koin 등 DI 사용 시 Mock/Real 전환 가능하도록 구현
