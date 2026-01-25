# 기능 작업 요청서: 이용약관 동의 화면

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | 기능 구현 |
| 우선순위 | 높음 |
| 관련 디자인 | [2026-01-25-terms-agreement.md](../design-requests/2026-01-25-terms-agreement.md) |

---

## 기능 요구사항

### 1. 서버 API 연동
- 화면 진입 시 서버에 **init API** 호출
- 약관 목록을 서버에서 동적으로 받아옴

### 2. 서버 Response 스펙
```json
{
  "terms": [
    {
      "id": "string",
      "title": "약관 제목",
      "detailUrl": "웹뷰 URL",
      "required": true/false
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| id | String | 약관 고유 식별자 |
| title | String | 약관 제목 (화면에 표시) |
| detailUrl | String | 약관 상세 웹뷰 페이지 URL |
| required | Boolean | 필수 여부 (true: 필수, false: 선택) |

### 3. 동의 로직
- **필수 약관**: `required = true`인 항목
- **선택 약관**: `required = false`인 항목
- **다음 버튼 활성화 조건**: 모든 필수 약관에 동의해야 함
- 필수 약관 미동의 시 다음 버튼 비활성화 상태 유지

### 4. 전체 동의
- "약관에 모두 동의합니다" 체크 시 모든 항목 (필수 + 선택) 동의
- 개별 항목 해제 시 전체 동의 체크 해제
- 모든 개별 항목 체크 시 전체 동의 자동 체크

### 5. 약관 상세 보기
- 약관 항목 (또는 화살표) 클릭 시 해당 약관의 `detailUrl`로 웹뷰 페이지 이동
- 웹뷰에서 뒤로가기 시 약관 동의 화면으로 복귀

### 6. 화면 흐름
1. 화면 진입 → init API 호출 → 로딩 표시
2. API 성공 → 약관 목록 표시
3. 사용자 동의 선택
4. 필수 약관 모두 동의 → 다음 버튼 활성화
5. 다음 버튼 클릭 → 회원가입 완료 API 호출 → 메인 화면 이동

---

## 데이터 모델

### Domain Layer
```kotlin
data class TermsItem(
    val id: String,
    val title: String,
    val detailUrl: String,
    val required: Boolean
)
```

### Presentation Layer (State)
```kotlin
data class TermsAgreementState(
    val isLoading: Boolean = true,
    val terms: List<TermsItemUiModel> = emptyList(),
    val isAllAgreed: Boolean = false,
    val canProceed: Boolean = false,  // 필수 약관 모두 동의 여부
    val errorMessage: String? = null
)

data class TermsItemUiModel(
    val id: String,
    val title: String,
    val detailUrl: String,
    val required: Boolean,
    val isAgreed: Boolean = false
)
```

---

## API 연동

### Repository
```kotlin
interface TermsRepository {
    suspend fun getTermsList(): Result<List<TermsItem>>
    suspend fun agreeTerms(agreedTermIds: List<String>): Result<Unit>
}
```

### UseCase
```kotlin
class GetTermsListUseCase(private val repository: TermsRepository) {
    suspend operator fun invoke(): Result<List<TermsItem>>
}

class AgreeTermsUseCase(private val repository: TermsRepository) {
    suspend operator fun invoke(agreedTermIds: List<String>): Result<Unit>
}
```

---

## 구현 체크리스트

### Data Layer
- [ ] TermsRemoteDataSource 구현
- [ ] TermsRepositoryImpl 구현
- [ ] API Response DTO 정의

### Domain Layer
- [ ] TermsItem 모델 정의
- [ ] TermsRepository 인터페이스 정의
- [ ] GetTermsListUseCase 구현
- [ ] AgreeTermsUseCase 구현

### Presentation Layer
- [ ] TermsAgreementContract 수정 (서버 데이터 기반)
- [ ] TermsAgreementViewModel 수정
- [ ] TermsAgreementScreen 수정
- [ ] 로딩/에러 상태 처리
- [ ] 웹뷰 연동

### 테스트
- [ ] 필수 약관 미동의 시 버튼 비활성화 확인
- [ ] 전체 동의 로직 확인
- [ ] 약관 상세 웹뷰 이동 확인
- [ ] API 에러 처리 확인
