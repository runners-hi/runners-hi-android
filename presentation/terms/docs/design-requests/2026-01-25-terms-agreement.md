# 디자인 작업 요청서: 이용약관 동의 화면

## 작업 정보
| 항목 | 내용 |
|------|------|
| 작업 유형 | UI 구현 |
| 우선순위 | 높음 |
| Figma URL | https://www.figma.com/design/HTvSziiFcmlKFo3fjFM8gI?node-id=1-2267 |

---

## 디자인 스펙

### 레이아웃
- **배경색**: #17191C (BlueGray90)
- **좌우 패딩**: 24dp

### 상단 네비게이션
- **뒤로가기 아이콘**: ic/arrow_back/w (24x24dp)
- **위치**: top=70dp, left=24dp

### 타이틀
- **텍스트**: "가입을 위해\n약관 동의가 필요해요"
- **폰트**: Pretendard SemiBold 24sp
- **색상**: #F1F2F4 (BlueGray5)
- **위치**: top=158dp (중앙 정렬), left=24dp
- **줄 간격**: 1.5

### 전체 동의 섹션
- **체크박스**: ic/chck_bg/gray (24x24dp)
- **텍스트**: "약관에 모두 동의합니다."
- **폰트**: Pretendard SemiBold 16sp
- **색상**: #FFFFFF
- **화살표**: ic/arrow/right/w (24x24dp)
- **패딩**: vertical 16dp
- **위치**: top=234dp

### 구분선
- **색상**: #454B54 (BlueGray70)
- **높이**: 1dp
- **위치**: top=298dp

### 개별 약관 항목
각 항목 구성:
- **체크박스**: ic/chck_bg/gray (24x24dp)
- **텍스트**: Pretendard Regular 16sp, #FFFFFF
- **화살표**: ic/arrow/right/w (24x24dp)
- **패딩**: vertical 16dp
- **간격**: 체크박스-텍스트 16dp

항목 목록 (top 위치):
1. 서비스 이용약관 동의 (필수) - top=307dp
2. 개인정보 수집 및 이용 동의 (필수) - top=363dp
3. 마케팅 정보 수신 동의 (선택) - top=419dp
4. SNS 수신 동의 (선택) - top=475dp
5. 만 14세 이상입니다 (필수) - top=531dp

### 다음 버튼
- **비활성화 상태**:
  - 배경색: #255860 (어두운 청록색)
  - 텍스트 색상: #17191C
- **활성화 상태**:
  - 배경색: Primary (#00EEFF)
  - 텍스트 색상: #17191C
- **크기**: 312dp x 56dp
- **모서리**: 71dp (pill shape)
- **텍스트**: "다음", Pretendard SemiBold 16sp
- **위치**: bottom 중앙, top=800dp

---

## 체크박스 상태

### 미선택 상태
- ic/chck_bg/gray (회색 배경 체크박스)

### 선택 상태
- ic/chck_bg/primary (Primary 색상 체크박스 + 체크 아이콘)

---

## 에셋 목록

| 에셋명 | 용도 | 크기 |
|--------|------|------|
| ic_arrow_back_w | 뒤로가기 | 24x24dp |
| ic_arrow_right_w | 상세보기 화살표 | 24x24dp |
| ic_chck_bg_gray | 미선택 체크박스 | 24x24dp |
| ic_chck_bg_primary | 선택 체크박스 | 24x24dp |

---

## 구현 체크리스트

- [ ] 배경색 및 레이아웃 적용
- [ ] 상단 뒤로가기 버튼
- [ ] 타이틀 텍스트 스타일
- [ ] 전체 동의 섹션 구현
- [ ] 구분선
- [ ] 개별 약관 항목 리스트
- [ ] 체크박스 선택/미선택 상태
- [ ] 다음 버튼 활성화/비활성화 상태
- [ ] 항목 클릭 시 상세 화면 이동
