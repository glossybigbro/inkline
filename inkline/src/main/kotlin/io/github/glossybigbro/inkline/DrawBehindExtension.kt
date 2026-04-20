package io.github.glossybigbro.inkline

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind

/**
 * Inkline의 밑줄을 텍스트 뒤에 그리는 Modifier 확장.
 *
 * ```kotlin
 * Text(
 *     text = inkline.apply(text),
 *     modifier = Modifier.drawBehind(inkline),
 *     onTextLayout = inkline::onTextLayout,
 * )
 * ```
 */
fun Modifier.drawBehind(inkline: Inkline): Modifier {
    return this.drawBehind {
        with(inkline) { draw() }
    }
}
