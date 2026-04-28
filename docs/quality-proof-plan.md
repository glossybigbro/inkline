# Quality Proof Plan: Inkline

## Overview

Inkline은 `TextDecoration.Underline`의 대체제다.
채택하려는 개발자에게 **"바꿔도 된다"**를 증명해야 한다.

이 문서는 그 증명에 필요한 모든 항목을 정리한다.
항목마다 **이게 뭔지 → 왜 하는지 → 어떻게 하는지**를 기술한다.

Inkline 규모(파일 6개)에 당장 필요한 것과, API가 커진 뒤에 도입할 것을 구분한다.

---

## 전체 항목 요약

| # | 항목 | Phase | Inkline 우선순위 |
|---|------|-------|-----------------|
| A-1 | Unit Tests | 테스트 | **필수** |
| A-2 | Compose UI Tests | 테스트 | 권장 |
| A-3 | Screenshot Tests | 테스트 | **필수** |
| A-4 | CI Matrix | 테스트 | 권장 |
| B-1 | Benchmark | 성능 | **필수** |
| B-2 | Baseline Profiles | 성능 | 나중에 |
| B-3 | Compose Compiler Metrics | 성능 | 권장 |
| B-4 | Performance Regression CI | 성능 | 나중에 |
| C-1 | Code Coverage (Kover) | 안정성 | 권장 |
| C-2 | Binary Compatibility Validator | 안정성 | **필수** |
| C-3 | Static Analysis (Detekt) | 안정성 | 권장 |
| C-4 | Custom Lint Rules | 안정성 | 나중에 |
| D-1 | API Docs (Dokka) | 문서 & 배포 | 권장 |
| D-2 | Proguard / R8 Rules | 문서 & 배포 | **필수** |
| D-3 | Maven Central | 문서 & 배포 | **필수** |
| D-4 | Size Analysis | 문서 & 배포 | 권장 |
| E-1 | CONTRIBUTING.md | 거버넌스 | 권장 |
| E-2 | Code of Conduct | 거버넌스 | 권장 |
| E-3 | Migration Guide | 거버넌스 | 나중에 |
| E-4 | Badge Wall | 거버넌스 | 권장 |
| E-5 | SECURITY.md | 거버넌스 | 권장 |
| F-1 | Dependabot / Renovate | 공급망 | 권장 |
| F-2 | Reproducible Builds | 공급망 | 나중에 |
| F-3 | KMP Readiness | 아키텍처 | 나중에 |
| F-4 | SBOM | 공급망 | 나중에 |

---

## Phase A — 테스트

검증 목표: **"정확하게 동작한다"**

---

### A-1. Unit Tests

**이게 뭔지:**
코드의 가장 작은 단위(함수, 클래스)를 독립적으로 검증하는 테스트.
Android 에뮬레이터 없이 개발 PC(JVM)에서 바로 실행된다.
실행 속도가 빨라서(수백 개가 1초 이내) 커밋할 때마다 돌릴 수 있다.

**왜 하는지:**
"이 함수에 이 값을 넣으면 이 결과가 나온다"를 코드로 보장한다.
리팩토링할 때 기존 동작이 깨지면 즉시 알 수 있다.
모든 테스트의 기초 — 이게 없으면 나머지 테스트도 의미 없다.

**Inkline에서 테스트할 것:**

| 테스트 대상 | 검증 항목 |
|------------|----------|
| `UnderlineConfig` | 기본값 검증 (offset=2.dp, thickness=1.dp, color=Unspecified, style=Solid) |
| `InklineStyle` | enum 값 완전성 (Solid, Dashed, Dotted, Wavy) |
| `InklineScopeImpl` | 단일 underline 등록, 복수 underline 등록, 빈 스코프 |
| `Inkline.apply(String)` | String → AnnotatedString 변환, 빈 문자열 처리 |
| `Inkline.apply(AnnotatedString)` | AnnotatedString 통과 (identity) |

- 경로: `inkline/src/test/kotlin/io/github/glossybigbro/inkline/`
- 도구: JUnit 4
- 기준: 전체 통과
- 누가 하나: 모든 라이브러리. Timber(파일 3개)조차 unit test가 있다.

---

### A-2. Compose UI Tests

**이게 뭔지:**
실제 Compose UI 트리를 메모리에 올려서 렌더링이 정상적으로 되는지 검증하는 테스트.
`createComposeRule()`로 화면을 구성하고, 특정 노드가 존재하는지·보이는지 확인한다.
에뮬레이터에서 돌리거나(androidTest) Robolectric으로 JVM에서 돌릴 수 있다.

**왜 하는지:**
Unit Test는 로직만 검증하지, "화면에 실제로 그려지는지"는 모른다.
Compose는 컴파일러 플러그인이 코드를 변환하기 때문에, 컴파일은 되지만 런타임에 크래시가 날 수 있다.
UI Test는 이 갭을 메운다 — 실제 Compose 환경에서 동작을 확인.

