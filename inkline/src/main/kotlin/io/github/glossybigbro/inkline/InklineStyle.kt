package io.github.glossybigbro.inkline

import androidx.compose.runtime.Immutable

/**
 * Visual style of the underline.
 */
@Immutable
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
