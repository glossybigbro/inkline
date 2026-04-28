package io.github.glossybigbro.inkline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class UnderlineConfigTest {
    @Test
    fun `기본값 검증`() {
        val config = UnderlineConfig()

        assertEquals(2.dp, config.offset)
        assertEquals(1.dp, config.thickness)
        assertEquals(Color.Unspecified, config.color)
        assertEquals(InklineStyle.Solid, config.style)
    }

    @Test
    fun `커스텀 값 생성`() {
        val config =
            UnderlineConfig(
                offset = 6.dp,
                thickness = 2.dp,
                color = Color.Blue,
                style = InklineStyle.Wavy,
            )

        assertEquals(6.dp, config.offset)
        assertEquals(2.dp, config.thickness)
        assertEquals(Color.Blue, config.color)
        assertEquals(InklineStyle.Wavy, config.style)
    }

    @Test
    fun `copy로 일부 값만 변경`() {
        val original = UnderlineConfig()
        val copied = original.copy(offset = 8.dp, color = Color.Red)

        assertEquals(8.dp, copied.offset)
        assertEquals(1.dp, copied.thickness)
        assertEquals(Color.Red, copied.color)
        assertEquals(InklineStyle.Solid, copied.style)
    }

    @Test
    fun `동일한 값이면 equals true`() {
        val a = UnderlineConfig(offset = 4.dp, color = Color.Green)
        val b = UnderlineConfig(offset = 4.dp, color = Color.Green)

        assertEquals(a, b)
    }
}
