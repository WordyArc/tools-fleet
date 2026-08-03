package dev.ashenarx.tools.fleet.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.Dimension

class PopupSizeTest {

    @Test
    fun `the popup always fits inside the window`() {
        val windows = listOf(
            Dimension(400, 300),
            Dimension(800, 600),
            Dimension(1600, 1000),
            Dimension(3000, 2000),
        )

        windows.forEach { window ->
            val size = popupSizeFor(window)
            assertTrue(size.width <= window.width, "width ${size.width} exceeds window $window")
            assertTrue(size.height <= window.height, "height ${size.height} exceeds window $window")
        }
    }

    @Test
    fun `the popup grows with the window`() {
        val small = popupSizeFor(Dimension(900, 700))
        val large = popupSizeFor(Dimension(1600, 1100))

        assertTrue(large.width >= small.width, "$large is narrower than $small")
        assertTrue(large.height >= small.height, "$large is shorter than $small")
    }

    @Test
    fun `the popup stops growing on a very large window`() {
        assertEquals(popupSizeFor(Dimension(3000, 2000)), popupSizeFor(Dimension(6000, 4000)))
    }
}
