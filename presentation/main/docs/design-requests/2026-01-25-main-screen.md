# 디자인 작업 요청서: 메인 화면 (홈)

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | UI 구현 |
| 우선순위 | 높음 |
| Figma URL | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-1118 |
| 가이드 URL | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-3417 |
| 티어 이미지 URL | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-3541 |

---

## 레이아웃 가이드 (중요!)

### 상단 고정 영역
- **Android**: Status Bar + AppBar (Toolbar 포함)
- **iOS**: Status Bar + UINavigationBar
- **위치**: 상단에 고정 (position: fixed, top: 0)
- **동작**: 스크롤과 무관하게 항상 노출

### 타이틀 하단 고정 영역
- **높이**: 8dp (Fixed)
- **배경색**: #17191C (BlueGray90)
- **동작**: 스크롤과 무관하게 항상 고정

### z-index 우선순위
```
Content < Title Bar < Dim < Modal/Alert/BottomSheet(모달류)
```

### 하단 스크롤 여백
- **모든 스크롤 콘텐츠와 Bottom Navigation Bar 사이 간격**: 40dp (Fixed)

---

## 전체 레이아웃
- **배경색**: #17191C (BlueGray90)
- **좌우 패딩**: 20dp (카드), 24dp (타이틀) - **Fixed**
- **스크롤**: 세로 스크롤 가능

---

## 1. Title Bar (상단 헤더)
- **배경색**: #17191C (BlueGray90)
- **높이**: 56dp
- **좌측**: "Runners HI" 로고 (24dp 좌측 패딩) - **Fixed**
- **우측**: 알림 아이콘 (28x28dp, 우측 24dp 패딩) - **Fixed**
- **동작**: 상단 고정, 스크롤 시에도 항상 노출

---

## 2. Tier Card (등급 카드)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 24dp
- **크기**: width=320dp, height=140dp
- **위치**: top=122dp

