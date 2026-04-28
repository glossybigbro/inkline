package io.github.glossybigbro.inkline

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import kotlin.math.sin

/**
 * Custom underline drawing engine for Jetpack Compose.
 *
 * Connect to any [Text] composable with 3 hooks:
 * 1. `inkline.apply(text)` — convert to [AnnotatedString]
 * 2. `inkline::onTextLayout` — capture layout info
 * 3. `Modifier.drawBehind(inkline)` — draw underlines
 *
 * ```kotlin
 * val inkline = rememberInkline {
 *     underline(offset = 4.dp, color = Color.Blue)
 * }
 *
 * Text(
 *     text = inkline.apply("Hello"),
 *     modifier = Modifier.drawBehind(inkline),
 *     onTextLayout = inkline::onTextLayout,
 * )
 * ```
 *
 * @see rememberInkline
 * @see drawBehind
 */
@Stable
class Inkline internal constructor(
    private val configs: List<UnderlineConfig>,
) {
    private var layoutResult: TextLayoutResult? = null

    /**
     * Captures the [TextLayoutResult] from `Text`'s `onTextLayout` callback.
     * Must be connected for underlines to render.
     */
    fun onTextLayout(result: TextLayoutResult) {
        layoutResult = result
    }

    /**
     * Converts a plain [String] to an [AnnotatedString] for use with `Text`.
     */
    fun apply(text: String): AnnotatedString = buildAnnotatedString { append(text) }

    /**
     * Passes through an existing [AnnotatedString] unchanged.
     * Native [TextDecoration.Underline] is not stripped — Inkline draws on a separate layer.
     */
    fun apply(text: AnnotatedString): AnnotatedString = text

    /**
     * Draws all configured underlines on the [DrawScope] canvas.
     * Called internally by [Modifier.drawBehind].
     */
    fun DrawScope.draw() {
        val layout = layoutResult ?: return

        for (config in configs) {
            val offsetPx = config.offset.toPx()
            val thicknessPx = config.thickness.toPx()
            val lineColor =
                if (config.color == Color.Unspecified) {
                    Color.Black
                } else {
                    config.color
                }

            for (line in 0 until layout.lineCount) {
                val lineLeft = layout.getLineLeft(line)
                val lineRight = layout.getLineRight(line)
                val lineBottom = layout.getLineBottom(line)
                val y = lineBottom + offsetPx

                when (config.style) {
                    InklineStyle.Solid -> {
                        drawLine(
                            color = lineColor,
                            start = Offset(lineLeft, y),
                            end = Offset(lineRight, y),
                            strokeWidth = thicknessPx,
                        )
                    }

                    InklineStyle.Dashed -> {
                        val dashLength = thicknessPx * 6
                        val gapLength = thicknessPx * 3
                        drawLine(
                            color = lineColor,
                            start = Offset(lineLeft, y),
                            end = Offset(lineRight, y),
                            strokeWidth = thicknessPx,
                            pathEffect =
                                PathEffect.dashPathEffect(
                                    floatArrayOf(dashLength, gapLength),
                                ),
                        )
                    }

                    InklineStyle.Dotted -> {
                        val dotLength = thicknessPx
                        val gapLength = thicknessPx * 2
                        drawLine(
                            color = lineColor,
                            start = Offset(lineLeft, y),
                            end = Offset(lineRight, y),
                            strokeWidth = thicknessPx,
                            pathEffect =
                                PathEffect.dashPathEffect(
                                    floatArrayOf(dotLength, gapLength),
                                ),
                        )
                    }

                    InklineStyle.Wavy -> {
                        val wavePath =
                            buildWavyPath(
                                startX = lineLeft,
                                endX = lineRight,
                                y = y,
                                amplitude = thicknessPx * 1.5f,
                                wavelength = thicknessPx * 8f,
                            )
                        drawPath(
                            path = wavePath,
                            color = lineColor,
                            style = Stroke(width = thicknessPx),
                        )
                    }
                }
            }
        }
    }

    private fun buildWavyPath(
        startX: Float,
        endX: Float,
        y: Float,
        amplitude: Float,
        wavelength: Float,
    ): Path {
        val path = Path()
        path.moveTo(startX, y)

        var x = startX
        val step = 1f
        while (x <= endX) {
            val progress = (x - startX) / wavelength * (2 * Math.PI).toFloat()
            val yOffset = sin(progress) * amplitude
            path.lineTo(x, y + yOffset)
            x += step
        }

        return path
    }
}
