package io.github.glossybigbro.inkline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 밑줄 설정.
 */
data class UnderlineConfig(
    val offset: Dp = 2.dp,
    val thickness: Dp = 1.dp,
    val color: Color = Color.Unspecified,
    val style: InklineStyle = InklineStyle.Solid,
)
