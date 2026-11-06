# RH Latin 글꼴
- 무엇: Pretendard(SIL OFL 1.1)의 ASCII 32~126만 잘라 글자 폭을 아이폰 SF 폭으로 바꾼 파생 글꼴
- 이름: OFL 예약 이름(Pretendard·Inter·Source)을 피해 "RH Latin"으로 바꿈
- 결과: `presentation/common/src/main/res/font/rh_latin_<굵기>_<크기>.otf` 32개
- 다시 만들기(macOS, `pip install fonttools`): `python3 scripts/sf-latin-fonts/gen_latin.py presentation/common/src/main/res/font`
- 쓰는 곳: `presentation/common/.../component/SfText.kt`
- 라이선스: SIL Open Font License 1.1 (Pretendard 원본과 같음)
