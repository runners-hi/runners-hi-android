# 기능 작업 요청서: 메인 화면 (홈)

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | 기능 구현 |
| 우선순위 | 높음 |
| 관련 디자인 | [2026-01-25-main-screen.md](../design-requests/2026-01-25-main-screen.md) |
| Tier Bottom Sheet | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-2045 |
| Tier Guide | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-3496 |
| Empty State | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-1662 |

---

## 모듈 구조 변경

### 삭제할 모듈
- `presentation:home` - 기존 홈 화면 모듈 삭제 (새 메인 화면으로 대체)

### 신규 모듈
- `domain:home:api` - 홈 도메인 인터페이스
- `domain:home:impl` - 홈 도메인 구현체
- `data:home:api` - 홈 데이터 인터페이스
- `data:home:impl` - 홈 데이터 구현체 (Mock)

### 수정할 모듈
- `presentation:main` - 메인 화면 구현
- `presentation:common` - BottomNavigationBar 공통 컴포넌트 추가

---

## 기능 요구사항

### 1. Init API 호출
- 화면 진입 시 **init API 호출** (Mock)
- API 엔드포인트: `GET /api/home/init`
- **Header에 userToken 포함**
  ```
  Authorization: Bearer {userToken}
  ```

### userToken 저장 위치
- **저장소**: 암호화된 로컬 스토리지 (EncryptedSharedPreferences)
- **저장 시점**: 로그인 시 저장 (아직 미구현, 추후 로그인 기능에서 구현 예정)
- **Mock 구현**: 현재는 하드코딩된 Mock 토큰 사용

### 2. 응답 데이터 구조
init API 응답에 모든 데이터가 포함됨:
- **Tier 정보**: 티어명, 레벨, 진행률
- **Today's Run 데이터**: 거리, 페이스, 시간
- **This Week 데이터**: 총 거리, 각 요일별 러닝 기록 (뛴 날/안 뛴 날 구분)
- **Mission Event 데이터**: 이벤트 배너, 미션 목록

