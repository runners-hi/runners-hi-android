# /figma - Figma 디자인 구현 스킬

Figma URL을 받아서 Android Compose UI를 구현합니다.

## 사용법

```
/figma <Figma URL>
```

예시:
```
/figma https://www.figma.com/design/xxx/yyy?node-id=123-456
```

## 워크플로우

### 1단계: URL 파싱
- fileKey 추출 (design/:fileKey/ 부분)
- nodeId 추출 (node-id=xxx-xxx → xxx:xxx로 변환)

### 2단계: 디자인 컨텍스트 추출
```
mcp__figma__get_design_context(fileKey, nodeId)
```
- 색상, 레이아웃, 에셋 URL 확인

### 3단계: 스크린샷 확인
```
mcp__figma__get_screenshot(fileKey, nodeId)
```
- 시각적으로 디자인 확인

### 4단계: 에셋 다운로드 (필수!)
결과에서 `const imgXxx = "https://..."` 형태의 에셋 URL이 있으면:

1. curl로 다운로드
2. 파일 형식 확인 (file 명령어)
3. SVG → Android Vector Drawable XML 변환
4. `res/drawable/` 에 저장

```bash
# 다운로드
curl -L "<에셋URL>" -o temp_asset

# 형식 확인
file temp_asset

# SVG면 XML로 변환하여 저장
# PNG/JPG면 그대로 drawable에 저장
```

### 5단계: Compose 코드 생성
- Figma 코드(React/Tailwind)를 Jetpack Compose로 변환
- 에셋은 `painterResource(R.drawable.xxx)` 사용
- 색상은 Figma에 명시된 코드 사용

### 6단계: 빌드 확인
```bash
./gradlew :모듈:assembleDebug
```

## 금지 사항

- ❌ Canvas로 로고/아이콘 직접 그리기
- ❌ 에셋 URL 무시하고 직접 구현
- ❌ 색상 코드 임의 변경

## 에셋 파일 명명 규칙

| Figma 이름 | Android drawable |
|-----------|------------------|
| img/logo/runnershi/main | ic_logo_runnershi.xml |
| icon/arrow/left | ic_arrow_left.xml |
| bg/gradient/primary | bg_gradient_primary.xml |
