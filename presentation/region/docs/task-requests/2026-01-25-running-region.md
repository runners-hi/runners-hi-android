# 기능 작업 요청서: 러닝 지역 선택 화면

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | 기능 구현 |
| 우선순위 | 높음 |
| 관련 디자인 | [2026-01-25-running-region.md](../design-requests/2026-01-25-running-region.md) |

---

## 기능 요구사항

### 0. 초기 상태
- 화면 진입 시 검색 결과(시 데이터) **표시하지 않음**
- 검색 입력 필드만 표시
- 검색어 입력 전까지 빈 상태 유지

### 1. 지역 검색 API
- 검색어 입력 시 **검색 API 호출** (Mock)
- 검색어를 query parameter로 포함
- API 엔드포인트: `GET /api/regions?query={검색어}`

### 2. Debounce 처리
- 검색어 입력 시 **300ms debounce** 적용
- 사용자가 타이핑을 멈춘 후 300ms 후에 API 호출
- 연속 입력 시 이전 요청 취소

### 3. 지역 선택 API
- 검색 결과에서 지역 클릭 시 **지역 선택 API 호출** (Mock)
- API 엔드포인트: `POST /api/user/region`
- Request Body: `{ "regionId": "string", "regionName": "string" }`

### 4. 화면 이동
- 지역 선택 API 성공 후 **Main 화면으로 이동**
- 실패 시 에러 메시지 표시

---

## 데이터 모델

### Domain Layer
```kotlin
data class Region(
    val id: String,
    val name: String  // 예: "강릉시", "서울시"
)
```

### Presentation Layer (State)
```kotlin
data class RegionSelectionState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<RegionUiModel> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)

data class RegionUiModel(
    val id: String,
    val name: String
)
```

---

## API 스펙

### 검색 API (Mock)
```kotlin
// Request
GET /api/regions?query=강

// Response
{
  "regions": [
    { "id": "1", "name": "강릉시" },
    { "id": "2", "name": "강진군" }
  ]
}
```

### 지역 선택 API (Mock)
```kotlin
// Request
POST /api/user/region
{
  "regionId": "1",
  "regionName": "강릉시"
}

// Response
{
  "success": true
}
```

---

## Repository / UseCase

### Repository
```kotlin
interface RegionRepository {
    suspend fun searchRegions(query: String): Result<List<Region>>
    suspend fun selectRegion(regionId: String, regionName: String): Result<Unit>
}
```

### UseCase
```kotlin
class SearchRegionsUseCase(private val repository: RegionRepository) {
    suspend operator fun invoke(query: String): Result<List<Region>>
}

class SelectRegionUseCase(private val repository: RegionRepository) {
    suspend operator fun invoke(regionId: String, regionName: String): Result<Unit>
}
```

---

## ViewModel 이벤트/이펙트

### Events
```kotlin
sealed interface Event : UiEvent {
    data class SearchQueryChanged(val query: String) : Event
    data class RegionSelected(val region: RegionUiModel) : Event
    object BackClicked : Event
    object ErrorDismissed : Event
}
```

### Effects
```kotlin
sealed interface Effect : UiEffect {
    object NavigateToMain : Effect
    object NavigateBack : Effect
    data class ShowToast(val message: String) : Effect
}
```

---

## Debounce 구현

```kotlin
// ViewModel 내부
private val searchQueryFlow = MutableStateFlow("")

init {
    viewModelScope.launch {
        searchQueryFlow
            .debounce(300) // 300ms debounce
            .filter { it.isNotBlank() }
            .distinctUntilChanged()
            .collect { query ->
                searchRegions(query)
            }
    }
}

fun onSearchQueryChanged(query: String) {
    updateState { copy(searchQuery = query) }
    searchQueryFlow.value = query

    // 검색어가 비어있으면 결과 초기화
    if (query.isBlank()) {
        updateState { copy(searchResults = emptyList()) }
    }
}
```

---

## Mock 데이터

```kotlin
val mockRegions = listOf(
    Region("1", "강릉시"),
    Region("2", "거제시"),
    Region("3", "경산시"),
    Region("4", "경주시"),
    Region("5", "계룡시"),
    Region("6", "고양시"),
    Region("7", "공주시"),
    Region("8", "과천시"),
    Region("9", "광명시"),
    Region("10", "광주시"),
    Region("11", "구리시"),
    Region("12", "군포시"),
    Region("13", "김포시"),
    Region("14", "김해시"),
    Region("15", "나주시"),
    // ... 더 많은 시 데이터
)

// 검색 필터링
fun searchRegions(query: String): List<Region> {
    return mockRegions.filter { it.name.contains(query) }
}
```

---

## 화면 흐름

1. **화면 진입** → 검색 필드만 표시 (결과 없음)
2. **검색어 입력** → 300ms debounce 후 검색 API 호출
3. **검색 결과 표시** → 시 목록 리스트로 표시
4. **지역 클릭** → 지역 선택 API 호출 → 로딩 표시
5. **API 성공** → Main 화면으로 이동
6. **API 실패** → 에러 토스트 표시

---

## 구현 체크리스트

### Data Layer
- [ ] RegionRemoteDataSource 구현 (Mock)
- [ ] RegionRepositoryImpl 구현

### Domain Layer
- [ ] Region 모델 정의
- [ ] RegionRepository 인터페이스 정의
- [ ] SearchRegionsUseCase 구현
- [ ] SelectRegionUseCase 구현

### Presentation Layer
- [ ] RegionSelectionContract 구현
- [ ] RegionSelectionViewModel 구현
- [ ] RegionSelectionScreen 구현
- [ ] Debounce 300ms 적용
- [ ] 초기 상태 (결과 숨김) 처리
- [ ] 검색 결과 리스트 UI
- [ ] 지역 선택 후 Main 이동

### 테스트
- [ ] 초기 진입 시 결과 없음 확인
- [ ] Debounce 동작 확인
- [ ] 검색 API 호출 확인
- [ ] 지역 선택 API 호출 확인
- [ ] Main 화면 이동 확인
