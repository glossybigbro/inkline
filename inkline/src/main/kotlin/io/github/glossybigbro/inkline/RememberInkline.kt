package io.github.glossybigbro.inkline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Creates and remembers an [Inkline] instance configured by the [block] DSL.
 *
 * ```kotlin
 * val inkline = rememberInkline {
 *     underline(offset = 4.dp, thickness = 1.5.dp, color = Color.Blue)
 * }
 *
 * Text(
 *     text = inkline.extend("Hello"),
 *     modifier = Modifier.drawBehind(inkline),
 *     onTextLayout = inkline::onTextLayout,
 * )
 * ```
 *
 * @param block DSL block to configure underline decorations via [InklineScope].
 * @return A remembered [Inkline] instance ready to connect to a `Text` composable.
 * @see InklineScope.underline
 */
@Composable
fun rememberInkline(block: InklineScope.() -> Unit): Inkline =
    remember(block) {
        val scope = InklineScopeImpl()
        scope.block()
        Inkline(scope.configs.toList())
    }
