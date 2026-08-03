package dev.ashenarx.tools.fleet

import com.intellij.testFramework.junit5.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

@TestApplication
class ToolWindowMatcherTest {

    @Test
    fun `a blank query matches nothing`() {
        assertNull(ToolWindowMatcher("").matchOrNull("Terminal"))
        assertNull(ToolWindowMatcher("   ").matchOrNull("Terminal"))
    }

    @Test
    fun `a query matches in the middle of the text`() {
        assertNotNull(ToolWindowMatcher("min").matchOrNull("Terminal"))
    }

    @Test
    fun `query words match across word boundaries`() {
        assertNotNull(ToolWindowMatcher("run conf").matchOrNull("Run Configurations"))
    }

    @Test
    fun `matching ignores the query case`() {
        assertNotNull(ToolWindowMatcher("TERMINAL").matchOrNull("Terminal"))
        assertNotNull(ToolWindowMatcher("terminal").matchOrNull("Terminal"))
    }

    @Test
    fun `a typo in the query still matches`() {
        assertNotNull(ToolWindowMatcher("termnal").matchOrNull("Terminal"))
    }

    @Test
    fun `an unrelated query does not match`() {
        assertNull(ToolWindowMatcher("zzz").matchOrNull("Terminal"))
    }

    @Test
    fun `highlights cover exactly the matched characters`() {
        val match = ToolWindowMatcher("min").matchOrNull("Terminal")

        assertEquals("min", match?.highlights?.joinToString("") { "Terminal".substring(it) })
    }
}