**Inkline에서 테스트할 것:**

| 테스트 대상 | 검증 항목 |
|------------|----------|
| `rememberInkline` + `Text` | 크래시 없이 렌더링 |
| 4가지 스타일 | Solid, Dashed, Dotted, Wavy 각각 렌더링 |
| 멀티라인 | 줄바꿈 텍스트에서 각 라인에 밑줄 |
| 빈 텍스트 | 빈 문자열 입력 시 크래시 없음 |
| onTextLayout 미연결 | onTextLayout 없이 사용해도 크래시 없음 (밑줄만 안 그려짐) |

- 경로: `inkline/src/androidTest/kotlin/io/github/glossybigbro/inkline/`
- 도구: `androidx.compose.ui:ui-test-junit4`
- 기준: 전체 통과
- 누가 하나: Compose 라이브러리는 대부분. Accompanist, Coil 등.

---

### A-3. Screenshot Tests

**이게 뭔지:**
UI를 렌더링해서 스크린샷을 찍고, 이전에 저장된 기준 이미지(golden image)와 픽셀 단위로 비교하는 테스트.
코드를 바꿨을 때 "눈에 보이는 변화"가 의도된 건지 실수인지를 자동으로 잡아낸다.
Roborazzi는 에뮬레이터 없이 JVM(Robolectric)에서 스크린샷을 생성한다.

**왜 하는지:**
밑줄 라이브러리는 **시각적 결과물이 곧 품질**이다.
"offset을 바꿨더니 밑줄이 글자를 뚫고 지나간다" — 이런 건 Unit Test로 못 잡는다.
코드 리뷰에서 "이 변경이 밑줄 모양을 바꾸나요?"라고 물을 필요 없이, CI가 diff 이미지를 보여준다.
Inkline에서 가장 가치가 높은 테스트 — 시각적 라이브러리의 핵심.

**Inkline에서 테스트할 것:**

| 테스트 대상 | 검증 항목 |
|------------|----------|
| 4가지 스타일 | 각 스타일의 렌더링 결과가 기준 이미지와 일치 |
| offset 변화 | offset 0.dp / 4.dp / 8.dp의 시각적 차이 |
| thickness 변화 | thickness 0.5.dp / 1.dp / 2.dp의 시각적 차이 |
| 멀티라인 | 3줄 텍스트 렌더링 |
| 다크 테마 | 어두운 배경에서의 렌더링 |

