# Claude Code 규칙

## 커밋 규칙

### 자동 커밋
- 매 작업 완료 시 자동으로 커밋 및 push
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

### 파일 구조
```
workLog/
├── YYYY-MM-DD.md          # 일별 작업 로그
└── stats/
    └── YYYY-MM-DD.json    # 일별 토큰 통계 데이터
```

### 기록 내용
매 요청마다 아래 내용 기록:
1. **시각**: 요청 시각
2. **통계**: `npx ccusage session --json`으로 토큰/비용 정보
3. **요청 내용**: 사용자 요청 요약
4. **응답 요약**: 주요 응답 내용
5. **변화**: 파일 생성/수정/삭제 내역
6. **이슈**: 발생한 문제 및 해결 방법

### 세션 통계
- 총 요청 수
- 총 토큰/비용
- 평균/중위값 토큰/비용

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
