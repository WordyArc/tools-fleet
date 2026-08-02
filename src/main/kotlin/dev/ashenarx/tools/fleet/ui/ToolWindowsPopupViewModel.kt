package dev.ashenarx.tools.fleet.ui

import androidx.lifecycle.ViewModel
import dev.ashenarx.tools.fleet.ToolWindowItem
import dev.ashenarx.tools.fleet.ToolWindowMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ToolWindowsPopupViewModel(
    private val items: List<ToolWindowItem>,
) : ViewModel() {

    val uiState: StateFlow<ToolWindowsUiState>
        field: MutableStateFlow<ToolWindowsUiState> = MutableStateFlow(items.toUiState())

    fun setQuery(query: String) {
        uiState.value = items.toUiState(query)
    }

    fun moveSelection(delta: Int) {
        val state = uiState.value
        uiState.value = state.copy(
            selectedId = state.selectableItems.moveSelection(state.selectedId, delta),
        )
    }
}

internal data class ToolWindowsUiState(
    val activeItems: List<ToolWindowItem>,
    val newItems: List<ToolWindowItem>,
    val selectableItems: List<ToolWindowItem>,
    val selectedId: String?,
) {
    val isEmpty: Boolean
        get() = activeItems.isEmpty() && newItems.isEmpty()
}

private fun List<ToolWindowItem>.toUiState(query: String = ""): ToolWindowsUiState {
    val filtered = filterAndRank(query)
    val activeItems = filtered.filter(ToolWindowItem::isOpen)
    val newItems = filtered.filterNot(ToolWindowItem::isOpen)
    val selectableItems = (activeItems + newItems).filter(ToolWindowItem::isAvailable)

    return ToolWindowsUiState(
        activeItems = activeItems,
        newItems = newItems,
        selectableItems = selectableItems,
        selectedId = selectableItems.firstOrNull()?.id,
    )
}

private fun List<ToolWindowItem>.filterAndRank(query: String): List<ToolWindowItem> {
    if (query.isBlank()) return this
    val matcher = ToolWindowMatcher(query)

    return mapNotNull { item -> matcher.degreeOrNull(item.searchText)?.let { degree -> item to degree } }
        .sortedByDescending(Pair<ToolWindowItem, Int>::second)
        .map(Pair<ToolWindowItem, Int>::first)
}

private val ToolWindowItem.searchText: String
    get() = "$title $id"

private fun List<ToolWindowItem>.moveSelection(currentId: String?, delta: Int): String? {
    if (isEmpty()) return null
    val current = indexOfFirst { it.id == currentId }
    if (current < 0) return first().id

    return this[(current + delta).coerceIn(0, lastIndex)].id
}
