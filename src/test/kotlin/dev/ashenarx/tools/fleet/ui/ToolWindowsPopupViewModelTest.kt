package dev.ashenarx.tools.fleet.ui

import com.intellij.testFramework.junit5.TestApplication
import dev.ashenarx.tools.fleet.ToolWindowItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

@TestApplication
class ToolWindowsPopupViewModelTest {

    @Test
    fun `active items precede new items`() {
        val inactive = item("inactive")
        val active = item("active", isVisible = true)
        val viewModel = ToolWindowsPopupViewModel(listOf(inactive, active))

        val state = viewModel.uiState.value

        assertEquals(listOf(active), state.activeItems)
        assertEquals(listOf(inactive), state.newItems)
        assertEquals(listOf("active", "inactive"), state.selectableItems.map(ToolWindowItem::id))
    }

    @Test
    fun `navigation skips unavailable items and does not wrap`() {
        val active = item("active", isVisible = true)
        val unavailable = item("unavailable", isAvailable = false)
        val available = item("available")
        val viewModel = ToolWindowsPopupViewModel(listOf(unavailable, available, active))

        viewModel.moveSelection(1)
        assertEquals("available", viewModel.uiState.value.selectedId)

        viewModel.moveSelection(1)
        assertEquals("available", viewModel.uiState.value.selectedId)

        viewModel.moveSelection(-1)
        assertEquals("active", viewModel.uiState.value.selectedId)
    }

    @Test
    fun `search resets selection to the first result`() {
        val alpha = item("alpha", title = "Alpha")
        val terminal = item("terminal", title = "Terminal")
        val viewModel = ToolWindowsPopupViewModel(listOf(alpha, terminal))
        viewModel.moveSelection(1)
        assertEquals("terminal", viewModel.uiState.value.selectedId)

        viewModel.setQuery("alpha")

        assertEquals(listOf(alpha), viewModel.uiState.value.newItems)
        assertEquals("alpha", viewModel.uiState.value.selectedId)
    }

    @Test
    fun `closing a visible item moves it to new and selects the next active item`() {
        val first = item("first", isVisible = true)
        val second = item("second", isVisible = true)
        val inactive = item("inactive")
        val viewModel = ToolWindowsPopupViewModel(listOf(first, second, inactive))

        assertEquals("first", viewModel.closeSelected())

        val state = viewModel.uiState.value
        assertEquals(listOf(second), state.activeItems)
        assertEquals(listOf("first", "inactive"), state.newItems.map(ToolWindowItem::id))
        assertFalse(state.newItems.first().isVisible)
        assertEquals("second", state.selectedId)
    }

    @Test
    fun `closing is ignored for a selected new item`() {
        val active = item("active", isVisible = true)
        val inactive = item("inactive")
        val viewModel = ToolWindowsPopupViewModel(listOf(active, inactive))
        viewModel.moveSelection(1)

        assertNull(viewModel.closeSelected())
        assertEquals(listOf(active), viewModel.uiState.value.activeItems)
    }

    private fun item(
        id: String,
        title: String = id,
        isVisible: Boolean = false,
        isAvailable: Boolean = true,
    ) = ToolWindowItem(
        id = id,
        title = title,
        icon = null,
        isVisible = isVisible,
        isAvailable = isAvailable,
    )
}
