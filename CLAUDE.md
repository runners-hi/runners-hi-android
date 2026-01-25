# Claude Code 규칙

## 커밋 규칙

### 자동 커밋
- 매 작업 완료 시 순서: **workLog 기록 → 커밋 → push**
- 커밋 메시지는 한글로 작성

### 커밋 메시지 형식
```
타입: 설명

- 상세 내용 1
- 상세 내용 2

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
```

---

## Work Log 규칙

### 필수 기록
- **매 작업 완료 시 workLog에 기록** (커밋 전에 기록)
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

### 요청별 통계 테이블 형식
```markdown
### #N - 요청 제목
| 통계 | 값 |
|------|-----|
| 시각 | YYYY-MM-DD HH:MM |
| 토큰 | X,XXX |
| 비용 | $X.XX |
```

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

### 파일 구조
```
docs/
├── DESIGN_REQUEST_TEMPLATE.md    # 디자인 작업 템플릿
├── TASK_REQUEST_TEMPLATE.md      # 일반 작업 템플릿
├── design-requests/              # 디자인 작업 요청서
│   └── YYYY-MM-DD-작업명.md
└── task-requests/                # 일반 작업 요청서
    └── YYYY-MM-DD-작업명.md
```

### 디자인 작업 요청서 (Figma 링크 제공 시)
1. **작업 정보**: 작업 유형, 우선순위
2. **Figma 링크**: 사용자가 제공한 URL
3. **작업 설명**: Figma에서 추출한 디자인 정보
4. **기능 요구사항**: 필요한 동작/인터랙션
5. **참고사항**: 기존 컴포넌트 활용 등

**자동 처리:**
- Figma MCP로 디자인 컨텍스트 추출
- 스크린샷 생성 (필요 시)
- Compose 코드 생성

### 일반 작업 요청서
1. **작업 정보**: 작업 유형, 우선순위
2. **작업 설명**: 구현할 기능/수정 내용
3. **요구사항**: 기능적 요구사항 목록
4. **참고 파일**: 관련 코드 파일 경로
5. **완료 조건**: 작업 완료 기준

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

### 에셋 사용 원칙
- **Figma 에셋이 있으면 반드시 다운받아서 사용**
- 로고, 아이콘, 이미지 등을 Canvas나 코드로 직접 그리지 않음
- `get_design_context` 결과에서 에셋 URL 확인 → 다운로드 → drawable로 변환

### 에셋 처리 순서
1. `get_design_context` 호출
2. 결과에서 `const imgXxx = "https://..."` 에셋 URL 확인
3. 에셋 다운로드 (curl 등)
4. 파일 형식 확인 (SVG → Vector Drawable XML 변환)
5. `res/drawable/` 에 저장
6. 코드에서 `painterResource(R.drawable.xxx)` 사용

### 색상 사용
- Figma 디자인 컨텍스트에 명시된 색상 코드 그대로 사용
- 기존 테마 색상과 매핑 가능하면 테마 색상 사용

### 금지 사항
- Canvas로 로고/아이콘 직접 그리기 ❌
- Figma 에셋 URL이 있는데 무시하고 직접 구현 ❌
- 색상 코드 임의 변경 ❌
