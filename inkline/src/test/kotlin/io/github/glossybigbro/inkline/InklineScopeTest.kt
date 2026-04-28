package io.github.glossybigbro.inkline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InklineScopeTest {
    @Test
    fun `빈 스코프는 config 0개`() {
        val scope = InklineScopeImpl()

        assertTrue(scope.configs.isEmpty())
    }

    @Test
    fun `underline 하나 등록`() {
        val scope = InklineScopeImpl()
        scope.underline(offset = 4.dp, color = Color.Blue)

        assertEquals(1, scope.configs.size)
        assertEquals(4.dp, scope.configs[0].offset)
        assertEquals(Color.Blue, scope.configs[0].color)
    }

    @Test
    fun `underline 여러 개 등록`() {
        val scope = InklineScopeImpl()
        scope.underline(offset = 2.dp, style = InklineStyle.Solid)
        scope.underline(offset = 4.dp, style = InklineStyle.Dashed)
        scope.underline(offset = 6.dp, style = InklineStyle.Wavy)

        assertEquals(3, scope.configs.size)
        assertEquals(InklineStyle.Solid, scope.configs[0].style)
        assertEquals(InklineStyle.Dashed, scope.configs[1].style)
        assertEquals(InklineStyle.Wavy, scope.configs[2].style)
    }

    @Test
    fun `기본값으로 등록`() {
        val scope = InklineScopeImpl()
        scope.underline()

        assertEquals(1, scope.configs.size)
        assertEquals(UnderlineConfig(), scope.configs[0])
    }
}
