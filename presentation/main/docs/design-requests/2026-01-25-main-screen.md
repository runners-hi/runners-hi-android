# 디자인 작업 요청서: 메인 화면 (홈)

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | UI 구현 |
| 우선순위 | 높음 |
| Figma URL | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-1118 |

---

## 전체 레이아웃
- **배경색**: #17191C (BlueGray90)
- **좌우 패딩**: 20dp (카드), 24dp (타이틀)
- **스크롤**: 세로 스크롤 가능

---

## 1. Title Bar (상단 헤더)
- **배경색**: #17191C (BlueGray90)
- **높이**: 56dp
- **좌측**: "Runners HI" 로고 (24dp 좌측 패딩)
- **우측**: 알림 아이콘 (28x28dp, 우측 24dp 패딩)

---

## 2. Tier Card (등급 카드)
- **배경색**: #2E3238 (BlueGray80)
- **테두리**: 1dp solid #454B54 (BlueGray70)
- **모서리**: 24dp
- **크기**: width=320dp, height=140dp
- **위치**: top=122dp

### 내용
- **티어 아이콘**: Gold 메달 이미지 (60x40dp)
- **티어명**: "Gold Runner" (Pretendard SemiBold 20sp, White)
- **레벨**: "Level 31" (Pretendard Regular 14sp, #F1F2F4)
- **화살표**: 우측 상단 (24x24dp)
- **Progress Bar**
  - 배경: #454B54, height=12dp, rounded
  - 진행: #F5FF66 (Primary Yellow)
  - 텍스트: "Progress to next level" / "10%" (14sp)

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

### 데이터 항목 (3개 가로 배치)
| 항목 | 아이콘 | 아이콘 배경 | 값 | 라벨 |
|------|--------|-------------|-----|------|
| Distance | track | #00EEFF (20% opacity) | 7.4 km | Distance |
| Pace | flame | #FF6363 (20% opacity) | 8'00'' | Pace |
| Time | timer | #F5FF66 (20% opacity) | 40:00 | Time |

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
- **화살표**: 우측 (24x24dp)

### 이벤트 배너
- **배경색**: #2E3238 (BlueGray80)
- **모서리**: 40dp
- **크기**: width=312dp, height=40dp
- **내용**: "추석 이벤트" (#8F97A3) + "2025.10.01 - 2025.10.31" (#C7CBD1)

### 미션 아이템 그리드 (3열)
- **아이템 크기**: width=96dp
- **가로 간격**: ~12dp
- **세로 간격**: ~8dp

#### 미션 아이템 구조
- **아이콘 컨테이너**: 72x100dp, rounded pill
  - 미완료: 배경 #2E3238, 테두리 #454B54
  - 완료: 배경 rgba(255,203,47,0.2), 테두리 #FFCB2F (Gold)
- **아이콘 이미지**: 컨테이너 내부 중앙
- **미션명**: Pretendard SemiBold 14sp, #F1F2F4
- **설명**: Pretendard Regular 13sp, #8F97A3

---

## 6. Bottom Navigation Bar
- **배경색**: #17191C (BlueGray90)
- **높이**: 89dp (인디케이터 포함)
- **상단 라인**: 1dp #454B54

### 탭 (5개)
| 탭 | 아이콘 | 라벨 |
|----|--------|------|
| 홈 | ic_home | 홈 |
| 랭킹 | ic_rank | 랭킹 |
| 기록 | ic_record | 기록 |
| 미션 | ic_mission | 미션 |
| 마이페이지 | ic_user | 마이페이지 |

- **아이콘 크기**: 24x24dp
- **라벨**: Pretendard Regular 12sp, #75808B (비활성)
- **인디케이터**: 하단 중앙, #8F97A3, 4dp height

---

## 에셋 목록

| 에셋명 | 용도 | 크기 |
|--------|------|------|
| ic_logo_runnershi_txt | 상단 로고 | 95x28dp |
| ic_notification | 알림 아이콘 | 28x28dp |
| img_tier_gold | 골드 티어 아이콘 | 60x40dp |
| ic_track | 거리 아이콘 | 20x20dp |
| ic_flame | 페이스 아이콘 | 20x20dp |
| ic_timer | 시간 아이콘 | 20x20dp |
| ic_arrow_right | 화살표 아이콘 | 24x24dp / 20x20dp |
| ic_home | 홈 탭 아이콘 | 24x24dp |
| ic_rank | 랭킹 탭 아이콘 | 24x24dp |
| ic_record | 기록 탭 아이콘 | 24x24dp |
| ic_mission | 미션 탭 아이콘 | 24x24dp |
| ic_user | 마이페이지 탭 아이콘 | 24x24dp |

---

## 구현 체크리스트

- [ ] Title Bar (로고, 알림 아이콘)
- [ ] Tier Card (티어 아이콘, 정보, Progress Bar)
- [ ] Today's Run Card (Distance, Pace, Time)
- [ ] This Week Card (총 거리, 요일 인디케이터)
- [ ] Mission Event Section (헤더, 배너, 미션 그리드)
- [ ] Bottom Navigation Bar (5개 탭)
- [ ] 세로 스크롤 구현
