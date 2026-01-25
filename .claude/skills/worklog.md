# /worklog - 작업 완료 자동화 스킬

**모든 작업 완료 시 자동으로 실행되어야 하는 스킬입니다.**

## 자동 실행 조건

다음 상황에서 이 스킬을 **반드시** 실행:
1. 사용자 요청에 대한 작업 완료 시
2. 코드 변경이 발생한 경우
3. 문서 변경이 발생한 경우
4. PR 생성/업데이트 후

## 워크플로우

### 1단계: Baseline 확인

**Baseline 파일**: `workLog/stats/baseline.json`

```bash
# baseline 파일 확인
cat workLog/stats/baseline.json 2>/dev/null || echo "baseline 없음"
```

baseline 파일이 없으면 이전 값을 0으로 간주.

### 2단계: 현재 통계 수집

```bash
npx ccusage session --json 2>/dev/null | head -50
```

현재 세션의 누적값 확인:
- `inputTokens`
- `outputTokens`
- `cacheCreationTokens`
- `cacheReadTokens`
- `totalCost`

### 3단계: 델타 계산 (핵심!)

**이번 작업 비용 = 현재 누적값 - baseline 값**

```
이번 작업 input = 현재 inputTokens - baseline inputTokens
이번 작업 output = 현재 outputTokens - baseline outputTokens
이번 작업 cacheCreation = 현재 cacheCreationTokens - baseline cacheCreationTokens
이번 작업 cacheRead = 현재 cacheReadTokens - baseline cacheReadTokens
이번 작업 cost = 현재 totalCost - baseline totalCost
```

### 4단계: workLog 파일 업데이트

**파일 경로**: `workLog/YYYY-MM-DD.md`

**작업별 통계 테이블 (델타값 기록!)**:
```markdown
### #N - 작업 제목
| 통계 | 값 |
|------|-----|
| 시각 | YYYY-MM-DD HH:MM |
| 입력 토큰 | X,XXX (델타) |
| 출력 토큰 | X,XXX (델타) |
| 캐시 생성 | X,XXX (델타) |
| 캐시 읽기 | X,XXX,XXX (델타) |
| 비용 | $X.XX (델타) |

- **요청 내용**: 사용자가 요청한 내용 요약
- **작업 내용**:
  - 수행한 작업 1
  - 수행한 작업 2
- **변화**:
  ```
  [NEW] 새로 생성된 파일
  [MODIFIED] 수정된 파일
  ```
- **브랜치**: 현재 브랜치명
```

### 5단계: Baseline 업데이트

**작업 완료 후 현재 값을 새 baseline으로 저장**:

`workLog/stats/baseline.json`:
```json
{
  "timestamp": "2026-01-25T11:00:00Z",
  "inputTokens": 27225,
  "outputTokens": 4406,
  "cacheCreationTokens": 3065995,
  "cacheReadTokens": 118533607,
  "totalCost": 78.68
}
```

### 6단계: 세션 통계 업데이트

파일 상단의 세션 통계는 **누적값**으로 기록:
```markdown
## 세션 통계 요약
| 항목 | 값 |
|------|-----|
| 총 요청 수 | N |
| 총 비용 (누적) | $XX.XX |
```

### 7단계: 커밋 및 푸시

```bash
git add workLog/
git commit -m "docs: workLog Entry #N 추가

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
git push
```

## 예시: 델타 계산

**Baseline (이전 작업 완료 시점)**:
```json
{
  "inputTokens": 20000,
  "outputTokens": 3000,
  "cacheCreationTokens": 2000000,
  "cacheReadTokens": 100000000,
  "totalCost": 70.00
}
```

**현재 (작업 완료 후)**:
```
inputTokens: 27225
outputTokens: 4406
cacheCreationTokens: 3065995
cacheReadTokens: 118533607
totalCost: 78.68
```

**델타 (이번 작업 비용)**:
```
입력 토큰: 27225 - 20000 = 7,225
출력 토큰: 4406 - 3000 = 1,406
캐시 생성: 3065995 - 2000000 = 1,065,995
캐시 읽기: 118533607 - 100000000 = 18,533,607
비용: 78.68 - 70.00 = $8.68
```

## 체크리스트

작업 완료 전 확인:
- [ ] baseline.json 확인
- [ ] ccusage로 현재 통계 수집
- [ ] 델타 계산 (현재 - baseline)
- [ ] workLog에 델타값 기록
- [ ] baseline.json 업데이트
- [ ] git commit 완료
- [ ] git push 완료

## 주의사항

- **절대 누적값을 개별 작업 비용으로 기록하지 말 것!**
- 반드시 (현재 - baseline) 델타값을 기록
- baseline 파일이 없으면 새로 생성
- 세션 시작 시 baseline이 없으면 현재 값을 baseline으로 저장
