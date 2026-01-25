# /task-request - 작업 요청서 생성 스킬

작업 요청서를 모듈별로 생성합니다.

## 사용법

```
/task-request <작업 설명>
```

예시:
```
/task-request 로그인 화면 구현
/task-request 약관 동의 API 연동
```

## 워크플로우

### 1단계: 모듈 확인 (필수)
사용자에게 대상 모듈 질문:

```
"이 작업은 어떤 모듈에 해당하나요?"

1. presentation/splash
2. presentation/login
3. presentation/terms
4. presentation/home
5. domain/<기능>
6. data/<기능>
7. 기타 (직접 입력)
```

### 2단계: 요청서 타입 확인
```
"어떤 타입의 요청서인가요?"

1. 일반 작업 요청서 (기능 구현, 버그 수정 등)
2. 디자인 작업 요청서 (Figma 링크 있음)
```

### 3단계: 디렉토리 생성
```bash
mkdir -p <모듈경로>/docs/task-requests
# 또는
mkdir -p <모듈경로>/docs/design-requests
```

### 4단계: 요청서 생성

**파일명**: `YYYY-MM-DD-<작업명>.md`

**일반 작업 요청서 템플릿**:
```markdown
# 작업 요청서 - <작업명>

## 작업 정보
- **작업 유형**: [기능 구현 / 버그 수정 / 리팩토링]
- **우선순위**: [높음 / 보통 / 낮음]
- **요청일**: YYYY-MM-DD

## 작업 설명
<작업 내용>

## 요구사항
- [ ] 요구사항 1
- [ ] 요구사항 2

## 참고 파일
- `경로/파일.kt`

## 완료 조건
- [ ] 조건 1
- [ ] 조건 2
```

**디자인 작업 요청서 템플릿**:
```markdown
# 디자인 작업 요청서

## 작업 정보
- **작업 유형**: [화면 구현 / 컴포넌트 구현]
- **우선순위**: [높음 / 보통 / 낮음]
- **요청일**: YYYY-MM-DD

## Figma 링크
<URL>

## 작업 설명
<!-- Figma MCP로 추출 -->

## 기능 요구사항
- [ ] 요구사항 1
- [ ] 요구사항 2

## 참고사항
```

### 5단계: 사용자에게 내용 확인
- 요구사항 추가/수정 여부 확인
- 완료 조건 확인

## 파일 위치 규칙

| 모듈 | 경로 |
|------|------|
| presentation/splash | `presentation/splash/docs/` |
| presentation/login | `presentation/login/docs/` |
| presentation/terms | `presentation/terms/docs/` |
| domain/auth | `domain/auth/api/docs/` |
| data/auth | `data/auth/api/docs/` |
