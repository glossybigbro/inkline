package io.github.glossybigbro.inkline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Inkline DSL 빌더 스코프.
 *
 * ```kotlin
 * rememberInkline {
 *     underline(offset = 4.dp, color = Color.Blue)
 * }
 * ```
 */
interface InklineScope {
    fun underline(
        offset: Dp = 2.dp,
        thickness: Dp = 1.dp,
        color: Color = Color.Unspecified,
        style: InklineStyle = InklineStyle.Solid,
    )
}

internal class InklineScopeImpl : InklineScope {
    val configs = mutableListOf<UnderlineConfig>()

    override fun underline(
        offset: Dp,
        thickness: Dp,
        color: Color,
        style: InklineStyle,
    ) {
        configs += UnderlineConfig(offset, thickness, color, style)
    }
}
