# Benchmarking: Text Underline Across Platforms

## Background

Jetpack Compose의 `TextDecoration.Underline`은 on/off만 가능하다.
offset, thickness, color, style 어느 것도 커스텀할 수 없다.

이로 인해 실제 발생하는 문제:
- 알파벳 `f`가 `e`로 보이는 가독성 이슈 (밑줄이 글리프에 붙음)
- descender(`g`, `y`, `p`)와 밑줄 충돌
- 프로젝트마다 `drawBehind` 워크어라운드를 반복 구현

다른 플랫폼은 이 문제를 어떻게 풀고 있는지 비교한다.

---

## Platform Comparison

### CSS

가장 성숙한 구현. 2020년대 들어 모든 모던 브라우저에서 완전 지원.

| Property | 설명 | 값 |
|----------|------|-----|
| `text-decoration-line` | 선 종류 | `underline`, `overline`, `line-through` |
| `text-decoration-color` | 선 색상 | 모든 CSS color 값 |
| `text-decoration-style` | 선 스타일 | `solid`, `dashed`, `dotted`, `double`, `wavy` |
| `text-decoration-thickness` | 선 두께 | `<length>`, `<percentage>`, `from-font`, `auto` |
| `text-underline-offset` | 텍스트-밑줄 간격 | `auto`, `<length>`, `<percentage>` |
| `text-underline-position` | 밑줄 위치 | `auto`, `from-font`, `under`, `left`, `right` |

```css
a {
  text-underline-offset: 4px;
  text-decoration-thickness: 1.5px;
  text-decoration-color: blue;
  text-decoration-style: wavy;
}
```

**특징:**
- thickness에 절대값(`px`, `em`) 사용 — 폰트 크기와 독립적으로 제어 가능
- offset으로 간격을 정밀 조절 — `f`/`e` 문제 해결의 핵심
- `from-font` 값으로 폰트 자체 메트릭 활용 가능

---

### iOS / macOS

UIKit의 `NSAttributedString`으로 스타일과 색상을 제어한다.

**NSUnderlineStyle (bitwise 조합):**

| 스타일 | 값 | 패턴 | 값 |
|--------|-----|------|-----|
| `.single` | 0x01 | (solid) | 0x00 |
| `.thick` | 0x02 | `.patternDot` | 0x100 |
| `.double` | 0x09 | `.patternDash` | 0x200 |
| | | `.patternDashDot` | 0x300 |
| | | `.patternDashDotDot` | 0x400 |

```swift
// UIKit
let attributes: [NSAttributedString.Key: Any] = [
    .underlineStyle: NSUnderlineStyle.thick.rawValue | NSUnderlineStyle.patternDash.rawValue,
    .underlineColor: UIColor.blue
]

// SwiftUI (iOS 16+)
Text("Hello")
    .underline(true, pattern: .dash, color: .blue)
```

**특징:**
- 스타일(single/thick/double)과 패턴(dot/dash)을 bitwise OR로 조합
- 색상은 별도 attribute로 지원
- **offset 제어 불가** — 텍스트-밑줄 간격을 조절하려면 NSLayoutManager 커스텀 필요
- **thickness 커스텀 불가** — single/thick/double 프리셋만 제공

---

### Android / Jetpack Compose

가장 제한적인 플랫폼.

**Compose:**
```kotlin
// 이게 전부
Text(
    text = "Hello",
    textDecoration = TextDecoration.Underline
)
```
- on/off만 가능. color, thickness, style, offset 모두 불가.
- `TextDecoration.combine()`으로 underline + lineThrough 조합은 가능하지만 커스텀 아님.

**Framework (View 시스템):**
- `UnderlineSpan` — SpannableString에 밑줄 적용. 파라미터 없음.
- `TextPaint.setUnderlineText(boolean)` — 단순 토글.

