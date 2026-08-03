package dev.ashenarx.tools.fleet.ui

import com.intellij.testFramework.common.timeoutRunBlocking
import com.intellij.testFramework.junit5.TestApplication
import dev.ashenarx.tools.fleet.ToolWindowItem
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

@TestApplication
class ToolWindowsPopupViewModelTest {

    @Test
    fun `active items precede recent and new items`() {
        val new = item("new")
        val recent = item("recent", hasBeenOpened = true)
        val active = item("active", isVisible = true)
        val viewModel = ToolWindowsPopupViewModel(listOf(new, recent, active))

        val state = viewModel.uiState.value

        assertEquals(listOf(active), state.activeItems)
        assertEquals(listOf(recent), state.recentItems)
        assertEquals(listOf(new), state.newItems)
        assertEquals(listOf("active", "recent", "new"), state.selectableItems.map(ToolWindowItem::id))
    }

    @Test
    fun `navigation skips unavailable items and does not wrap`() = timeoutRunBlocking {
        val active = item("active", isVisible = true)
        val unavailable = item("unavailable", isAvailable = false)
        val available = item("available")
        val viewModel = ToolWindowsPopupViewModel(listOf(unavailable, available, active))

        viewModel.moveSelection(1)
        assertEquals("available", viewModel.uiState.first { it.selectedId == "available" }.selectedId)

        viewModel.moveSelection(1)
        assertEquals("available", viewModel.uiState.value.selectedId)

        viewModel.moveSelection(-1)
        assertEquals("active", viewModel.uiState.first { it.selectedId == "active" }.selectedId)
    }

    @Test
    fun `search resets selection to the first result`() = timeoutRunBlocking {
        val alpha = item("alpha", title = "Alpha")
        val terminal = item("terminal", title = "Terminal")
        val viewModel = ToolWindowsPopupViewModel(listOf(alpha, terminal))
        viewModel.moveSelection(1)
        assertEquals("terminal", viewModel.uiState.first { it.selectedId == "terminal" }.selectedId)

        viewModel.setQuery("alpha")

        val state = viewModel.uiState.first { it.newItems == listOf(alpha) }
        assertEquals("alpha", state.selectedId)
    }

    @Test
    fun `closing a visible item moves it to recent and selects the next active item`() = timeoutRunBlocking {
        val first = item("first", isVisible = true)
        val second = item("second", isVisible = true)
        val inactive = item("inactive")
        val viewModel = ToolWindowsPopupViewModel(listOf(first, second, inactive))

        assertEquals("first", viewModel.closeSelected())

        val state = viewModel.uiState.first { it.recentItems.any { item -> item.id == "first" } }
        assertEquals(listOf(second), state.activeItems)
        assertEquals(listOf("first"), state.recentItems.map(ToolWindowItem::id))
        assertFalse(state.recentItems.first().isVisible)
        assertEquals(listOf(inactive), state.newItems)
        assertEquals("second", state.selectedId)
    }

    @Test
    fun `closing is ignored for a selected new item`() = timeoutRunBlocking {
        val active = item("active", isVisible = true)
        val inactive = item("inactive")
        val viewModel = ToolWindowsPopupViewModel(listOf(active, inactive))
        viewModel.moveSelection(1)
        viewModel.uiState.first { it.selectedId == "inactive" }

        assertNull(viewModel.closeSelected())
        assertEquals(listOf(active), viewModel.uiState.value.activeItems)
    }

    private fun item(
        id: String,
        title: String = id,
        isVisible: Boolean = false,
        hasBeenOpened: Boolean = isVisible,
        isAvailable: Boolean = true,
    ) = ToolWindowItem(
        id = id,
        title = title,
        icon = null,
        isVisible = isVisible,
        hasBeenOpened = hasBeenOpened,
        isAvailable = isAvailable,
    )
}
