# RunnersHi iOS 공유 스펙

> Android와 iOS 간 공유되는 비즈니스 로직 및 Mock 데이터 명세

---

## 목차
1. [Models](#models)
2. [Mock Response Data](#mock-response-data)
3. [Business Logic](#business-logic)
4. [Splash Flow](#splash-flow)

---

## Models

### AppConfig
```
AppConfig {
    minVersion: String       // 최소 지원 버전
    latestVersion: String    // 최신 버전
    maintenanceMode: Boolean // 점검 모드 여부
    maintenanceMessage: String // 점검 메시지
}
```

### AuthToken
```
AuthToken {
    accessToken: String   // 액세스 토큰
    refreshToken: String  // 리프레시 토큰
    expiresIn: Int        // 만료 시간 (초)
}
```

### User
```
User {
    id: String                   // 사용자 ID
    name: String                 // 이름
    profileImageUrl: String?     // 프로필 이미지 URL (nullable)
    totalDistance: Double        // 총 달린 거리 (km)
    totalRuns: Int               // 총 달린 횟수
}
```

### RankingItem
```
RankingItem {
    rank: Int           // 순위
    user: User          // 사용자 정보
    score: Double       // 점수 (주간 거리)
    change: RankChange  // 순위 변동
}
```

### Enums

#### RankChange
| Value | Description |
|-------|-------------|
| `NONE` | 변동 없음 |
| `UP` | 순위 상승 |
| `DOWN` | 순위 하락 |

#### VersionCheckResult
| Value | Description |
|-------|-------------|
| `UpToDate` | 최신 버전 |
| `NeedsUpdate(minVersion, latestVersion)` | 업데이트 필요 |
| `Maintenance(message)` | 점검 중 |
| `Error(exception)` | 오류 발생 |

#### LoginCheckResult
| Value | Description |
|-------|-------------|
| `NotLoggedIn` | 로그인 안됨 (토큰 없음) |
| `LoggedIn` | 로그인됨 (토큰 유효) |
| `TokenRefreshFailed` | 토큰 갱신 실패 |

---

## Mock Response Data

### 1. App Config API
**Endpoint:** `GET /api/config`

**Response:**
```json
{
    "minVersion": "1.0.0",
    "latestVersion": "1.2.0",
    "maintenanceMode": false,
    "maintenanceMessage": ""
}
```

**Delay:** 500ms

---

### 2. Auth Token Refresh API
**Endpoint:** `POST /api/auth/refresh`

**Request:**
```json
{
    "refreshToken": "current_refresh_token"
}
```

**Response (Success):**
```json
{
    "accessToken": "new_access_token_1737471234567",
    "refreshToken": "new_refresh_token_1737471234567",
    "expiresIn": 3600
}
```

**Response (Failure - refreshToken이 "expired"인 경우):**
```json
{
    "error": "TokenExpiredException",
    "message": "Refresh token has expired"
}
```

**Delay:** 300ms

---

### 3. User API
**Endpoint:** `GET /api/users/{id}`

**Response:**
```json
{
    "id": "0",
    "name": "테스트유저",
    "profileImageUrl": null,
    "totalDistance": 45.2,
    "totalRuns": 12
}
```

---

### 4. Weekly Ranking API
**Endpoint:** `GET /api/ranking/weekly`

**Response:**
```json
{
    "rankings": [
        {
            "rank": 1,
            "user": {
                "id": "1",
                "name": "김러너",
                "profileImageUrl": null,
                "totalDistance": 156.5,
                "totalRuns": 42
            },
            "score": 156.5,
            "change": "NONE"
        },
        {
            "rank": 2,
            "user": {
                "id": "2",
                "name": "박조깅",
                "profileImageUrl": null,
                "totalDistance": 142.3,
                "totalRuns": 38
            },
            "score": 142.3,
            "change": "UP"
        },
        {
            "rank": 3,
            "user": {
                "id": "3",
                "name": "이마라톤",
                "profileImageUrl": null,
                "totalDistance": 128.7,
                "totalRuns": 35
            },
            "score": 128.7,
            "change": "DOWN"
        },
        {
            "rank": 4,
            "user": {
                "id": "4",
                "name": "최달리기",
                "profileImageUrl": null,
                "totalDistance": 115.2,
                "totalRuns": 30
            },
            "score": 115.2,
            "change": "UP"
        },
        {
            "rank": 5,
            "user": {
                "id": "5",
                "name": "정스프린트",
                "profileImageUrl": null,
                "totalDistance": 98.4,
                "totalRuns": 28
            },
            "score": 98.4,
            "change": "NONE"
        }
    ]
}
```

---

## Business Logic

### 1. Version Check Logic (버전 비교)

**Pseudocode:**
```
function isVersionLower(current: String, min: String) -> Boolean {
    currentParts = current.split(".").map { toInt or 0 }
    minParts = min.split(".").map { toInt or 0 }

    maxLength = max(currentParts.size, minParts.size)

    for i in 0 until maxLength {
        currentPart = currentParts[i] or 0
        minPart = minParts[i] or 0

        if (currentPart < minPart) return true   // 현재 버전이 낮음
        if (currentPart > minPart) return false  // 현재 버전이 높음
    }

    return false  // 버전이 같음
}
```

**Examples:**
| Current | Min | Result |
|---------|-----|--------|
| "1.0.0" | "1.0.0" | false (같음) |
| "1.0.0" | "1.0.1" | true (낮음) |
| "1.1.0" | "1.0.1" | false (높음) |
| "2.0" | "1.9.9" | false (높음) |
| "1.0" | "1.0.0" | false (같음) |

---

### 2. Check App Version UseCase

**Pseudocode:**
```
function checkAppVersion(currentVersion: String) -> VersionCheckResult {
    try {
        config = fetchAppConfig()

        if (config.maintenanceMode) {
            return Maintenance(config.maintenanceMessage)
        }

        if (isVersionLower(currentVersion, config.minVersion)) {
            return NeedsUpdate(config.minVersion, config.latestVersion)
        }

        return UpToDate

    } catch (error) {
        return Error(error)
    }
}
```

---

### 3. Check Login Status UseCase

**Pseudocode:**
```
function checkLoginStatus() -> LoginCheckResult {
    storedToken = getStoredToken()

    if (storedToken == null) {
        return NotLoggedIn
    }

    result = refreshToken(storedToken.refreshToken)

    if (result.isSuccess) {
        return LoggedIn
    } else {
        return TokenRefreshFailed
    }
}
```

---

## Splash Flow

### Flow Diagram

```
┌─────────────────┐
│   App Launch    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Splash Screen  │
│    (Loading)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Check App       │
│ Version         │
└────────┬────────┘
         │
    ┌────┴────┬────────────┬────────────┐
    │         │            │            │
    ▼         ▼            ▼            ▼
┌───────┐ ┌────────┐ ┌───────────┐ ┌───────┐
│UpTo   │ │Needs   │ │Maintenance│ │ Error │
│Date   │ │Update  │ │           │ │       │
└───┬───┘ └────┬───┘ └─────┬─────┘ └───┬───┘
    │          │           │           │
    │          ▼           ▼           ▼
    │     ┌────────┐  ┌─────────┐ ┌─────────┐
    │     │ Force  │  │ Error   │ │ Error   │
    │     │ Update │  │ Dialog  │ │ Dialog  │
    │     │ Dialog │  └─────────┘ └─────────┘
    │     └────────┘
    │
    ▼
┌─────────────────┐
│ Check Login     │
│ Status          │
└────────┬────────┘
         │
    ┌────┴────┬────────────┐
    │         │            │
    ▼         ▼            ▼
┌────────┐ ┌────────┐ ┌──────────┐
│Not     │ │Token   │ │ Logged   │
│LoggedIn│ │Refresh │ │ In       │
│        │ │Failed  │ │          │
└────┬───┘ └────┬───┘ └─────┬────┘
     │          │           │
     ▼          ▼           ▼
┌─────────────────┐   ┌─────────────┐
│   Login Screen  │   │ Home Screen │
└─────────────────┘   └─────────────┘
```

### State Machine

```
SplashUiState:
├── Loading           // 초기 로딩 상태
├── ForceUpdate       // 강제 업데이트 필요
│   ├── currentVersion: String
│   └── minVersion: String
├── NavigateToLogin   // 로그인 화면으로 이동
├── NavigateToHome    // 홈 화면으로 이동
└── Error             // 에러 발생
    └── message: String
```

### Implementation Steps

1. **앱 시작** → Loading 상태로 시작
2. **버전 체크** 실행
   - 점검 모드 → Error(점검 메시지) 표시
   - 버전 낮음 → ForceUpdate 다이얼로그 표시 (스토어 이동)
   - 버전 정상 → 다음 단계로
   - 오류 발생 → Error 표시
3. **로그인 상태 확인** 실행
   - 토큰 없음 → Login 화면으로
   - 토큰 갱신 실패 → Login 화면으로
   - 토큰 유효 → Home 화면으로

---

## Notes

- 모든 시간 단위는 **밀리초(ms)** 또는 **초(s)**로 명시
- Mock 데이터의 delay는 실제 네트워크 지연을 시뮬레이션
- 버전 비교는 semantic versioning (x.y.z) 형식 가정
- 토큰 갱신 시 실패하면 기존 토큰 삭제