### 내용
- **티어 아이콘**: 60x40dp - **Fixed**
- **티어명**: "{Tier} Runner" (Pretendard SemiBold 20sp, White)
- **레벨**: "Level {N}" (Pretendard Regular 14sp, #F1F2F4)
- **화살표**: 우측 상단 (24x24dp) - **Fixed**
- **Progress Bar**
  - 배경: #454B54, height=12dp, rounded
  - 진행색: 티어별로 다름 (아래 표 참조)
  - 텍스트: "Progress to next level" / "10%" (14sp) - **Fixed**

### 티어 종류 (5단계)
| 티어 | 티어명 | 아이콘 색상 | Progress Bar 색상 |
|------|--------|-------------|-------------------|
| Bronze | Bronze Runner | #FF6B35 (주황/빨강) | #FF6B35 |
| Silver | Silver Runner | #A8B4C4 (은회색) | #A8B4C4 |
| Gold | Gold Runner | #FFD700 (금색) | #F5FF66 |
| Platinum | Platinum Runner | #4ADE80 (녹색) | #4ADE80 |
| Diamond | Diamond Runner | #00EEFF (시안) | #00EEFF |

---

## 3. Today's Run Card (오늘의 러닝)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 24dp
- **크기**: width=320dp, height=171dp
- **위치**: top=277dp

### 헤더
- **텍스트**: "Today's Run" (Pretendard SemiBold 16sp, #C7CBD1)
- **화살표**: 우측 (20x20dp)

### 데이터 항목 레이아웃 (3개 가로 배치) - **중요!**
```
containerWidth = screenWidth - 좌우 패딩(40dp)
아이템 간 간격 = 4dp × 2 = 8dp (Fixed)
아이템 width = (containerWidth - 8dp) / 3 (Flexible)
아이템 내부 콘텐츠 = 수직, 수평 center 정렬
```

| 항목 | 아이콘 | 아이콘 배경 | 값 | 라벨 |
|------|--------|-------------|-----|------|
| Distance | track | #00EEFF (20% opacity) | 7.4 km | Distance |
| Pace | flame | #FF6363 (20% opacity) | 8'00'' | Pace |
| Time | timer | #F5FF66 (20% opacity) | 40:00 | Time |

- **좌우 패딩**: 20dp - **Fixed**
- **아이템 간 간격**: 4dp - **Fixed**
- **아이템 width**: **Flexible** (화면 너비에 따라 조절)
- **아이콘 배경**: 34x44dp, rounded pill
- **값 텍스트**: Pretendard SemiBold 20sp, White
- **라벨 텍스트**: Pretendard Regular 14sp, #8F97A3

---

## 4. This Week Card (이번 주)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 24dp
- **크기**: width=320dp, height=186dp
- **위치**: top=464dp

### 헤더
- **텍스트**: "This Week" (Pretendard SemiBold 16sp, #C7CBD1)

### 총 거리
- **텍스트**: "7.4 km" (Pretendard SemiBold 24sp, White, 중앙 정렬)

### 요일 인디케이터 (7개 가로 배치)
| 요일 | 상태 |
|------|------|
| M, W, T, F, S, S | 활성 (하이라이트) |
| T (화요일) | 비활성 (회색) |

#### 활성 상태 (뛴 날)
- **배경**: #255860, 30x40dp, rounded pill
- **요일 텍스트**: #00EEFF (Primary), 14sp
- **거리 텍스트**: #C7CBD1, 14sp (예: "0.3", "10")

#### 비활성 상태 (안 뛴 날)
- **배경**: #ABB1BA (20% opacity), 30x40dp, rounded pill
- **요일 텍스트**: #8F97A3, 14sp
- **거리 텍스트**: 없음

---

## 5. Mission Event Section (미션 이벤트)

### 헤더
- **텍스트**: "미션 이벤트" (Pretendard SemiBold 20sp, White)
- **화살표**: 우측 (24x24dp) - **Fixed**

### 이벤트 배너 - **중요!**
- **배경색**: #2E3238 (BlueGray80)
- **모서리**: 40dp
- **높이**: 40dp - **Fixed** (padding이 아닌 height 고정!)
- **너비**: 312dp
- **내용**: "추석 이벤트" (#8F97A3) + "2025.10.01 - 2025.10.31" (#C7CBD1)

### 미션 아이템 그리드 (3열) - **중요!**
```
containerWidth = screenWidth - 좌우 패딩(40dp)
아이템 간 간격 = 4dp × 2 = 8dp (Fixed)
아이템 width = (containerWidth - 8dp) / 3 (Flexible)
아이템 내부 콘텐츠 = 수직, 수평 center 정렬
```

- **좌우 패딩**: 20dp - **Fixed**
- **아이템 간 간격**: 4dp - **Fixed**
- **아이템 width**: **Flexible** (화면 너비에 따라 조절)
- **세로 간격**: ~8dp - **Fixed**

#### 미션 아이템 구조
- **아이콘 컨테이너**: 72x100dp, rounded pill
  - 미완료: 배경 #2E3238, 테두리 #454B54
  - 완료: 배경 rgba(255,203,47,0.2), 테두리 #FFCB2F (Gold)
- **아이콘 이미지**: 컨테이너 내부 중앙 - 서버에서 이미지 URL 전달
- **미션명**: Pretendard SemiBold 14sp, #F1F2F4
- **설명**: Pretendard Regular 13sp, #8F97A3

---

## 6. Bottom Navigation Bar
> **구현 위치**: `presentation:common` 모듈에 공통 컴포넌트로 분리

- **배경색**: #17191C (BlueGray90)
- **높이**: 89dp (인디케이터 포함)
- **상단 라인**: 1dp #454B54
- **동작**: 하단 고정, 스크롤과 무관하게 항상 노출

### 탭 (5개)
| 탭 | 아이콘 | 라벨 |
|----|--------|------|
| 홈 | ic_home | 홈 |
| 랭킹 | ic_rank | 랭킹 |
| 기록 | ic_record | 기록 |
| 미션 | ic_mission | 미션 |
| 마이페이지 | ic_user | 마이페이지 |

- **아이콘 크기**: 24x24dp - **Fixed**
- **라벨**: Pretendard Regular 12sp, #75808B (비활성)
- **인디케이터**: 하단 중앙, #8F97A3, 4dp height

---

## 7. Tier Info Bottom Sheet (티어 안내 바텀시트)
> **Figma URL**: https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-2045

### 트리거
- **Tier Card**의 **화살표** 클릭 시 바텀시트 표시

### 딤(Dim) 배경
- **배경색**: rgba(12, 13, 14, 0.6) → #0C0D0E99
- **동작**: 딤 영역 클릭 시 바텀시트 닫힘

### 바텀시트 컨테이너
- **배경색**: #2E3238 (BlueGray80)
- **상단 모서리**: 36dp (rounded-tl, rounded-tr)
- **높이**: 화면 하단에서 위로 확장

### 인디케이터 (드래그 핸들)
- **색상**: #8F97A3 (BlueGray40)
- **크기**: 66x4dp, rounded
- **위치**: 상단 중앙, top=12dp

### 현재 티어 정보 (상단)
- **티어 아이콘**: 60x40dp - rotate 180도
- **티어명**: "{Tier} Runner" (Pretendard SemiBold 24sp, White, 중앙 정렬)
- **레벨**: "Level {N}" (Pretendard Regular 14sp, #F1F2F4, 중앙 정렬)
- **Progress Bar**: 전체 화면과 동일한 스타일

### 티어 목록 (카드)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 40dp
- **크기**: width=320dp, height=342dp

### 티어 아이템 (5개)
| 순서 | 티어 | 레벨 범위 |
|------|------|-----------|
| 1 | Bronze Runner | Level 1 - Level 5 |
| 2 | Silver Runner | Level 6 - Level 20 |
| 3 | Gold Runner | Level 21 - Level 40 |
| 4 | Platinum Runner | Level 41 - Level 70 |
| 5 | Diamond Runner | Level 71 - Level 100 |

- **아이템 높이**: 62dp
- **아이템 구성**:
  - 티어 아이콘: 48x32dp (60x40의 80% 스케일)
  - 티어명: Pretendard SemiBold 16sp, White
  - 레벨 범위: Pretendard Regular 14sp, #8F97A3

### 티어 안내 문구
- **타이틀**: "티어 안내" (Pretendard SemiBold 20sp, White)
- **내용**: bullet list (Pretendard Regular 14sp, #F1F2F4)
  - 러너 티어는 매년 1월 1일에 초기화됩니다.
  - 한 해 동안 쌓은 점수를 기준으로 12월 마지막 주에 최종 티어가 확정되며, 확정된 티어의 뱃지는 미션함으로 지급됩니다.
  - 연중에는 모든 러너가 브론즈 Level 1에서 시작하며, 점수를 달성할 때마다 레벨업과 상위 티어 승급이 가능합니다.
  - 티어는 하향되지 않고, 오직 승급만 할 수 있습니다.
  - 신규 가입자는 자동으로 브론즈 Level 1에서 시작하며, GPS 조작 등 부정 기록은 무효 처리됩니다.
  - 추후 등급별 선정 기준이나 혜택은 변경될 수 있습니다.

---

## 8. Empty State (신규 유저/빈 데이터)
> **Figma URL**: https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-1662

### 적용 조건
- 러닝 데이터가 없는 신규 유저
- Today's Run, This Week 모두 데이터 0인 경우

### Today's Run Card (빈 상태)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 24dp
- **크기**: width=320dp, height=171dp
- **일러스트**: 러닝 트랙 아이콘 (87.8x60dp, 시안 컬러, 중앙)
- **안내 문구**: "러닝으로 하루를 채워보세요" (Pretendard Regular 14sp, White, 중앙)

### This Week Card (빈 상태)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 24dp
- **크기**: width=320dp, height=160dp
- **총 거리**: "0 km" (Pretendard SemiBold 24sp, White, 중앙)
- **하단 버튼/필드**:
  - 배경: #454B54, rounded pill, height=40dp, width=280dp
  - 텍스트: "달리면 이곳에 기록이 쌓여요 🏃‍♂️" (Pretendard Regular 14sp, White, 중앙)

### Mission Section (빈 상태)
- 이벤트 배너: 없음 (eventBanner = null)
- 미션 아이템: 신규 유저용 초기 미션 표시
  - "데뷔전" - 첫 러닝 등록
  - "집 앞 스타트" - 지역 인증

---

## 에셋 목록

| 에셋명 | 용도 | 크기 |
|--------|------|------|
| ic_logo_runnershi_txt | 상단 로고 | 95x28dp |
| ic_notification | 알림 아이콘 | 28x28dp |
| img_tier_bronze | 브론즈 티어 아이콘 | 60x40dp |
| img_tier_silver | 실버 티어 아이콘 | 60x40dp |
| img_tier_gold | 골드 티어 아이콘 | 60x40dp |
| img_tier_platinum | 플래티넘 티어 아이콘 | 60x40dp |
| img_tier_diamond | 다이아몬드 티어 아이콘 | 60x40dp |
| ic_track | 거리 아이콘 | 20x20dp |
| ic_flame | 페이스 아이콘 | 20x20dp |
| ic_timer | 시간 아이콘 | 20x20dp |
| ic_arrow_right | 화살표 아이콘 | 24x24dp / 20x20dp |
| img_mission_placeholder | 미션 이미지 기본값 | 72x72dp |
| ic_home | 홈 탭 아이콘 | 24x24dp |
| ic_rank | 랭킹 탭 아이콘 | 24x24dp |
| ic_record | 기록 탭 아이콘 | 24x24dp |
| ic_mission | 미션 탭 아이콘 | 24x24dp |
| ic_user | 마이페이지 탭 아이콘 | 24x24dp |
| img_empty_track | 빈 상태 러닝 트랙 일러스트 | 87.8x60dp |
| img_mission_debut | 데뷔전 미션 아이콘 | 68x68dp |
| img_mission_location | 집 앞 스타트 미션 아이콘 | 58x58dp |

---

## 레이아웃 규칙 요약

| 요소 | Fixed | Flexible |
|------|-------|----------|
| 좌우 패딩 | O | - |
| 아이템 간 간격 | O | - |
| 아이템 width | - | O |
| 이벤트 배너 높이 (40dp) | O | - |
| 하단 스크롤 여백 (40dp) | O | - |

---

## 구현 체크리스트

### presentation:main 모듈
- [ ] Title Bar (로고, 알림 아이콘) - 상단 고정
- [ ] 타이틀 하단 고정 영역 (8dp)
- [ ] Tier Card (5가지 티어 아이콘, 정보, Progress Bar)
- [ ] Today's Run Card (Distance, Pace, Time) - Flexible width 적용
- [ ] This Week Card (총 거리, 요일 인디케이터)
- [ ] Mission Event Section (헤더, 배너 height 40dp 고정, 미션 그리드 Flexible)
- [ ] 스크롤 콘텐츠와 BottomNav 간격 40dp
- [ ] 세로 스크롤 구현

### Tier Info Bottom Sheet (바텀시트)
- [ ] 딤(Dim) 배경 (rgba(12, 13, 14, 0.6))
- [ ] 바텀시트 컨테이너 (상단 모서리 36dp)
- [ ] 드래그 핸들 인디케이터
- [ ] 현재 티어 정보 섹션
- [ ] 티어 목록 카드 (5가지 티어)
- [ ] 티어 안내 문구 (bullet list)
- [ ] 딤 클릭 시 닫기 동작

### Empty State (빈 데이터 상태)
- [ ] Today's Run Card 빈 상태 (러닝 트랙 일러스트 + 안내 문구)
- [ ] This Week Card 빈 상태 (0 km + 안내 필드)
- [ ] 데이터 유무에 따른 UI 분기 처리

### presentation:common 모듈
- [ ] Bottom Navigation Bar (5개 탭) - 하단 고정, 공통 컴포넌트로 분리

### 에셋
- [ ] 5가지 티어 이미지 (Bronze, Silver, Gold, Platinum, Diamond)
- [ ] 미션 placeholder 이미지
- [ ] 빈 상태 러닝 트랙 일러스트
- [ ] 초기 미션 아이콘 (데뷔전, 집 앞 스타트)
