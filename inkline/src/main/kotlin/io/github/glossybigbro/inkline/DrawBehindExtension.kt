package io.github.glossybigbro.inkline

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind

/**
 * Draws [inkline] underlines behind the text content.
 *
 * This is the third hook in the 3-hook connection pattern.
 * Pair with [Inkline.apply] and [Inkline.onTextLayout]:
 *
 * ```kotlin
 * Text(
 *     text = inkline.apply(text),
 *     modifier = Modifier.drawBehind(inkline),
 *     onTextLayout = inkline::onTextLayout,
 * )
 * ```
 *
 * @param inkline The [Inkline] instance containing underline configurations.
 * @see Inkline
 * @see rememberInkline
 */
fun Modifier.drawBehind(inkline: Inkline): Modifier =
    this.drawBehind {
        with(inkline) { draw() }
    }
