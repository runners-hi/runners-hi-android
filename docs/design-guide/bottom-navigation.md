# Bottom Navigation 디자인 가이드

## Figma Reference
- **Node ID**: 1-1119
- **URL**: https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-1119

## 디자인 스펙

### 컨테이너
| 속성 | 값 |
|------|-----|
| 배경색 | #17191C (BlueGray/90) |
| 높이 | 89dp (60dp 탭 영역 + 28dp 인디케이터 영역 + 1dp 상단 라인) |
| 상단 라인 | #454B54 (BlueGray/70), 1dp |

### 탭 아이템
| 속성 | 값 |
|------|-----|
| 탭 너비 | 72dp |
| 탭 높이 | 60dp |
| 패딩 | horizontal: 4dp, vertical: 8dp |
| 아이콘-텍스트 간격 | 4dp |

### 아이콘
| 속성 | 값 |
|------|-----|
| 크기 | 24x24dp |
| 비활성 색상 | #75808B (BlueGray/50) |
| 활성 색상 | #00EEFF (Primary) |

### 텍스트
| 속성 | 값 |
|------|-----|
| 폰트 | Pretendard Regular |
| 크기 | 12sp |
| Line Height | 16sp |
| 비활성 색상 | #75808B (BlueGray/50) |
| 활성 색상 | #00EEFF (Primary) |

### 하단 인디케이터 바
| 속성 | 값 |
|------|-----|
| 높이 | 28dp |
| 인디케이터 색상 | #8F97A3 (BlueGray/40) |
| 인디케이터 크기 | 약 66dp x 4dp |
| 모서리 반경 | 2dp |

## 탭 목록

| 순서 | 탭 ID | 라벨 | 아이콘 (mono) |
|------|-------|------|---------------|
| 1 | HOME | 홈 | ic_home_mono |
| 2 | RANKING | 랭킹 | ic_rank_mono |
| 3 | RECORD | 기록 | ic_record_mono |
| 4 | MISSION | 미션 | ic_mission_mono |
| 5 | MY_PAGE | 마이페이지 | ic_user_mono |

## 아이콘 에셋 (Figma에서 Export 필요)

아래 아이콘들을 Figma에서 SVG로 Export 후 Android Studio Vector Asset으로 변환:

1. **ic_home_mono.xml** - 홈 아이콘
2. **ic_rank_mono.xml** - 트로피 아이콘
3. **ic_record_mono.xml** - 기록/펜 아이콘
4. **ic_mission_mono.xml** - 깃발 아이콘
5. **ic_user_mono.xml** - 사용자 아이콘

### 아이콘 색상 처리
- 아이콘은 mono 스타일 (단색)
- `android:tint`로 색상 변경 가능하도록 제작
- 기본 fill 색상: #75808B

## 컬러 토큰

```kotlin
// theme/Color.kt
val BlueGray90 = Color(0xFF17191C)  // 배경
val BlueGray70 = Color(0xFF454B54)  // 상단 라인
val BlueGray50 = Color(0xFF75808B)  // 비활성 아이콘/텍스트
val BlueGray40 = Color(0xFF8F97A3)  // 하단 인디케이터
val Primary = Color(0xFF00EEFF)     // 활성 아이콘/텍스트
```

## 구현 체크리스트

- [ ] 커스텀 아이콘 5개 추가 (Figma Export)
- [ ] 상단 라인 (1dp, BlueGray/70) 추가
- [ ] 하단 홈 인디케이터 바 추가
- [ ] Material NavigationBarItem → 커스텀 구현으로 변경
- [ ] 탭 크기 72x60dp 적용