**워크어라운드:**
```kotlin
// 현재 유일한 방법: onTextLayout + drawBehind
var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
Text(
    text = "Hello",
    onTextLayout = { layoutResult = it },
    modifier = Modifier.drawBehind {
        layoutResult?.let { layout ->
            // 각 라인마다 직접 Canvas에 선 그리기
            for (i in 0 until layout.lineCount) {
                val left = layout.getLineLeft(i)
                val right = layout.getLineRight(i)
                val bottom = layout.getLineBottom(i)
                drawLine(
                    color = Color.Blue,
                    start = Offset(left, bottom + 4f),  // offset
                    end = Offset(right, bottom + 4f),
                    strokeWidth = 1.5f                    // thickness
                )
            }
        }
    }
)
```
- 매번 이 보일러플레이트를 반복해야 함
- multi-line, RTL, ellipsis 처리까지 하면 코드가 급격히 복잡해짐

---

### Flutter

Compose보다는 낫지만 offset이 빠져있다.

| Property | 설명 | 값 |
|----------|------|-----|
| `decoration` | 선 종류 | `TextDecoration.underline`, `.overline`, `.lineThrough` |
| `decorationColor` | 선 색상 | 모든 Color 값 |
| `decorationStyle` | 선 스타일 | `solid`, `dashed`, `dotted`, `double`, `wavy` |
| `decorationThickness` | 선 두께 | `double` (폰트 stroke 기준 배수) |

```dart
Text(
  'Hello World',
  style: TextStyle(
    decoration: TextDecoration.underline,
    decorationColor: Colors.blue,
    decorationStyle: TextDecorationStyle.wavy,
    decorationThickness: 2.0,  // 폰트 기준 2배
  ),
)
```

**특징:**
- color, style 지원은 CSS 수준
- thickness는 **상대값** (폰트 stroke 기준 배수) — 절대값 지정 불가
- **offset 미지원** — 간격 조절하려면 shadow hack이나 커스텀 painting 필요

---

## Comparison Matrix

| Feature | CSS | iOS/macOS | Android/Compose | Flutter |
|---------|:---:|:---------:|:---------------:|:-------:|
| **Color** | O | O | X | O |
| **Thickness** | O (절대값) | △ (프리셋) | X | △ (상대값) |
| **Style** (solid/dashed/dotted/wavy) | O | O (bitwise) | X | O |
| **Double** | O | O | X | O |
| **Offset** (텍스트-밑줄 간격) | O | X | X | X |
| **Position** (under/over) | △ | X | X | X |
| **Multi-line** | O (자동) | O (자동) | X (수동) | O (자동) |

---

## Key Insights

1. **Android/Compose가 가장 제한적** — 유일하게 on/off만 지원. Inkline이 채울 갭이 명확하다.

2. **CSS가 가장 성숙** — 6개 독립 프로퍼티로 완전한 제어 제공. API 네이밍과 설계 철학의 참고 대상.

3. **Offset은 CSS만 네이티브 지원** — iOS, Flutter도 못 하는 것. Inkline의 핵심 차별점이 될 수 있다.

4. **Thickness 단위는 플랫폼마다 다름** — CSS는 절대값(`px`), Flutter는 상대값(배수), iOS는 프리셋. 어떤 방식을 택할지 API 설계 시 결정 필요.

5. **모든 플랫폼의 워크어라운드는 Canvas 기반** — Android drawBehind, iOS NSLayoutManager, Flutter CustomPainter. Compose의 `drawBehind` + `TextLayoutResult` 접근이 업계 표준 패턴.

---

## References

- [MDN: text-decoration](https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration)
- [MDN: text-underline-offset](https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-offset)
- [Apple: NSUnderlineStyle](https://developer.apple.com/documentation/uikit/nsunderlinestyle)
- [Apple: NSAttributedString.Key.underlineColor](https://developer.apple.com/documentation/foundation/nsattributedstring/key/underlinecolor)
- [Android: UnderlineSpan](https://developer.android.com/reference/android/text/style/UnderlineSpan)
- [A better underline for Android - Android Developers Medium](https://medium.com/androiddevelopers/a-better-underline-for-android-90ba3a2e4fb)
- [Flutter: TextStyle](https://api.flutter.dev/flutter/painting/TextStyle-class.html)
- [Drawing custom text spans in Compose - Saket Narayan](https://saket.me/compose-custom-text-spans/)
