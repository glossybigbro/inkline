package io.github.glossybigbro.inkline

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * DSL scope for configuring underline decorations.
 *
 * ```kotlin
 * rememberInkline {
 *     underline(offset = 4.dp, color = Color.Blue)
 * }
 * ```
 */
@Stable
interface InklineScope {
    /**
     * Adds an underline decoration with the given properties.
     *
     * @param offset Gap between the text baseline and the underline.
     * @param thickness Underline stroke width.
     * @param color Underline color. [Color.Unspecified] follows the text color.
     * @param style Visual style of the underline.
     */
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
