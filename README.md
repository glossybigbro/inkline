<p align="center">
  <img src="docs/images/inkline-logo.svg" width="200" />
</p>

<p align="center">
  Draw the line that <code>TextDecoration</code> couldn't.<br/>
  Custom underline for Jetpack Compose.
</p>

<p align="center">
  <a href=""><img src="https://img.shields.io/maven-central/v/io.github.glossybigbro/inkline" /></a>
  <a href=""><img src="https://img.shields.io/badge/API-24%2B-brightgreen" /></a>
  <a href=""><img src="https://img.shields.io/badge/Jetpack%20Compose-BOM%202024-blue" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" /></a>
</p>

## The Problem

Compose's `TextDecoration.Underline` is on/off only. No offset, no thickness, no color, no style.

| Platform | Offset | Thickness | Color | Style |
|----------|:------:|:---------:|:-----:|:-----:|
| CSS | O | O | O | O |
| iOS (UIKit / SwiftUI) | O | O | O | O |
| Android Compose | X | X | X | X |
| **Inkline** | **O** | **O** | **O** | **O** |

This causes real problems:
- `f` looks like `e` when underline sticks to the glyph
- Descenders (`g`, `y`, `p`) collide with the underline
- Every project reinvents the same `drawBehind` workaround

## Features

- **Offset** - control the gap between text and underline
- **Thickness** - thin hairline to bold emphasis
- **Color** - independent from text color
- **Style** - solid, dashed, dotted, wavy
- **Multi-line** - works with text wrapping
- **Compose-first** - works with any Text composable, zero dependency

## Installation

```kotlin
// build.gradle.kts
implementation("io.github.glossybigbro:inkline:<version>")
```

## Quick Start

```kotlin
val inkline = rememberInkline {
    underline(
        offset = 4.dp,
        thickness = 1.5.dp,
        color = Color.Blue,
        style = InklineStyle.Solid
    )
}

Text(
    text = inkline.extend("Hello World"),
    modifier = Modifier.drawBehind(inkline),
    onTextLayout = inkline::onTextLayout
)
```

## Styles

```kotlin
// Solid (default)
rememberInkline { underline(offset = 4.dp) }

// Dashed
rememberInkline { underline(offset = 4.dp, style = InklineStyle.Dashed) }

// Dotted
rememberInkline { underline(offset = 4.dp, style = InklineStyle.Dotted) }

// Wavy
rememberInkline { underline(offset = 4.dp, style = InklineStyle.Wavy) }
```

## Advanced

```kotlin
// Multi-line support
val inkline = rememberInkline {
    underline(
        offset = 3.dp,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )
}

Text(
    text = inkline.extend(longText),
    modifier = Modifier.drawBehind(inkline),
    onTextLayout = inkline::onTextLayout,
    maxLines = 3,
    overflow = TextOverflow.Ellipsis
)
```

## Requirements

- Jetpack Compose BOM 2024.01.00+
- Minimum SDK 24
- Kotlin 1.9+

## License

```
Copyright 2026 glossybigbro

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
