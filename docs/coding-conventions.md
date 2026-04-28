# Coding Conventions: Inkline

## Overview

Inkline은 3개의 공식 표준을 따른다.
프로젝트 특화 규칙은 이 문서에서 별도로 정의한다.

| 표준 | 출처 | 적용 범위 |
|------|------|----------|
| [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) | JetBrains | 네이밍, 파일 구조, 포맷 |
| [Compose API Guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md) | Google | @Composable 네이밍, Modifier 위치, Stability |
| [KDoc](https://kotlinlang.org/docs/kotlin-doc.html) | Kotlin 공식 | 문서화 태그, 형식 |

ktlint + Spotless가 Kotlin Coding Conventions를 자동 강제한다.
이 문서는 도구가 잡지 못하는 규칙을 다룬다.

---

## 1. 언어 규칙

**이게 뭔지:**
코드, 주석, 문서에 어떤 언어를 쓸지 정한 규칙.
오픈소스 라이브러리는 전 세계 개발자가 읽으므로 public API는 영어가 필수.

| 대상 | 언어 | 이유 |
|------|------|------|
| 코드 (변수명, 함수명, 클래스명) | 영어 | Kotlin 표준 |
| public API KDoc | **영어** | IDE 자동완성에 뜸, Dokka가 문서로 생성 |
| internal 코드 주석 | 한국어 OK | 외부에 안 보임 |
| 커밋 메시지 | 영어 | Conventional Commits (feat:, fix:) |
| docs/ 문서 | 한국어 OK | 프로젝트 내부 설계 문서 |

---

## 2. Compose 규칙

**이게 뭔지:**
Google의 Compose API Guidelines에서 Inkline에 적용되는 핵심 규칙.
Compose 라이브러리가 생태계에서 일관되게 동작하려면 이 규칙을 따라야 한다.

### 2-1. @Composable 네이밍

| 반환 타입 | 네이밍 | 예시 |
|----------|--------|------|
| Unit (UI 방출) | PascalCase 명사 | `InklineText()` |
| 값 반환 | camelCase 동사/remember | `rememberInkline()` |

- `remember*` 접두사: Composition에 걸쳐 캐싱하는 함수에 사용.

### 2-2. 파라미터 순서

@Composable 함수의 파라미터는 이 순서를 따른다:

```
1. Required 파라미터 (기본값 없음)
2. modifier: Modifier = Modifier (첫 번째 optional)
3. 추가 optional 파라미터
4. Trailing @Composable lambda
```

Inkline은 현재 @Composable UI를 직접 방출하지 않으므로,
이 규칙은 향후 `InklineText` 같은 convenience composable 추가 시 적용.

### 2-3. Stability 어노테이션

**이게 뭔지:**
Compose 컴파일러에게 "이 클래스의 인스턴스가 변하지 않는다"고 알려주는 어노테이션.
Stable한 파라미터만 받는 composable은 입력이 같으면 recomposition을 건너뛴다(skip).
라이브러리가 이 어노테이션을 빠뜨리면 사용자 앱에서 불필요한 recomposition이 발생한다.

| 어노테이션 | 의미 | 사용 대상 |
|-----------|------|----------|
| `@Immutable` | 생성 후 절대 변하지 않음 | data class, enum |
| `@Stable` | equals가 일관적 (같으면 계속 같음) | 상태 홀더, 인터페이스 |

Inkline 적용:

```kotlin
@Immutable
data class UnderlineConfig(...)

@Immutable
enum class InklineStyle { ... }

@Stable
interface InklineScope { ... }
```

**규칙:** 한번 public API에 붙인 어노테이션은 제거하지 않는다.

### 2-4. Defaults Object

**이게 뭔지:**
컴포넌트의 기본값을 하나의 object에 모아두는 패턴.
Compose 생태계의 관례 — `ButtonDefaults`, `TextFieldDefaults` 등.
사용자가 "기본 offset이 뭐지?" 할 때 한 곳에서 찾을 수 있다.

```kotlin
object InklineDefaults {
    val Offset = 2.dp
    val Thickness = 1.dp
    val Color = Color.Unspecified
    val Style = InklineStyle.Solid
}
```

기본값을 Defaults object으로 이동할지는 API 표면이 커질 때 검토.
현재는 `UnderlineConfig`의 기본값으로 충분.

---

## 3. KDoc 규칙

**이게 뭔지:**
Kotlin 소스 코드에 작성하는 문서화 주석 형식.
IDE가 자동완성 시 보여주고, Dokka가 HTML API 문서로 변환한다.
라이브러리의 KDoc은 곧 사용자가 읽는 매뉴얼이다.

### 3-1. 필수 대상

| 대상 | KDoc 필수? |
|------|-----------|
| public class / interface / enum | **필수** |
| public function | **필수** |
| public property | 의미가 자명하지 않으면 필수 |
| internal / private | 선택 (한국어 OK) |
| 테스트 코드 | 불필요 |

### 3-2. KDoc 구조

```kotlin
/**
 * 첫 줄은 요약 (한 문장). <- 이게 IDE 자동완성에 뜨는 텍스트
 *
 * 상세 설명 (필요한 경우).
 * 여러 줄 가능.
 *
 * @param name 파라미터 설명.
 * @return 반환값 설명.
 * @throws ExceptionType 예외 발생 조건.
 * @sample com.example.SampleFunction
 * @see RelatedClass
 */
```

### 3-3. 태그 순서

공식 순서를 따른다:

```
@param → @return → @throws → @sample → @see
```

### 3-4. 작성 스타일

**간결한 인라인 참조를 선호한다.**
`@param`을 나열하는 대신, 설명문 안에서 `[paramName]`으로 참조:

```kotlin
// 좋음 — 자연스럽게 읽힌다
/**
 * Draws underlines behind the text using the given [inkline] configuration.
 * Call this inside `Modifier.drawBehind { }`.
 */
fun Modifier.drawBehind(inkline: Inkline): Modifier

// 나쁨 — 형식적이고 반복적
/**
 * Draws underlines behind the text.
 *
 * @param inkline The inkline configuration to use.
 * @return A new Modifier with the underline drawing applied.
 */
fun Modifier.drawBehind(inkline: Inkline): Modifier
```

단, 파라미터가 3개 이상이면 `@param` 태그를 사용한다:

```kotlin
/**
 * Configuration for a single underline decoration.
 *
 * @param offset Gap between the text baseline and the underline.
 * @param thickness Underline stroke width.
 * @param color Underline color. [Color.Unspecified] follows the text color.
 * @param style Visual style of the underline.
 */
data class UnderlineConfig(...)
```

### 3-5. 코드 예시

KDoc에 사용법 예시를 포함한다.
사용자가 IDE에서 바로 복사해서 쓸 수 있도록:

```kotlin
/**
 * Creates and remembers an [Inkline] instance.
 *
 * ```kotlin
 * val inkline = rememberInkline {
 *     underline(offset = 4.dp, color = Color.Blue)
 * }
 * ```
 */
@Composable
fun rememberInkline(block: InklineScope.() -> Unit): Inkline
```

### 3-6. enum 값 개별 설명

enum의 각 값에 KDoc을 작성한다:

```kotlin
enum class InklineStyle {
    /** Continuous straight line. */
    Solid,

    /** Repeating dash segments. */
    Dashed,

    /** Repeating dot segments. */
    Dotted,

    /** Sine wave pattern. */
    Wavy,
}
```

---

## 4. API 진화 규칙

**이게 뭔지:**
라이브러리 버전이 올라갈 때 기존 사용자의 코드를 깨뜨리지 않기 위한 규칙.
Binary Compatibility Validator(C-2)가 자동으로 감시하지만, 코드 작성 시점에 의식해야 한다.

### 4-1. 하지 말 것

| 금지 | 이유 |
|------|------|
| public 함수/클래스 삭제 | 사용자 코드 컴파일 실패 |
| public 함수 시그니처 변경 | 바이너리 호환성 깨짐 |
| 기본값 있는 파라미터를 기본값 없이 변경 | 사용자 코드 컴파일 실패 |
| `@Stable` / `@Immutable` 제거 | 사용자 앱 recomposition 성능 저하 |

### 4-2. 해야 할 것

| 방법 | 설명 |
|------|------|
| 새 파라미터는 기본값 필수 | 기존 호출 코드가 깨지지 않음 |
| 제거 대신 `@Deprecated` | 사용자에게 마이그레이션 시간을 줌 |
| 오버로드 추가 | 시그니처 변경 대신 새 오버로드 |

```kotlin
// v0.2.0
fun underline(offset: Dp = 2.dp)

// v0.3.0 — 파라미터 추가 시 기본값 필수
fun underline(offset: Dp = 2.dp, animation: Boolean = false)

// 제거가 필요할 때 — 바로 지우지 않고 deprecated
@Deprecated("Use underline() instead", ReplaceWith("underline()"))
fun addUnderline() { ... }
```

---

## 5. 파일 구조

| 규칙 | 설명 |
|------|------|
| 파일명 = 클래스명 | `Inkline.kt`, `InklineStyle.kt` |
| top-level 함수는 설명적 파일명 | `RememberInkline.kt`, `DrawBehindExtension.kt` |
| 테스트 파일명 = 대상 + Test | `InklineStyleTest.kt` |
| 패키지 = 모듈 경로 | `io.github.glossybigbro.inkline` |

---

## 6. 포맷팅

ktlint + Spotless가 자동으로 강제하는 규칙:

- 4-space 들여쓰기 (탭 금지)
- trailing comma 사용
- 와일드카드 import 금지
- 빈 줄: 클래스 멤버 사이 1줄

커밋 전 `./gradlew spotlessApply` 실행.

---

## 참고

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose API Guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md)
- [Compose Component API Guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md)
- [KDoc - Documenting Kotlin Code](https://kotlinlang.org/docs/kotlin-doc.html)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