### 3. This Week 요일 표시
- **뛴 날**: 하이라이트 (#255860 배경, #00EEFF 텍스트)
- **안 뛴 날**: 회색 (#ABB1BA 20% 배경, #8F97A3 텍스트)
- 각 요일에 러닝 거리 표시 (뛴 날만)

### 4. 미션 이벤트 상태
- **완료 미션**: Gold 테두리 (#FFCB2F), 배경 rgba(255,203,47,0.2)
- **미완료 미션**: 회색 테두리 (#454B54), 배경 #2E3238

---

## 데이터 모델

### Domain Layer

```kotlin
// 티어 종류 (5단계)
enum class Tier {
    BRONZE,    // 브론즈
    SILVER,    // 실버
    GOLD,      // 골드
    PLATINUM,  // 플래티넘
    DIAMOND    // 다이아몬드
}

// 홈 화면 전체 데이터
data class HomeData(
    val tier: TierInfo,
    val todaysRun: TodaysRun,
    val thisWeek: ThisWeekData,
    val missionEvent: MissionEventData
)

// 티어 정보
data class TierInfo(
    val tier: Tier,            // GOLD
    val tierName: String,      // "Gold Runner"
    val level: Int,            // 31
    val progressPercent: Int   // 10 (0-100)
)

// 오늘의 러닝
data class TodaysRun(
    val distanceKm: Double,    // 7.4
    val paceMinutes: Int,      // 8
    val paceSeconds: Int,      // 0
    val timeMinutes: Int,      // 40
    val timeSeconds: Int       // 0
)

// 이번 주 데이터
data class ThisWeekData(
    val totalDistanceKm: Double,           // 7.4
    val dailyRecords: List<DailyRecord>    // 7개 (월-일)
)

data class DailyRecord(
    val dayOfWeek: DayOfWeek,  // MONDAY, TUESDAY, ...
    val distanceKm: Double?,   // null이면 안 뛴 날
    val hasRun: Boolean        // 뛴 여부
)

// 미션 이벤트
data class MissionEventData(
    val eventBanner: EventBanner?,
    val missions: List<MissionItem>
)

data class EventBanner(
    val title: String,         // "추석 이벤트"
    val period: String         // "2025.10.01 - 2025.10.31"
)

data class MissionItem(
    val id: String,
    val name: String,          // "문라이트"
    val description: String,   // "10월 러닝 인증"
    val imageUrl: String,
    val isCompleted: Boolean
)
```

### Presentation Layer (State)

```kotlin
data class MainState(
    val isLoading: Boolean = true,
    val tierInfo: TierInfoUiModel? = null,
    val todaysRun: TodaysRunUiModel? = null,
    val thisWeek: ThisWeekUiModel? = null,
    val missionEvent: MissionEventUiModel? = null,
    val errorMessage: String? = null
) : UiState

data class TierInfoUiModel(
    val tier: Tier,              // GOLD - 티어별 아이콘/색상 결정용
    val tierName: String,
    val level: String,           // "Level 31"
    val progressPercent: Int,
    val progressText: String     // "10%"
)

data class TodaysRunUiModel(
    val distance: String,        // "7.4 km"
    val pace: String,            // "8'00''"
    val time: String             // "40:00"
)

data class ThisWeekUiModel(
    val totalDistance: String,   // "7.4 km"
    val days: List<DayUiModel>
)

data class DayUiModel(
    val label: String,           // "M", "T", "W", ...
    val distance: String?,       // "0.3", "10", null
    val hasRun: Boolean
)

data class MissionEventUiModel(
    val banner: EventBannerUiModel?,
    val missions: List<MissionItemUiModel>
)

data class EventBannerUiModel(
    val title: String,
    val period: String
)

data class MissionItemUiModel(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val isCompleted: Boolean
)
```

---

## API 스펙

### Init API (Mock)

```kotlin
// Request
GET /api/home/init
Headers:
  Authorization: Bearer {userToken}

// Response
{
  "tier": {
    "tier": "GOLD",           // BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    "tierName": "Gold Runner",
    "level": 31,
    "progressPercent": 10
  },
  "todaysRun": {
    "distanceKm": 7.4,
    "paceMinutes": 8,
    "paceSeconds": 0,
    "timeMinutes": 40,
    "timeSeconds": 0
  },
  "thisWeek": {
    "totalDistanceKm": 7.4,
    "dailyRecords": [
      { "dayOfWeek": "MONDAY", "distanceKm": 10.0, "hasRun": true },
      { "dayOfWeek": "TUESDAY", "distanceKm": null, "hasRun": false },
      { "dayOfWeek": "WEDNESDAY", "distanceKm": 0.3, "hasRun": true },
      { "dayOfWeek": "THURSDAY", "distanceKm": 0.3, "hasRun": true },
      { "dayOfWeek": "FRIDAY", "distanceKm": 0.3, "hasRun": true },
      { "dayOfWeek": "SATURDAY", "distanceKm": 0.3, "hasRun": true },
      { "dayOfWeek": "SUNDAY", "distanceKm": 0.3, "hasRun": true }
    ]
  },
  "missionEvent": {
    "eventBanner": {
      "title": "추석 이벤트",
      "period": "2025.10.01 - 2025.10.31"
    },
    "missions": [
      {
        "id": "1",
        "name": "문라이트",
        "description": "10월 러닝 인증",
        "imageUrl": "https://...",
        "isCompleted": true
      },
      {
        "id": "2",
        "name": "전력질주",
        "description": "페이스 5'00\"",
        "imageUrl": "https://...",
        "isCompleted": false
      },
      // ... more missions
    ]
  }
}
```

---

## Repository / UseCase

### Repository

```kotlin
interface HomeRepository {
    suspend fun getHomeData(userToken: String): Result<HomeData>
}
```

### UseCase

```kotlin
class GetHomeDataUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(userToken: String): Result<HomeData>
}
```

---

## ViewModel 이벤트/이펙트

### Events

```kotlin
sealed interface Event : UiEvent {
    object LoadData : Event                    // 초기 데이터 로드
    object RefreshData : Event                 // 새로고침
    object TierCardClicked : Event             // 티어 카드 클릭
    object TodaysRunClicked : Event            // Today's Run 클릭
    object MissionEventClicked : Event         // 미션 이벤트 더보기 클릭
    data class MissionItemClicked(val id: String) : Event  // 미션 아이템 클릭
    data class BottomNavClicked(val tab: BottomNavTab) : Event  // 바텀 탭 클릭
}

enum class BottomNavTab {
    HOME, RANKING, RECORD, MISSION, MY_PAGE
}
```

### Effects

```kotlin
sealed interface Effect : UiEffect {
    object NavigateToTierDetail : Effect
    object NavigateToTodaysRunDetail : Effect
    object NavigateToMissionEvent : Effect
    data class NavigateToMissionDetail(val id: String) : Effect
    data class NavigateToTab(val tab: BottomNavTab) : Effect
    data class ShowToast(val message: String) : Effect
}
```

---

## Mock 데이터

```kotlin
val mockHomeData = HomeData(
    tier = TierInfo(
        tier = Tier.GOLD,
        tierName = "Gold Runner",
        level = 31,
        progressPercent = 10
    ),
    todaysRun = TodaysRun(
        distanceKm = 7.4,
        paceMinutes = 8,
        paceSeconds = 0,
        timeMinutes = 40,
        timeSeconds = 0
    ),
    thisWeek = ThisWeekData(
        totalDistanceKm = 7.4,
        dailyRecords = listOf(
            DailyRecord(DayOfWeek.MONDAY, 10.0, true),
            DailyRecord(DayOfWeek.TUESDAY, null, false),
            DailyRecord(DayOfWeek.WEDNESDAY, 0.3, true),
            DailyRecord(DayOfWeek.THURSDAY, 0.3, true),
            DailyRecord(DayOfWeek.FRIDAY, 0.3, true),
            DailyRecord(DayOfWeek.SATURDAY, 0.3, true),
            DailyRecord(DayOfWeek.SUNDAY, 0.3, true)
        )
    ),
    missionEvent = MissionEventData(
        eventBanner = EventBanner(
            title = "추석 이벤트",
            period = "2025.10.01 - 2025.10.31"
        ),
        missions = listOf(
            MissionItem("1", "문라이트", "10월 러닝 인증", "...", true),
            MissionItem("2", "전력질주", "페이스 5'00\"", "...", false),
            MissionItem("3", "밤톨 러닝", "1km", "...", true),
            MissionItem("4", "한가위", "10월 5일-10월 8일 러닝 인증", "...", false),
            MissionItem("5", "송편 파워", "10월 6일 인증", "...", false),
            MissionItem("6", "갓러닝", "누적 거리 35km", "...", false)
        )
    )
)
```

---

## 화면 흐름

1. **화면 진입** → Health Connect 권한 요청 → init API 호출 → 로딩 상태 표시
2. **API 성공 + 데이터 있음** → 데이터 표시
3. **API 성공 + 데이터 없음** → Empty State 표시
4. **API 실패** → 에러 메시지 표시, 재시도 버튼

---

## Tier Info Bottom Sheet

### 트리거
- Tier Card의 **화살표 아이콘** 클릭 시 바텀시트 표시

### 바텀시트 상태 관리
```kotlin
// State
data class MainState(
    ...
    val showTierInfoSheet: Boolean = false,
    val tierGuideList: List<TierGuideItem> = emptyList()
)

data class TierGuideItem(
    val tier: Tier,
    val tierName: String,    // "Bronze Runner"
    val levelRange: String   // "Level 1 - Level 5"
)

// Event
sealed interface Event : UiEvent {
    ...
    object TierArrowClicked : Event      // 화살표 클릭 → 바텀시트 표시
    object TierSheetDismissed : Event    // 바텀시트 닫기
}
```

### 티어 가이드 데이터 (하드코딩)
```kotlin
val tierGuideList = listOf(
    TierGuideItem(Tier.BRONZE, "Bronze Runner", "Level 1 - Level 5"),
    TierGuideItem(Tier.SILVER, "Silver Runner", "Level 6 - Level 20"),
    TierGuideItem(Tier.GOLD, "Gold Runner", "Level 21 - Level 40"),
    TierGuideItem(Tier.PLATINUM, "Platinum Runner", "Level 41 - Level 70"),
    TierGuideItem(Tier.DIAMOND, "Diamond Runner", "Level 71 - Level 100")
)
```

### 티어 안내 문구 (하드코딩)
```kotlin
val tierInfoTexts = listOf(
    "러너 티어는 매년 1월 1일에 초기화됩니다.",
    "한 해 동안 쌓은 점수를 기준으로 12월 마지막 주에 최종 티어가 확정되며, 확정된 티어의 뱃지는 미션함으로 지급됩니다.",
    "연중에는 모든 러너가 브론즈 Level 1에서 시작하며, 점수를 달성할 때마다 레벨업과 상위 티어 승급이 가능합니다.",
    "티어는 하향되지 않고, 오직 승급만 할 수 있습니다.",
    "신규 가입자는 자동으로 브론즈 Level 1에서 시작하며, GPS 조작 등 부정 기록은 무효 처리됩니다.",
    "추후 등급별 선정 기준이나 혜택은 변경될 수 있습니다."
)
```

---

## Empty State (빈 데이터 상태)

### 적용 조건
```kotlin
val hasRunningData: Boolean
    get() = todaysRun != null && todaysRun.distanceKm > 0 ||
            thisWeek != null && thisWeek.totalDistanceKm > 0
```

### State 확장
```kotlin
data class MainState(
    ...
    val isEmptyState: Boolean = false  // 러닝 데이터 없음
)
```

### Empty State UI 모델
```kotlin
// Today's Run 빈 상태
data class TodaysRunEmptyUiModel(
    val illustrationRes: Int,           // R.drawable.img_empty_track
    val message: String = "러닝으로 하루를 채워보세요"
)

// This Week 빈 상태
data class ThisWeekEmptyUiModel(
    val totalDistance: String = "0 km",
    val message: String = "달리면 이곳에 기록이 쌓여요 🏃‍♂️"
)
```

### UI 분기 처리
```kotlin
@Composable
fun TodaysRunCard(
    data: TodaysRunUiModel?,
    isEmpty: Boolean
) {
    if (isEmpty || data == null) {
        TodaysRunEmptyContent()
    } else {
        TodaysRunDataContent(data)
    }
}
```

---

## Health Connect API 연동

### 개요
- Android Health Connect API를 사용하여 사용자의 운동 데이터(러닝 거리, 시간) 읽기
- 메인 화면 진입 시 권한 요청

### 필요 권한
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.health.READ_EXERCISE" />
<uses-permission android:name="android.permission.health.READ_DISTANCE" />
```

### 의존성 추가
```kotlin
// libs.versions.toml
[versions]
healthConnect = "1.1.0-alpha12"

[libraries]
health-connect = { group = "androidx.health.connect", name = "connect-client", version.ref = "healthConnect" }
```

### 권한 요청 시점
- **메인 화면 최초 진입 시** 권한 요청 다이얼로그 표시
- 권한 거부 시에도 앱 사용 가능 (서버 데이터만 표시)

### Data Layer
```kotlin
// data:health:api
interface HealthDataSource {
    suspend fun hasPermissions(): Boolean
    suspend fun requestPermissions(): Boolean
    suspend fun getExerciseSessions(startTime: Instant, endTime: Instant): List<ExerciseSession>
    suspend fun getTotalDistance(startTime: Instant, endTime: Instant): Double
    suspend fun getTotalDuration(startTime: Instant, endTime: Instant): Duration
}

data class ExerciseSession(
    val id: String,
    val startTime: Instant,
    val endTime: Instant,
    val distanceMeters: Double,
    val exerciseType: Int  // EXERCISE_TYPE_RUNNING
)
```

### Domain Layer
```kotlin
// domain:health:api
interface HealthRepository {
    suspend fun checkHealthPermissions(): Boolean
    suspend fun requestHealthPermissions(): Boolean
    suspend fun getTodayExerciseData(): Result<ExerciseData>
    suspend fun getWeekExerciseData(): Result<List<DailyExerciseData>>
}

data class ExerciseData(
    val distanceKm: Double,
    val durationMinutes: Int
)

data class DailyExerciseData(
    val date: LocalDate,
    val distanceKm: Double,
    val durationMinutes: Int
)
```

### UseCase
```kotlin
class SyncHealthDataUseCase(
    private val healthRepository: HealthRepository,
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Result<HomeData> {
        // 1. Health Connect에서 데이터 가져오기
        val todayExercise = healthRepository.getTodayExerciseData()
        val weekExercise = healthRepository.getWeekExerciseData()

        // 2. 서버 데이터와 병합하여 반환
        return homeRepository.getHomeDataWithHealthData(todayExercise, weekExercise)
    }
}
```

### ViewModel 흐름
```kotlin
init {
    // 1. 권한 확인
    checkHealthPermissions()
}

private fun checkHealthPermissions() {
    viewModelScope.launch {
        val hasPermission = healthRepository.checkHealthPermissions()
        if (!hasPermission) {
            sendEffect(Effect.RequestHealthPermissions)
        } else {
            loadData()
        }
    }
}

fun onHealthPermissionResult(granted: Boolean) {
    loadData()  // 권한 결과와 관계없이 데이터 로드
}

---

## 구현 체크리스트

### 모듈 구조 변경
- [x] `presentation:home` 모듈 삭제
- [x] `domain:home:api` 모듈 생성
- [x] `domain:home:impl` 모듈 생성
- [x] `data:home:api` 모듈 생성
- [x] `data:home:impl` 모듈 생성
- [x] `settings.gradle.kts` 업데이트
- [x] `domain:health:api` 모듈 생성 (Health Connect)
- [x] `domain:health:impl` 모듈 생성
- [x] `data:health:api` 모듈 생성
- [x] `data:health:impl` 모듈 생성

### Data Layer (data:home)
- [x] HomeRemoteDataSource 구현 (Mock)
- [x] HomeRepositoryImpl 구현

### Data Layer (data:health)
- [x] HealthDataSource 인터페이스 정의
- [x] HealthConnectDataSource 구현
- [x] MockHealthDataSource 구현

### Domain Layer (domain:home)
- [x] Tier enum 정의
- [x] HomeData 모델 정의 (TierInfo, TodaysRun, ThisWeekData, MissionEventData)
- [x] HomeRepository 인터페이스 정의
- [x] GetHomeDataUseCase 구현

### Domain Layer (domain:health)
- [x] ExerciseRecord, HealthPermissionStatus 모델 정의
- [x] HealthRepository 인터페이스 정의
- [x] CheckHealthPermissionUseCase 구현
- [x] GetTodayRunningDataUseCase 구현
- [x] GetWeekRunningDataUseCase 구현

### Presentation Layer (presentation:main)
- [x] MainContract 구현 (State, Event, Effect)
- [x] MainViewModel 구현
- [x] MainScreen 구현

### UI 컴포넌트 (presentation:main)
- [x] TitleBar (로고, 알림)
- [x] TierCard (5가지 티어 아이콘, 정보, Progress Bar)
- [x] TodaysRunCard (Distance, Pace, Time)
- [x] ThisWeekCard (총 거리, 요일 인디케이터)
- [x] MissionEventSection (배너, 미션 그리드)

### Tier Info Bottom Sheet
- [x] TierInfoBottomSheet 컴포넌트 구현
- [x] 딤(Dim) 배경 처리
- [x] 드래그 핸들 인디케이터
- [x] 현재 티어 정보 표시
- [x] 티어 목록 카드 (5가지 티어)
- [x] 티어 안내 문구 (bullet list)
- [x] State 확장 (showTierInfoSheet, tierGuideList)
- [x] Event 추가 (TierArrowClicked, TierSheetDismissed)

### Empty State (빈 데이터 상태)
- [x] TodaysRunEmptyContent 컴포넌트
- [x] ThisWeekEmptyContent 컴포넌트
- [x] State 확장 (isEmptyState)
- [x] 데이터 유무에 따른 UI 분기 처리
- [x] 빈 상태 일러스트 에셋

### Health Connect 연동
- [x] build.gradle.kts에 health-connect 의존성 추가
- [x] AndroidManifest.xml에 권한 선언
- [x] 권한 요청 다이얼로그 구현
- [x] HealthConnectDataSource 구현
- [x] 메인 화면 진입 시 권한 요청 로직

### 공통 컴포넌트 (presentation:common)
- [x] BottomNavigationBar (5개 탭) - 공통 컴포넌트로 분리

### 상태 처리
- [x] 로딩 상태 UI
- [x] 에러 상태 UI
- [x] 빈 데이터 상태 UI

### 테스트
- [x] init API 호출 확인
- [x] 저장된 토큰을 홈 데이터 요청에 전달 (실제 HTTP 헤더는 서버 연동 때 적용)
- [x] 5가지 티어 아이콘/색상 표시 확인
- [x] Today's Run 데이터 표시 확인
- [x] This Week 요일별 하이라이트 확인
- [x] 미션 이벤트 완료/미완료 상태 확인
- [x] 바텀 네비게이션 동작 확인
- [x] Tier Info 바텀시트 동작 확인
- [x] Empty State 표시 확인
- [x] Health Connect 권한 요청 확인
