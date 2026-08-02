package dev.ashenarx.tools.fleet.ui

import com.intellij.testFramework.junit5.TestApplication
import dev.ashenarx.tools.fleet.ToolWindowItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@TestApplication
class ToolWindowsPopupViewModelTest {

    @Test
    fun `active items precede new items`() {
        val inactive = item("inactive")
        val active = item("active", isOpen = true)
        val viewModel = ToolWindowsPopupViewModel(listOf(inactive, active))

        val state = viewModel.uiState.value

        assertEquals(listOf(active), state.activeItems)
        assertEquals(listOf(inactive), state.newItems)
        assertEquals(listOf("active", "inactive"), state.selectableItems.map(ToolWindowItem::id))
    }

    @Test
    fun `navigation skips unavailable items and does not wrap`() {
        val active = item("active", isOpen = true)
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

    private fun item(
        id: String,
        title: String = id,
        isOpen: Boolean = false,
        isAvailable: Boolean = true,
    ) = ToolWindowItem(
        id = id,
        title = title,
        icon = null,
        isOpen = isOpen,
        isAvailable = isAvailable,
    )
}
