# 디자인 작업 요청서 - 로그인 화면

## 작업 정보
- **작업 유형**: 화면 구현
- **우선순위**: 높음
- **요청일**: 2026-01-25

## Figma 링크
https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI/%EC%A0%9C%EB%AA%A9-%EC%97%86%EC%9D%8C?node-id=1-2130

## 디자인 스펙

### 화면 구성
- **배경색**: #17191C (BlueGray/90)
- **화면 크기**: 360 x 800 (기준)

### 로고 영역 (화면 중앙 상단)
- **로고 아이콘**: 80x80dp, 화면 중앙
- **로고 텍스트**: "Runners HI", 136x40dp, 로고 아래

### 소셜 로그인 버튼

#### 카카오 로그인 버튼
- **위치**: 하단에서 약 104dp 위
- **크기**: 312 x 56dp
- **배경색**: #FEE500 (카카오 노란색)
- **모서리**: 71dp (pill shape)
- **아이콘**: 카카오 로고 24x24dp
- **텍스트**: "카카오로 시작하기"
  - 폰트: Pretendard SemiBold
  - 크기: 16sp
  - 색상: #0C0D0E (BlueGray/95)
- **아이콘-텍스트 간격**: 16dp

#### Apple 로그인 버튼
- **위치**: 카카오 버튼 아래 24dp
- **크기**: 312 x 56dp
- **배경색**: #2E3238 (BlueGray/80)
- **테두리**: 1dp, #454B54 (BlueGray/70)
- **모서리**: 71dp (pill shape)
- **아이콘**: Apple 로고
- **텍스트**: "Apple ID로 시작하기"
  - 폰트: Pretendard SemiBold
  - 크기: 16sp
  - 색상: #FFFFFF (BlueGray/White)

### 사용된 컬러
| 토큰 | 값 | 용도 |
|------|-----|------|
| BlueGray/90 | #17191C | 배경 |
| BlueGray/80 | #2E3238 | Apple 버튼 배경 |
| BlueGray/70 | #454B54 | Apple 버튼 테두리 |
| BlueGray/95 | #0C0D0E | 카카오 버튼 텍스트 |
| BlueGray/White | #FFFFFF | Apple 버튼 텍스트 |
| Primary | #00EEFF | 로고 색상 |
| 카카오 Yellow | #FEE500 | 카카오 버튼 배경 |

### 에셋 URL (7일간 유효)
- 로고 텍스트: https://www.figma.com/api/mcp/asset/4360d5e2-4168-480b-a676-3d5470cbf1bc
- 로고 아이콘: https://www.figma.com/api/mcp/asset/6570ed92-b0a2-46f4-9828-afba77dbaf25
- 카카오 로고: https://www.figma.com/api/mcp/asset/7e35e42b-1d2d-4d97-8305-a4b15f270da7
- Apple 로고: https://www.figma.com/api/mcp/asset/4d61abcf-dffb-434e-ba6d-104119e50176, https://www.figma.com/api/mcp/asset/a8ddb940-8470-4e2d-9309-fa61bfd7d18e

## 기능 요구사항

### 스플래시 → 로그인 화면 진입 흐름
- [ ] 스플래시 화면에서 로그인 정보(토큰) 확인
  - 토큰 있음 → 우리 서버 로그인 시도 → 성공 시 메인 화면으로
  - 토큰 없음 → 로그인 화면으로 이동

### 로그인 화면 동작
- [ ] 카카오 로그인 버튼 클릭 → 카카오 SDK 로그인 진행
- [ ] Apple 로그인 버튼 클릭 → Apple Sign In 진행
- [ ] 로그인 완료 후 분기:
  - 회원 가입 완료된 유저 → 메인 화면으로 이동
  - 회원 가입 필요한 유저 → 이용 약관 화면으로 이동
- [ ] 로그인 실패 시 에러 메시지 표시
- [ ] 로딩 중 버튼 비활성화 및 로딩 인디케이터 표시

## 화면 전환 애니메이션 스펙

### 스플래시 → 로그인 화면 전환
로그인 정보가 없어서 로그인 화면으로 이동할 때, 스플래시의 로고가 자연스럽게 이동하는 애니메이션 적용

#### 로고 아이콘 애니메이션
- **시작 위치**: 스플래시 화면 중앙 (화면 정중앙)
- **끝 위치**: 로그인 화면의 Figma 위치 (화면 중앙에서 약간 위, top 기준 약 50% - 99px)
- **애니메이션**: 위치 이동 (약 300ms, EaseInOut)

#### "Runners HI" 텍스트 애니메이션
- **시작 상태**: 투명 (alpha = 0)
- **끝 상태**: 완전 표시 (alpha = 1)
- **타이밍**: 로고 이동 완료 후 페이드인 (약 200ms)
- **위치**: 로고 아이콘 아래 (Figma 기준 top: 391px)

#### 소셜 로그인 버튼 애니메이션
- **시작 상태**: 화면 아래에서 위로 슬라이드 + 페이드인
- **타이밍**: 로고 텍스트 페이드인과 동시 또는 약간 후 (약 300ms)

## 구현 체크리스트

### 레이아웃
- [x] 배경색 적용 (#17191C)
- [x] 로고 아이콘 배치 (기존 ic_logo_runnershi.xml 활용)
- [x] 로고 텍스트 배치 ("Runners HI" - 텍스트로 구현)
- [x] 카카오 로그인 버튼 스타일 적용
- [x] Apple 로그인 버튼 스타일 적용

### 애니메이션
- [x] 스플래시 → 로그인 화면 전환 시 로고 위치 이동 애니메이션
- [x] "Runners HI" 텍스트 페이드인 애니메이션
- [x] 소셜 로그인 버튼 슬라이드업 + 페이드인 애니메이션

### 기능 연결
- [ ] 카카오 로그인 SDK 연동
- [ ] Apple Sign In 연동
- [x] 로그인 결과에 따른 화면 분기 (메인 / 이용약관)

## 참고사항
- 로고 아이콘은 splash에서 사용한 `ic_logo_runnershi.xml` 재사용 가능
- 시맨틱 컬러 시스템 활용 (Color.kt)
- MVI 패턴 적용 (LoginContract, LoginViewModel, LoginScreen)

---
