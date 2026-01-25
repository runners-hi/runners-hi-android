# Claude Code 규칙

## 작업 완료 규칙 (필수!)

### 모든 작업 완료 시 반드시 실행
**매 작업 완료 후 `/worklog` 스킬 자동 실행** (`.claude/skills/worklog.md`)

순서:
1. **workLog 기록** → 2. **커밋** → 3. **push**

> ⚠️ 이 단계를 절대 빠뜨리지 말 것!

---

## 커밋 규칙

### 커밋 메시지
- 한글로 작성

### 커밋 메시지 형식
```
타입: 설명

- 상세 내용 1
- 상세 내용 2

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
```

---

## Work Log 규칙

> `/worklog` 스킬 참조 (`.claude/skills/worklog.md`)

### 필수 기록
- **모든 작업 완료 시 자동으로 workLog 기록** (커밋 전에 기록)
- 작업 내용, 변경 파일, 토큰 통계 등 빠짐없이 기록

### 파일 구조
```
workLog/
├── YYYY-MM-DD.md          # 일별 작업 로그
└── stats/
    └── YYYY-MM-DD.json    # 일별 토큰 통계 데이터
```

### 토큰 통계 기록 방법
1. **작업 시작 전**: `npx ccusage session --json` 실행하여 현재 누적 토큰 확인
2. **작업 완료 후**: 다시 `npx ccusage session --json` 실행
3. **차이 계산**: (작업 후 토큰) - (작업 전 토큰) = 해당 작업의 토큰 사용량

### 기록 내용
매 요청마다 아래 내용 기록:
1. **시각**: 요청 시각
2. **통계 (필수)**:
   - 토큰: 해당 요청에서 사용한 총 토큰 수
   - 비용: 해당 요청의 비용 ($)
3. **요청 내용**: 사용자 요청 요약
4. **응답 요약**: 주요 응답 내용
5. **변화**: 파일 생성/수정/삭제 내역
6. **이슈**: 발생한 문제 및 해결 방법

### 요청별 통계 테이블 형식 (상세 필수!)
```markdown
### #N - 요청 제목
| 통계 | 값 |
|------|-----|
| 시각 | YYYY-MM-DD HH:MM |
| 입력 토큰 | X,XXX |
| 출력 토큰 | X,XXX |
| 캐시 생성 | X,XXX |
| 캐시 읽기 | X,XXX,XXX |
| 총 토큰 | X,XXX,XXX |
| 비용 | $X.XX |
```

> ⚠️ 빈약한 통계 금지! 모든 항목 기록 필수

### 세션 통계 (파일 상단)
- 총 요청 수
- 총 토큰/비용
- 평균/중위값 토큰/비용

### stats JSON 파일 업데이트
매 요청마다 `workLog/stats/YYYY-MM-DD.json`에 다음 정보 추가:
```json
{
  "requests": [
    {
      "id": N,
      "time": "YYYY-MM-DD HH:MM:SS",
      "title": "요청 제목",
      "tokens": {
        "input": X,
        "output": X,
        "cacheCreation": X,
        "cacheRead": X,
        "total": X
      },
      "cost": X.XX
    }
  ]
}
```

---

## 작업 요청서 규칙

작업 요청서 생성 시 `/task-request` 스킬 사용 (`.claude/skills/task-request.md`)

### 핵심 원칙
- **모듈별 관리**: `presentation/<모듈>/docs/` 에 저장
- **요청서 생성 시 대상 모듈 확인 필수**

### 파일 구조
```
presentation/<모듈>/docs/
├── task-requests/           # 일반 작업 요청서
└── design-requests/         # 디자인 작업 요청서

docs/
├── *_TEMPLATE.md            # 템플릿
└── shared/                  # 공통 문서
```

---

## PR 생성 규칙

### 타이틀 형식
`[타입] 설명`

타입 종류:
- `[Feat]` - 새로운 기능
- `[Fix]` - 버그 수정
- `[Docs]` - 문서 작업
- `[Refactor]` - 리팩토링
- `[Style]` - UI/스타일 변경
- `[Chore]` - 기타 작업
- `[Test]` - 테스트 관련

### Assignee
- 항상 `kangraemin` 지정

### PR 본문
`.github/PULL_REQUEST_TEMPLATE.md` 템플릿 형식 준수

---

## Figma 디자인 작업 규칙

Figma URL이 제공되면 `/figma` 스킬 사용 (`.claude/skills/figma.md`)

### 핵심 원칙
- **에셋이 있으면 반드시 다운받아서 사용** (Canvas로 직접 그리기 금지)
- 색상은 Figma에 명시된 코드 그대로 사용
