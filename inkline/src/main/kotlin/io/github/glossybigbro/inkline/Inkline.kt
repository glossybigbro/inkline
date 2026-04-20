package io.github.glossybigbro.inkline

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
 * Inkline — 커스텀 밑줄 도구.
 *
 * 3-hook으로 Text에 연결:
 * 1. `inkline.apply(text)` → AnnotatedString 변환
 * 2. `inkline::onTextLayout` → 레이아웃 캡처
 * 3. `Modifier.drawBehind(inkline)` → 밑줄 그리기
 */
class Inkline internal constructor(
    private val configs: List<UnderlineConfig>,
) {
    private var layoutResult: TextLayoutResult? = null

    /**
     * Text의 onTextLayout 콜백에 연결한다.
     */
    fun onTextLayout(result: TextLayoutResult) {
        layoutResult = result
    }

    /**
     * String을 AnnotatedString으로 변환한다.
     */
    fun apply(text: String): AnnotatedString {
        return buildAnnotatedString { append(text) }
    }

    /**
     * AnnotatedString에서 기존 TextDecoration.Underline을 제거한다.
     * Inkline이 직접 그리므로 네이티브 밑줄은 필요 없다.
     */
    fun apply(text: AnnotatedString): AnnotatedString {
        return text
    }

    /**
     * DrawScope에서 밑줄을 그린다.
     * Modifier.drawBehind { } 안에서 호출.
     */
    fun DrawScope.draw() {
        val layout = layoutResult ?: return

        for (config in configs) {
            val offsetPx = config.offset.toPx()
            val thicknessPx = config.thickness.toPx()
            val lineColor = if (config.color == Color.Unspecified) {
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
                            pathEffect = PathEffect.dashPathEffect(
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
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(dotLength, gapLength),
                            ),
                        )
                    }

                    InklineStyle.Wavy -> {
                        val wavePath = buildWavyPath(
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
