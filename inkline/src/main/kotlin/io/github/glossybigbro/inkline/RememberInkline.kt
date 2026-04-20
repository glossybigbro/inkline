package io.github.glossybigbro.inkline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Inkline 인스턴스를 생성하고 remember한다.
 *
 * ```kotlin
 * val inkline = rememberInkline {
 *     underline(offset = 4.dp, thickness = 1.5.dp, color = Color.Blue)
 * }
 * ```
 */
@Composable
fun rememberInkline(block: InklineScope.() -> Unit): Inkline {
    return remember(block) {
        val scope = InklineScopeImpl()
        scope.block()
        Inkline(scope.configs.toList())
    }
}
