package io.github.glossybigbro.inkline

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for a single underline decoration.
 *
 * @param offset Gap between the text baseline and the underline.
 * @param thickness Underline stroke width.
 * @param color Underline color. [Color.Unspecified] follows the text color.
 * @param style Visual style of the underline.
 */
@Immutable
data class UnderlineConfig(
    val offset: Dp = 2.dp,
    val thickness: Dp = 1.dp,
    val color: Color = Color.Unspecified,
    val style: InklineStyle = InklineStyle.Solid,
)
