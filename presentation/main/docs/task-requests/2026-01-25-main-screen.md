# 기능 작업 요청서: 메인 화면 (홈)

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | 기능 구현 |
| 우선순위 | 높음 |
| 관련 디자인 | [2026-01-25-main-screen.md](../design-requests/2026-01-25-main-screen.md) |

---

## 기능 요구사항

### 1. Init API 호출
- 화면 진입 시 **init API 호출** (Mock)
- API 엔드포인트: `GET /api/home/init`
- **Header에 userToken 포함**
  ```
  Authorization: Bearer {userToken}
  ```

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
// 홈 화면 전체 데이터
data class HomeData(
    val tier: TierInfo,
    val todaysRun: TodaysRun,
    val thisWeek: ThisWeekData,
    val missionEvent: MissionEventData
)

// 티어 정보
data class TierInfo(
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

1. **화면 진입** → init API 호출 → 로딩 상태 표시
2. **API 성공** → 데이터 표시
3. **API 실패** → 에러 메시지 표시, 재시도 버튼

---

## 구현 체크리스트

### Data Layer
- [ ] HomeRemoteDataSource 구현 (Mock)
- [ ] HomeRepositoryImpl 구현

### Domain Layer
- [ ] HomeData 모델 정의 (Tier, TodaysRun, ThisWeek, MissionEvent)
- [ ] HomeRepository 인터페이스 정의
- [ ] GetHomeDataUseCase 구현

### Presentation Layer
- [ ] MainContract 구현 (State, Event, Effect)
- [ ] MainViewModel 구현
- [ ] MainScreen 구현

### UI 컴포넌트
- [ ] TitleBar (로고, 알림)
- [ ] TierCard (티어 정보, Progress Bar)
- [ ] TodaysRunCard (Distance, Pace, Time)
- [ ] ThisWeekCard (총 거리, 요일 인디케이터)
- [ ] MissionEventSection (배너, 미션 그리드)
- [ ] BottomNavigationBar (5개 탭)

### 상태 처리
- [ ] 로딩 상태 UI
- [ ] 에러 상태 UI
- [ ] 빈 데이터 상태 UI

### 테스트
- [ ] init API 호출 확인
- [ ] userToken 헤더 포함 확인
- [ ] Today's Run 데이터 표시 확인
- [ ] This Week 요일별 하이라이트 확인
- [ ] 미션 이벤트 완료/미완료 상태 확인
- [ ] 바텀 네비게이션 동작 확인
