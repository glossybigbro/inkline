package io.github.glossybigbro.inkline

import org.junit.Assert.assertEquals
import org.junit.Test

class InklineStyleTest {
    @Test
    fun `enum 값 4개 존재`() {
        val values = InklineStyle.entries

        assertEquals(4, values.size)
    }

    @Test
    fun `enum 값 이름 검증`() {
        val names = InklineStyle.entries.map { it.name }

        assertEquals(listOf("Solid", "Dashed", "Dotted", "Wavy"), names)
    }

    @Test
    fun `valueOf로 변환`() {
        assertEquals(InklineStyle.Solid, InklineStyle.valueOf("Solid"))
        assertEquals(InklineStyle.Dashed, InklineStyle.valueOf("Dashed"))
        assertEquals(InklineStyle.Dotted, InklineStyle.valueOf("Dotted"))
        assertEquals(InklineStyle.Wavy, InklineStyle.valueOf("Wavy"))
    }
}
