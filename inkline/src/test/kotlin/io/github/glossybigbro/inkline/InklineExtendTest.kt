package io.github.glossybigbro.inkline

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class InklineExtendTest {
    private fun createInkline(): Inkline {
        val scope = InklineScopeImpl()
        scope.underline()
        return Inkline(scope.configs.toList())
    }

    @Test
    fun `extend String은 AnnotatedString으로 변환`() {
        val inkline = createInkline()
        val result = inkline.extend("Hello")

        assertEquals("Hello", result.text)
    }

    @Test
    fun `extend 빈 문자열`() {
        val inkline = createInkline()
        val result = inkline.extend("")

        assertEquals("", result.text)
    }

    @Test
    fun `extend AnnotatedString은 그대로 통과`() {
        val inkline = createInkline()
        val original = AnnotatedString("Hello World")
        val result = inkline.extend(original)

        assertEquals(original, result)
    }

    @Test
    fun `extend 한국어 텍스트`() {
        val inkline = createInkline()
        val result = inkline.extend("대한항공 밑줄 테스트")

        assertEquals("대한항공 밑줄 테스트", result.text)
    }

    @Test
    fun `extend 특수문자 포함`() {
        val inkline = createInkline()
        val result = inkline.extend("f vs e — different offers!")

        assertEquals("f vs e — different offers!", result.text)
    }
}