- 경로: `inkline/src/test/kotlin/io/github/glossybigbro/inkline/screenshot/`
- 도구: [Roborazzi](https://github.com/takahirom/roborazzi) — Robolectric 기반, 에뮬레이터 불필요
- 기준 이미지: `inkline/src/test/resources/screenshots/`
- 기준: 픽셀 diff 0% (정확 일치)
- CI 연동: PR에서 diff 이미지를 코멘트로 첨부
- 누가 하나: Accompanist(Google), Coil, Cash App 라이브러리들. 시각적 UI 라이브러리는 필수.

---

### A-4. CI Matrix

**이게 뭔지:**
하나의 테스트 스위트를 여러 환경(API 레벨, OS 버전)에서 병렬로 실행하는 CI 설정.
GitHub Actions의 `matrix` strategy로 구현한다.
한 번 push하면 API 23·28·34에서 동시에 테스트가 돌아간다.

**왜 하는지:**
Android는 API 레벨마다 동작이 다르다. Canvas API, TextLayoutResult 같은 저수준 API는 버전마다 미묘한 차이가 있다.
"내 에뮬레이터(API 34)에서는 되는데 사용자(API 23)에서 크래시"를 방지한다.

**Inkline 설정:**

| API Level | Android Version | 이유 |
|-----------|----------------|------|
| 23 | 6.0 (Marshmallow) | minSdk — 최저 지원 버전 |
| 28 | 9.0 (Pie) | 중간 구간 |
| 34 | 14 | 최신 안정 |

- 도구: GitHub Actions matrix strategy
- 기준: 모든 API 레벨에서 test 통과
- 누가 하나: AndroidX, Coil 등 대규모 라이브러리. 소형 라이브러리는 선택.

---

## Phase B — 성능

검증 목표: **"네이티브보다 느리지 않다"** (또는 허용 범위 내)

---

### B-1. Benchmark

**이게 뭔지:**
코드의 실행 시간, 메모리 사용량을 정밀하게 측정하는 테스트.
Android에서는 두 종류가 있다:
- **Microbenchmark**: 함수 단위. "이 객체 생성에 몇 ns 걸리나"
- **Macrobenchmark**: 프레임 단위. "이 화면 렌더링에 몇 ms 걸리나"

**왜 하는지:**
Inkline은 네이티브 `TextDecoration.Underline`을 대체한다.
대체제가 느리면 아무도 안 쓴다. **"네이티브 대비 오버헤드가 X%입니다"**를 수치로 보여줘야 채택 장벽이 내려간다.
"체감상 빠르다"는 증거가 아니다. 숫자가 증거다.

**Inkline에서 측정할 것:**

| 시나리오 | 비교 대상 | 측정 항목 |
|---------|----------|----------|
| 단일 라인 렌더링 | `TextDecoration.Underline` vs `Inkline` | 프레임 시간 (ms) |
| 멀티라인 (10줄) | 동일 | 프레임 시간 |
| 스타일별 | Solid vs Wavy | 프레임 시간 |
| 메모리 | Inkline 인스턴스 생성 | 할당 바이트 |

- 경로: `benchmark/` 모듈 (별도 모듈)
- 도구: [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview) + [Microbenchmark](https://developer.android.com/topic/performance/benchmarking/microbenchmark-overview)
- 기준: 네이티브 대비 **2x 이내** 오버헤드
- 결과: `docs/benchmark-results.md`에 수치 기록, README에 요약
- 누가 하나: Coil, Accompanist, AndroidX. 네이티브 대체제 라이브러리는 필수.

---

### B-2. Baseline Profiles

**이게 뭔지:**
앱이 설치될 때 "이 코드 경로는 자주 실행되니까 미리 기계어로 컴파일해둬"라고 ART에 알려주는 프로필 파일.
보통 Android 앱은 첫 실행 시 인터프리터로 시작 → 자주 쓰는 코드를 JIT 컴파일 → 나중에 AOT 컴파일하는 과정을 거친다.
Baseline Profile이 있으면 설치 직후부터 AOT 컴파일된 상태로 시작한다.

**왜 하는지:**
첫 렌더링 성능이 좋아진다. 특히 콜드 스타트에서 차이가 크다.
라이브러리가 Baseline Profile을 제공하면, 그 라이브러리를 쓰는 앱의 시작 성능이 개선된다.

**Inkline에서의 적용:**
Inkline은 렌더링 코드가 작아서(drawLine 몇 줄) 효과가 크지 않을 수 있다.
API가 커지고 Wavy path 계산 등 복잡한 로직이 늘어나면 도입 검토.

- 도구: [Baseline Profile Gradle Plugin](https://developer.android.com/topic/performance/baselineprofiles/overview)
- 누가 하나: Coil, Now in Android, AndroidX. 대규모 라이브러리 위주.
- **Inkline 우선순위: 나중에** — 현재 코드 규모에서는 오버엔지니어링.

---

### B-3. Compose Compiler Metrics

**이게 뭔지:**
Compose 컴파일러가 생성하는 안정성(stability) 리포트.
각 클래스와 함수가 Compose 관점에서 "stable"인지 "unstable"인지 보여준다.
Stable한 파라미터만 받는 composable은 입력이 안 바뀌면 recomposition을 건너뛴다(skip).
Unstable하면 부모가 recompose될 때 매번 같이 recompose된다.

**왜 하는지:**
Inkline의 `rememberInkline`이 불필요한 recomposition을 일으키면 성능이 나빠진다.
Compose Compiler Metrics로 "Inkline 클래스들이 stable입니다"를 증명하면,
사용자가 "이 라이브러리 붙이면 recomposition 늘어나는 거 아니야?" 걱정을 안 해도 된다.

**Inkline에서의 적용:**

| 확인 대상 | 기대 결과 |
|----------|----------|
| `Inkline` | stable (internal state만 보유) |
| `UnderlineConfig` | stable (data class, 모든 필드 immutable) |
| `InklineStyle` | stable (enum) |

- 도구: Compose Compiler에 `-P plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=<path>` 플래그
- 출력: `inkline_release-classes.txt`, `inkline_release-composables.txt`
- 기준: public 클래스 전부 stable
- 누가 하나: Cash App(Molecule), Slack(Circuit). 성능에 민감한 Compose 라이브러리.

---

### B-4. Performance Regression CI

**이게 뭔지:**
벤치마크를 한 번 돌리고 끝내는 게 아니라, **커밋마다 벤치마크를 돌려서 결과를 추적**하는 시스템.
시간 경과에 따른 성능 그래프를 생성하고, 성능이 기준 이상으로 떨어지면 CI를 실패시킨다.

**왜 하는지:**
"v0.2.0에서 Wavy 스타일이 20% 느려졌다" — 벤치마크를 매번 돌리지 않으면 이걸 모른다.
수동으로 비교하는 건 까먹기 마련이고, 자동화해야 진짜 지켜진다.

**Inkline에서의 적용:**
현재는 API가 작아서 성능 변동 가능성이 낮다.
기능이 추가되고(overline, strikethrough, animation 등) 코드가 복잡해지면 도입.

- 도구: [github-action-benchmark](https://github.com/benchmark-action/github-action-benchmark) 또는 자체 스크립트
- 누가 하나: AndroidX, Coil. 대규모 라이브러리.
- **Inkline 우선순위: 나중에** — B-1 벤치마크 결과를 수동 기록하는 것부터 시작.

---

## Phase C — 안정성

검증 목표: **"업데이트해도 안 깨진다"**

---

### C-1. Code Coverage (Kover)

**이게 뭔지:**
테스트가 소스 코드의 몇 %를 실행했는지 측정하는 도구.
"테스트가 있다"와 "테스트가 충분하다"는 다르다 — 커버리지는 후자를 수치화한다.
Kover는 Kotlin 공식 커버리지 도구로, JaCoCo보다 Kotlin에 최적화되어 있다.

**왜 하는지:**
커버리지 숫자 자체보다 **커버리지 하락 방지(ratchet)**가 핵심.
"새 기능 추가했는데 테스트 안 짰네" → CI가 잡아준다.
README 배지로 "이 라이브러리는 테스트 커버리지 85%"를 보여주면 신뢰도가 올라간다.

**Inkline에서의 적용:**
- 도구: [Kover](https://github.com/Kotlin/kotlinx-kover) (Kotlin 공식)
- 기준: 라인 커버리지 **80%+**
- CI 연동: Codecov 또는 Coveralls에 리포트 업로드 → README 배지
- 규칙: 커버리지 하락 시 CI 실패 (ratchet)
- 누가 하나: 대부분의 성숙한 오픈소스. 배지 달기는 거의 표준.

---

### C-2. Binary Compatibility Validator

**이게 뭔지:**
라이브러리의 public API를 텍스트 파일(`.api`)로 덤프해두고,
코드 변경 시 이 덤프와 현재 코드를 비교해서 **의도치 않은 API 변경을 감지**하는 도구.
JetBrains가 만든 Kotlin 공식 도구. kotlinx 라이브러리들이 전부 사용한다.

**왜 하는지:**
라이브러리는 API가 곧 계약이다. 함수 이름을 바꾸거나, 파라미터를 추가하거나, 클래스를 제거하면
그 라이브러리에 의존하는 모든 프로젝트(WDS 포함)의 빌드가 깨진다.
Binary Compatibility Validator는 이런 breaking change를 PR 단계에서 잡아준다.
"실수로 public 함수 이름 바꿨는데 WDS 빌드가 깨졌다" → 이걸 방지.

**동작 원리:**
1. `./gradlew apiDump` → `inkline/api/inkline.api` 파일 생성 (현재 API 스냅샷)
2. 코드 변경 후 `./gradlew apiCheck` → 스냅샷과 비교
3. API가 달라졌으면 빌드 실패 → 의도된 변경이면 `apiDump`로 업데이트

**Inkline에서의 적용:**
- 도구: [kotlinx-binary-compatibility-validator](https://github.com/Kotlin/binary-compatibility-validator) (JetBrains 공식)
- 파일: `inkline/api/inkline.api` (자동 생성 API dump)
- CI 연동: `apiCheck` 태스크 — dump와 현재 코드 불일치 시 빌드 실패
- 누가 하나: kotlinx 전체, Molecule, Ktor, OkHttp. 라이브러리라면 필수급.

---

### C-3. Static Analysis (Detekt)

**이게 뭔지:**
코드를 실행하지 않고 소스 코드 자체를 분석해서 잠재적 문제를 찾는 도구.
"이 함수가 너무 길다", "이 when에 else가 없다", "이 변수 이름이 컨벤션에 안 맞다" 같은 것을 자동으로 잡아낸다.
Spotless/ktlint가 포맷팅(들여쓰기, 줄바꿈)을 잡는다면, Detekt는 코드 품질(복잡도, 냄새)을 잡는다.

**왜 하는지:**
Spotless(이미 설정됨)는 "코드가 예쁜지"를 본다. Detekt는 "코드가 건강한지"를 본다.
Compose 전용 룰셋(`detekt-compose-rules`)을 추가하면 Compose 안티패턴도 잡아준다.
예: "Modifier 파라미터가 기본값 없이 선언됨", "remember 안에서 사이드이펙트"

**Inkline에서의 적용:**
- 도구: [Detekt](https://detekt.dev/)
- 추가 룰: [detekt-compose-rules](https://github.com/mrmans0n/compose-rules) (Compose 전용)
- 기준: 0 issues
- CI 연동: PR에서 Detekt 리포트
- 누가 하나: 대부분의 Kotlin 프로젝트. Spotless와 함께 쓰는 게 일반적.

---

### C-4. Custom Lint Rules

**이게 뭔지:**
Android Lint는 코드의 잠재적 문제를 찾는 정적 분석 도구인데,
라이브러리가 **자체 Lint 규칙을 만들어서 AAR에 포함**시킬 수 있다.
라이브러리를 사용하는 개발자의 IDE에서 실시간 경고가 뜬다.

**왜 하는지:**
예를 들어, 개발자가 `textDecoration = TextDecoration.Underline`을 쓰면:
> "⚠️ TextDecoration.Underline 대신 Inkline을 사용하세요. offset과 thickness를 커스텀할 수 있습니다."

이런 경고가 IDE에 바로 뜬다. 채택을 유도하는 동시에 올바른 사용법을 알려주는 강력한 도구.
Dagger(Google), Retrofit(Square) 등 유명 라이브러리들이 이 방식을 쓴다.

**Inkline에서의 적용:**
현재는 API가 단순해서 잘못 쓸 여지가 적다.
overline, strikethrough 등 기능이 추가되면 "이렇게 쓰면 안 돼요" 규칙이 필요해진다.

- 도구: Android Lint API (`com.android.tools.lint:lint-api`)
- 경로: `inkline-lint/` 모듈 (별도 모듈)
- 누가 하나: Dagger, Retrofit, Accompanist. 대규모 라이브러리.
- **Inkline 우선순위: 나중에** — API가 커진 후 도입.

---

## Phase D — 문서 & 배포

검증 목표: **"가져다 쓸 수 있다"**

---

### D-1. API Docs (Dokka)

**이게 뭔지:**
Kotlin/Java 소스 코드의 KDoc 주석을 읽어서 HTML API 문서 사이트를 자동 생성하는 도구.
JavaDoc의 Kotlin 버전. JetBrains 공식.
생성된 HTML을 GitHub Pages에 배포하면 `https://glossybigbro.github.io/inkline/` 같은 문서 사이트가 된다.

**왜 하는지:**
README는 "시작하는 법"을 알려주고, API Docs는 "모든 것의 상세"를 알려준다.
`rememberInkline`의 파라미터가 뭔지, `InklineStyle`에 뭐가 있는지 —
GitHub 소스를 뒤지지 않고 문서 사이트에서 바로 확인할 수 있다.
채택률에 직결. "문서 없는 라이브러리는 안 쓴다"는 개발자가 많다.

**Inkline에서의 적용:**
- 도구: [Dokka](https://github.com/Kotlin/dokka) (Kotlin 공식)
- 출력: HTML → GitHub Pages 자동 배포
- 기준: 모든 public API에 KDoc 작성
- CI 연동: main push 시 GitHub Pages 자동 배포
- 누가 하나: Coil, Ktor, kotlinx 전체. 성숙한 라이브러리의 표준.

---

### D-2. Proguard / R8 Consumer Rules

**이게 뭔지:**
Android 앱을 릴리즈 빌드하면 R8(구 Proguard)이 코드를 **난독화**하고 **사용하지 않는 코드를 제거**한다.
문제는, R8이 라이브러리 코드도 난독화/제거 대상으로 본다는 것.
Consumer Rules는 "이 클래스는 건드리지 마세요"라는 규칙 파일로, AAR에 포함되어 사용자 앱에 자동 적용된다.

**왜 하는지:**
WDS가 Inkline을 사용하고 릴리즈 빌드를 하면, R8이 Inkline의 public API를 난독화하거나 제거할 수 있다.
→ 런타임 크래시. Consumer Rules가 이걸 방지한다.
**라이브러리 배포 시 필수.** 이게 없으면 "디버그에선 되는데 릴리즈에서 크래시" 이슈가 생긴다.

**Inkline에서의 적용:**
- 파일: `inkline/consumer-rules.pro`
- 내용: public API 클래스 keep rule
- 검증: 샘플 앱 릴리즈 빌드(`minifyEnabled = true`)로 동작 확인
- 누가 하나: 모든 Android 라이브러리. Timber, Coil, OkHttp 전부.

---

### D-3. Maven Central 배포

**이게 뭔지:**
Maven Central은 Java/Kotlin 라이브러리의 **공식 중앙 저장소**.
`implementation("io.github.glossybigbro:inkline:1.0.0")` 한 줄로 누구나 가져다 쓸 수 있게 된다.
GitHub Packages나 JitPack과 달리 별도 repository 설정이 필요 없다 — Gradle이 기본으로 참조한다.

**왜 하는지:**
Maven Central에 없으면 사용자가 `repositories { maven("https://...") }`를 추가해야 한다.
이 한 단계가 채택률을 크게 떨어뜨린다. "build.gradle에 한 줄 추가"가 최선.
오픈소스 라이브러리의 사실상 필수 배포 채널.

**Inkline에서의 적용:**
- 도구: vanniktech maven-publish (이미 설정됨)
- 좌표: `io.github.glossybigbro:inkline`
- 절차: Sonatype OSSRH 계정 → GPG 서명 → staging → Maven Central sync
- 검증: 새 프로젝트에서 `implementation("io.github.glossybigbro:inkline:x.x.x")` 의존성 추가 확인
- 누가 하나: 모든 공개 라이브러리. 이게 없으면 "공개"가 아닌 셈.

---

### D-4. Size Analysis

**이게 뭔지:**
라이브러리가 앱에 추가하는 **바이너리 크기(AAR/DEX)**와 **메서드 수**를 측정·추적하는 것.
Android 앱은 DEX 메서드 65K 제한(멀티덱스 전)이 있고, 앱 크기는 다운로드율에 직접 영향을 미친다.

**왜 하는지:**
"이 라이브러리 추가하면 앱이 몇 KB 커지나요?" — 채택 결정에 영향을 주는 질문.
"Inkline은 앱 크기에 ~15KB만 추가합니다"를 README에 쓸 수 있으면 신뢰가 올라간다.
버전 올라갈 때마다 크기가 불어나는 것도 감지할 수 있다.

**Inkline에서의 적용:**
- 도구: [Android Size Analyzer](https://developer.android.com/topic/performance/reduce-apk-size) 또는 `./gradlew assembleRelease` 후 AAR 크기 직접 측정
- 측정 항목: AAR 파일 크기, DEX 메서드 수
- 기준: README에 크기 명시
- 누가 하나: Square(OkHttp), Google. 크기에 민감한 라이브러리.

---

## Phase E — 거버넌스

검증 목표: **"기여할 수 있고, 관리되고 있다"**

---

### E-1. CONTRIBUTING.md

**이게 뭔지:**
외부 기여자가 이 프로젝트에 코드를 기여하려면 어떻게 해야 하는지 안내하는 문서.
빌드 방법, 테스트 실행법, PR 규칙, 코드 스타일 등을 정리한다.
GitHub는 이 파일이 있으면 Issue/PR 작성 시 자동으로 링크를 보여준다.

**왜 하는지:**
"기여하고 싶은데 어떻게 시작하죠?" → 이 질문에 대한 답이 없으면 기여자가 이탈한다.
또한 Issue Template, PR Template을 함께 만들면 일관된 형식으로 소통할 수 있다.

**Inkline에서의 적용:**
- 파일: `CONTRIBUTING.md`, `.github/ISSUE_TEMPLATE/`, `.github/PULL_REQUEST_TEMPLATE.md`
- 내용: 빌드 방법, 테스트 실행, 커밋 컨벤션(Conventional Commits), PR 규칙
- 누가 하나: 기여자를 받는 모든 오픈소스.

---

### E-2. Code of Conduct

**이게 뭔지:**
프로젝트 커뮤니티의 행동 규범.
"우리 프로젝트에서는 이런 행동을 기대하고, 이런 행동은 허용하지 않습니다"를 명시한다.
Contributor Covenant가 업계 표준 — Linux, Kubernetes, Swift 등이 채택.

**왜 하는지:**
GitHub는 이 파일이 있는 프로젝트에 "Community Standards" 배지를 부여한다.
실질적 효과보다 **"이 프로젝트는 제대로 관리되고 있다"**는 신호.
빅테크 오픈소스는 예외 없이 포함한다.

**Inkline에서의 적용:**
- 파일: `CODE_OF_CONDUCT.md`
- 내용: [Contributor Covenant v2.1](https://www.contributor-covenant.org/version/2/1/code_of_conduct/) 채택
- 누가 하나: 사실상 모든 오픈소스 프로젝트.

---

### E-3. Migration Guide

**이게 뭔지:**
메이저 버전 업(v1 → v2) 시 기존 사용자가 코드를 어떻게 바꿔야 하는지 안내하는 문서.
"이 함수 이름이 바뀌었습니다", "이 파라미터가 제거되었습니다" 같은 변경사항과 대응 방법을 정리.

**왜 하는지:**
WDS가 Inkline v1을 쓰고 있는데 v2로 올려야 할 때,
마이그레이션 가이드가 없으면 CHANGELOG를 하나하나 읽으면서 파악해야 한다.
가이드가 있으면 기계적으로 따라 하면 된다.

**Inkline에서의 적용:**
현재 v0.x — API가 아직 불안정하고 변경이 잦은 시기.
v1.0 안정 릴리즈 이후, 메이저 버전 업이 생길 때 작성.

- 경로: `docs/migration/`
- 누가 하나: Coil(v1→v2→v3 가이드), OkHttp, Retrofit 등.
- **Inkline 우선순위: 나중에** — v1.0 이후.

---

### E-4. Badge Wall

**이게 뭔지:**
README 상단에 CI 상태, 커버리지, Maven Central 버전, 라이선스 등을 배지(shield)로 보여주는 것.
shields.io에서 자동 생성되며, 클릭하면 해당 서비스로 이동한다.

**왜 하는지:**
README를 열었을 때 첫 인상이 결정된다.
배지가 있으면: "CI 돌아가고, 테스트 커버리지 85%고, Maven Central에 배포되어 있고, 라이선스 명확하다"
→ 5초 안에 "이 라이브러리는 관리되고 있다"를 전달.

**Inkline에서의 적용:**

| 배지 | 출처 | 선행 조건 |
|------|------|----------|
| CI | GitHub Actions | 이미 있음 ✅ |
| Coverage | Codecov / Coveralls | C-1 완료 후 |
| Maven Central | shields.io | D-3 완료 후 |
| API Docs | GitHub Pages | D-1 완료 후 |
| License | shields.io | 이미 있음 ✅ |

- 누가 하나: 거의 모든 오픈소스. Coil, Timber, OkHttp 전부.

---

### E-5. SECURITY.md

**이게 뭔지:**
보안 취약점을 발견했을 때 어떻게 신고해야 하는지 안내하는 문서.
"이메일로 보내세요, public issue로 올리지 마세요" 같은 절차를 정의한다.
GitHub는 이 파일이 있으면 "Security Policy" 탭을 활성화한다.

**왜 하는지:**
보안 이슈를 public issue로 올리면 패치 전에 모두에게 공개된다.
SECURITY.md가 있으면 비공개 채널(이메일)로 먼저 보고받고, 패치 후 공개할 수 있다.
GitHub의 "Community Standards" 점수에도 반영된다.

**Inkline에서의 적용:**
- 파일: `SECURITY.md`
- 내용: 보안 이슈 보고 이메일, 대응 절차, 지원 버전 범위
- 누가 하나: 빅테크 오픈소스 전부. GitHub이 이걸 기준으로 보안 정책 유무를 판단.

---

## Phase F — 공급망 & 아키텍처

검증 목표: **"안전하고, 미래가 있다"**

---

### F-1. Dependabot / Renovate

**이게 뭔지:**
프로젝트의 의존성(라이브러리, 플러그인)을 자동으로 모니터링해서,
새 버전이 나오거나 보안 취약점이 발견되면 **자동으로 업데이트 PR을 생성**하는 봇.
Dependabot은 GitHub 내장, Renovate는 서드파티(더 유연).

**왜 하는지:**
"Compose BOM 새 버전 나왔는데 호환성 괜찮나?" — 사람이 매번 체크하면 까먹는다.
Dependabot이 PR을 올리면 CI가 빌드를 돌려서 호환성을 자동 검증한다.
보안 취약점(CVE)이 발견된 의존성도 즉시 알려준다.

**Inkline에서의 적용:**
- 도구: [Dependabot](https://docs.github.com/en/code-security/dependabot) (GitHub 내장)
- 파일: `.github/dependabot.yml`
- 설정: Gradle 의존성 + GitHub Actions 버전 모니터링
- 작업량: 설정 파일 하나 추가하면 끝. 코드 작업 아님.
- 누가 하나: GitHub에 있는 거의 모든 오픈소스. 켜기만 하면 되니까.

---

### F-2. Reproducible Builds

**이게 뭔지:**
같은 소스 코드로 빌드하면 **바이트 단위로 동일한 바이너리**가 나오는 것을 보장하는 것.
보통 빌드 결과물에는 타임스탬프, 빌드 경로 같은 비결정적 요소가 포함되어 매번 바이너리가 달라진다.
Reproducible Builds는 이런 요소를 제거해서 "이 AAR이 이 소스에서 나왔다"를 누구나 검증할 수 있게 한다.

**왜 하는지:**
공급망 보안(supply chain security). Maven Central에 올라간 AAR이 정말 GitHub 소스에서 빌드된 건지 증명.
악의적 코드가 빌드 과정에서 삽입되었는지 제3자가 검증할 수 있다.
SLSA(Supply-chain Levels for Software Artifacts) 프레임워크의 핵심 요구사항.

**Inkline에서의 적용:**
현재는 사용자 수가 적어서 공급망 공격 대상이 될 가능성이 낮다.
Maven Central 배포 후, 다운로드가 늘어나면 도입 검토.

- 도구: [Gradle Reproducible Builds](https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives) + [Reproducible Central](https://github.com/jvm-repo-rebuild/reproducible-central)
- 누가 하나: Apache 프로젝트, Google(Guava). 대규모 배포 라이브러리.
- **Inkline 우선순위: 나중에** — Maven Central 배포 + 사용자 증가 후.

---

### F-3. KMP Readiness

**이게 뭔지:**
Kotlin Multiplatform(KMP) — 하나의 Kotlin 코드베이스로 Android, iOS, Desktop, Web을 동시에 지원하는 기술.
KMP Readiness는 "현재 코드가 KMP로 전환 가능한 구조인가"를 점검하는 것.
JetBrains가 Compose Multiplatform을 밀고 있어서, Compose 라이브러리의 KMP 전환이 트렌드.

**왜 하는지:**
Coil 3, Ktor, kotlinx-serialization 등 주요 라이브러리가 KMP으로 전환 중.
Inkline의 핵심 로직(offset 계산, path 생성)은 플랫폼 독립적이라 KMP 전환 가능성이 높다.
지금 전환하는 게 아니라, **Android 전용 코드(`android.*`)를 최소화**하는 구조적 준비.

**Inkline에서의 적용:**
현재 Inkline은 `androidx.compose.ui`에만 의존 — Compose Multiplatform과 호환 가능성 높음.
`android.*` import가 없으면 KMP 전환 시 코드 변경이 최소화된다.

- 점검 항목: `android.*` import 유무, 플랫폼 의존 코드 분리
- 누가 하나: Coil 3(KMP 전환 완료), Ktor, CashApp(Molecule).
- **Inkline 우선순위: 나중에** — v1.0 안정 후 검토. 현재는 구조만 의식.

---

### F-4. SBOM (Software Bill of Materials)

**이게 뭔지:**
소프트웨어에 포함된 모든 구성 요소(의존성, 라이선스, 버전)를 기계가 읽을 수 있는 형식으로 나열한 명세서.
식품의 성분표와 같다. "이 라이브러리는 Compose UI 1.7.0, Kotlin 2.3.20을 사용합니다."
CycloneDX, SPDX 같은 표준 포맷이 있다.

**왜 하는지:**
미국 행정명령(EO 14028)으로 소프트웨어 공급망 투명성이 요구되기 시작했다.
엔터프라이즈 고객(대기업, 정부기관)은 사용하는 라이브러리의 SBOM을 요구하는 경우가 늘고 있다.
"이 라이브러리가 쓰는 의존성 중에 보안 취약점 있는 게 있나?" — SBOM으로 자동 스캔 가능.

**Inkline에서의 적용:**
현재는 의존성이 Compose UI, Foundation 두 개뿐이라 SBOM의 실질적 가치가 낮다.
의존성이 늘어나거나 엔터프라이즈 채택이 생기면 도입.

- 도구: [CycloneDX Gradle Plugin](https://github.com/CycloneDX/cyclonedx-gradle-plugin)
- 출력: `bom.json` (CycloneDX 포맷)
- 누가 하나: 대규모 엔터프라이즈 라이브러리. Spring, Quarkus 등.
- **Inkline 우선순위: 나중에** — 엔터프라이즈 채택 시.

---

## 실행 순서

Inkline 규모(파일 6개)에 맞게 단계적으로 도입한다.

### 지금 (v0.2.0)

필수 항목 — 라이브러리 최소 신뢰도 확보.

```
A-1  Unit Tests
A-3  Screenshot Tests
B-1  Benchmark
C-2  Binary Compatibility Validator
D-2  Proguard / R8 Rules
D-3  Maven Central
```

### 다음 (v0.3.0~)

권장 항목 — 품질 고도화.

```
A-2  Compose UI Tests
A-4  CI Matrix
B-3  Compose Compiler Metrics
C-1  Code Coverage (Kover)
C-3  Static Analysis (Detekt)
D-1  API Docs (Dokka)
D-4  Size Analysis
E-1  CONTRIBUTING.md
E-2  Code of Conduct
E-4  Badge Wall
E-5  SECURITY.md
F-1  Dependabot / Renovate
```

### API 확장 후 (v1.0+)

나중에 항목 — 규모가 커졌을 때 도입.

```
B-2  Baseline Profiles
B-4  Performance Regression CI
C-4  Custom Lint Rules
E-3  Migration Guide
F-2  Reproducible Builds
F-3  KMP Readiness
F-4  SBOM
```

---

## 참고 라이브러리

이 계획은 아래 오픈소스 라이브러리의 품질 체계를 참고했다:

| 라이브러리 | 조직 | 참고 항목 |
|-----------|------|----------|
| [Coil](https://github.com/coil-kt/coil) | coil-kt | 테스트 구조, Benchmark, Dokka, Baseline Profile |
| [Accompanist](https://github.com/google/accompanist) | Google | Screenshot test, API tracking, Custom Lint |
| [Molecule](https://github.com/cashapp/molecule) | Cash App | Binary compatibility, Compose Compiler Metrics |
| [Timber](https://github.com/JakeWharton/timber) | Jake Wharton | Consumer rules, Maven Central, 소형 라이브러리 기준 |
| [Telephoto](https://github.com/saket/telephoto) | Saket Narayan | Compose 라이브러리 품질 기준 |
| [OkHttp](https://github.com/square/okhttp) | Square | Size analysis, Migration guide |
| [Circuit](https://github.com/slackhq/circuit) | Slack | Compose Compiler Metrics, Detekt |
