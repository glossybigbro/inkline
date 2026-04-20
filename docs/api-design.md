# API Design: Inkline

## Overview

Inkline은 **어떤 Text composable에든 붙일 수 있는 도구**로 설계한다.
`InklineText` 같은 전용 composable이 아니라, 기존 Text에 연결하는 3-hook 패턴.

```kotlin
val inkline = rememberInkline {
    underline(offset = 4.dp, thickness = 1.5.dp, color = Color.Blue)
}

Text(
    text = inkline.apply(text),
    modifier = Modifier.drawBehind(inkline),
    onTextLayout = inkline::onTextLayout
)
```

---

## Why Not `Modifier.inkline()`?

밑줄을 그리려면 텍스트 레이아웃 정보(각 라인 위치, 길이)가 필요하다.
`TextLayoutResult`는 `Text`의 `onTextLayout` 콜백으로만 접근 가능하고, Modifier만으로는 가져올 수 없다.

Compose 내부 `TextStringSimpleNode`는 `Paragraph`를 직접 소유해서 가능하지만,
외부 라이브러리는 `onTextLayout` 경유가 유일한 방법.

Saket Narayan의 [extended-spans](https://github.com/saket/extended-spans)도 같은 결론.

## Why Not `InklineText()` Composable?

전용 composable로 감싸면 당장은 간결하지만:

- 기능이 늘어날수록 파라미터가 계속 늘어남
- 사용자의 커스텀 Text composable과 호환 불가
- Material Text, BasicText 등 다양한 Text variant에 대응 불가

3-hook 패턴은 **설정 객체**가 핵심이고, Text는 뭘 쓰든 상관없다.
기능이 추가돼도 설정만 늘어나고 Text 쪽 연결 코드는 항상 동일.

---

## API Signature

### rememberInkline

```kotlin
@Composable
fun rememberInkline(
    block: InklineScope.() -> Unit
): Inkline
```

DSL 빌더로 장식을 설정한다.

### InklineScope

```kotlin
interface InklineScope {
    fun underline(
        offset: Dp = 2.dp,
        thickness: Dp = 1.dp,
        color: Color = Color.Unspecified,
        style: InklineStyle = InklineStyle.Solid,
    )
}
```

v1은 `underline()`만 제공. 추후 `overline()`, `strikethrough()` 등 추가 가능.

### InklineStyle

```kotlin
enum class InklineStyle {
    Solid,
    Dashed,
    Dotted,
    Wavy,
}
```

### Inkline

```kotlin
class Inkline {
    // Text의 onTextLayout에 연결
    fun onTextLayout(result: TextLayoutResult)

    // AnnotatedString 변환 (기존 decoration 제거 → Inkline이 직접 그림)
    fun apply(text: AnnotatedString): AnnotatedString
    fun apply(text: String): AnnotatedString

    // Modifier.drawBehind에 연결
    fun DrawScope.draw()
}
```

3-hook 연결:
1. `inkline.apply(text)` → AnnotatedString 변환
2. `inkline::onTextLayout` → 레이아웃 정보 캡처
3. `Modifier.drawBehind(inkline)` → Canvas에 밑줄 그리기

---

## Parameters

| Parameter | Type | Default | 설명 |
|-----------|------|---------|------|
| `offset` | `Dp` | `2.dp` | 텍스트 baseline과 밑줄 사이 간격 |
| `thickness` | `Dp` | `1.dp` | 밑줄 두께 |
| `color` | `Color` | `Color.Unspecified` | 밑줄 색상. Unspecified면 텍스트 색상 따라감 |
| `style` | `InklineStyle` | `InklineStyle.Solid` | 밑줄 스타일 |

단위는 CSS처럼 **절대값(Dp)**. Compose 표준(`padding`, `border` 등)과 일관성 유지.
Flutter의 상대값(배수) 방식은 폰트마다 결과가 달라져서 채택하지 않음.

---

## Design Decisions

### 1. 3-hook 패턴 (extended-spans 참고)

Saket Narayan의 extended-spans가 확립한 패턴을 따른다.

```
rememberInkline { ... }  →  설정
inkline.apply(text)       →  AnnotatedString 변환
inkline::onTextLayout     →  레이아웃 캡처
drawBehind(inkline)       →  그리기
```

사용자가 3곳에 코드를 넣어야 하는 건 트레이드오프지만,
어떤 Text에든 붙일 수 있는 유연성이 더 크다.

### 2. DSL 빌더

```kotlin
// v1
rememberInkline {
    underline(offset = 4.dp)
}

// v2에서 이렇게 확장 가능
rememberInkline {
    underline(offset = 4.dp, color = Color.Blue)
    overline(thickness = 2.dp)
    strikethrough(style = InklineStyle.Wavy)
}
```

함수 파라미터가 아닌 DSL 빌더를 쓰는 이유:
- 장식 종류가 늘어나도 API가 깔끔하게 유지됨
- 여러 장식을 조합할 수 있음
- Compose 생태계 관례 (`Scaffold { }`, `LazyColumn { }` 등)

### 3. Dp 절대값

벤치마킹 결과 참고 (`docs/benchmarking.md`):
- CSS: 절대값 (`px`, `em`) → 가장 예측 가능
- Flutter: 상대값 (배수) → 폰트마다 다름
- iOS: 프리셋 → 세밀한 제어 불가

Compose에서 크기 단위는 `Dp`가 표준. density-independent.

### 4. Color.Unspecified 기본값

CSS의 `currentcolor` 동작과 동일.
대부분 밑줄과 텍스트 색상이 같으므로, 다를 때만 명시.

---

## Usage Examples

### Basic

```kotlin
val inkline = rememberInkline {
    underline()
}

Text(
    text = inkline.apply("Terms of Service"),
    modifier = Modifier.drawBehind(inkline),
    onTextLayout = inkline::onTextLayout
)
```

### Custom Offset + Color

```kotlin
val inkline = rememberInkline {
    underline(
        offset = 4.dp,
        thickness = 1.5.dp,
        color = Color.Blue
    )
}

Text(
    text = inkline.apply("different offers"),
    modifier = Modifier.drawBehind(inkline),
    onTextLayout = inkline::onTextLayout
)
```

### Wavy Style

```kotlin
val inkline = rememberInkline {
    underline(
        style = InklineStyle.Wavy,
        color = Color.Red,
        thickness = 1.5.dp
    )
}

Text(
    text = inkline.apply("speling error"),
    modifier = Modifier.drawBehind(inkline),
    onTextLayout = inkline::onTextLayout
)
```

### Multi-line

```kotlin
val inkline = rememberInkline {
    underline(offset = 3.dp)
}

Text(
    text = inkline.apply(longText),
    modifier = Modifier
        .width(200.dp)
        .drawBehind(inkline),
    onTextLayout = inkline::onTextLayout,
    maxLines = 3,
    overflow = TextOverflow.Ellipsis
)
```

---

## Out of Scope (v1)

| Feature | 이유 | 고려 시점 |
|---------|------|----------|
| `overline()` | 밑줄 먼저 완성 | v2 (DSL 구조상 추가 용이) |
| `strikethrough()` | 밑줄 먼저 완성 | v2 |
| AnnotatedString 부분 적용 | span 단위 레이아웃 계산 복잡 | v2 |
| Animation | 핵심 기능 안정화 우선 | v2 |
| Double 스타일 | 사용 빈도 낮음 | 커뮤니티 요청 시 |

---

## References

- [Saket Narayan - Drawing custom text spans in Compose UI](https://saket.me/compose-custom-text-spans/)
- [GitHub - saket/extended-spans](https://github.com/saket/extended-spans)
- [Romain Guy - A better underline for Android](https://medium.com/androiddevelopers/a-better-underline-for-android-90ba3a2e4fb)
- [MDN: text-underline-offset](https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-offset)
