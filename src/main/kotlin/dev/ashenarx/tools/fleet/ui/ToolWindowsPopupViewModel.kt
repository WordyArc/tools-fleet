package dev.ashenarx.tools.fleet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ashenarx.tools.fleet.ToolWindowItem
import dev.ashenarx.tools.fleet.ToolWindowMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class ToolWindowsPopupViewModel(
    items: List<ToolWindowItem>,
) : ViewModel() {

    private val itemState = MutableStateFlow(items)
    private val query = MutableStateFlow("")
    private val selection = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ToolWindowsUiState> = combine(itemState, query, selection) { items, query, selectedId ->
        items.toUiState(query, selectedId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = itemState.value.toUiState(),
    )

    fun setQuery(query: String) {
        selection.value = null
        this.query.value = query
    }

    fun moveSelection(delta: Int) {
        val state = uiState.value
        selection.value = state.selectableItems.moveSelection(state.selectedId, delta)
    }

    fun closeSelected(): String? {
        val state = uiState.value
        val activeIndex = state.activeItems.indexOfFirst { it.id == state.selectedId }
        if (activeIndex < 0) return null

        val id = state.activeItems[activeIndex].id
        val updatedItems = itemState.value.map { item ->
            if (item.id == id) item.copy(isVisible = false, hasBeenOpened = true) else item
        }
        itemState.value = updatedItems

        val updated = updatedItems.toUiState(query.value)
        val nextSelectedId = updated.activeItems.getOrNull(activeIndex)?.id
            ?: updated.activeItems.lastOrNull()?.id
            ?: updated.selectableItems.firstOrNull()?.id
        selection.value = nextSelectedId
        return id
    }
}

internal data class ToolWindowsUiState(
    val activeItems: List<ToolWindowItem>,
    val recentItems: List<ToolWindowItem>,
    val newItems: List<ToolWindowItem>,
    val selectableItems: List<ToolWindowItem>,
    val selectedId: String?,
) {
    val isEmpty: Boolean
        get() = activeItems.isEmpty() && recentItems.isEmpty() && newItems.isEmpty()

    val isSelectedVisible: Boolean
        get() = activeItems.any { it.id == selectedId }
}

private fun List<ToolWindowItem>.toUiState(
    query: String = "",
    selectedId: String? = null,
): ToolWindowsUiState {
    val filtered = filterAndRank(query)
    val activeItems = filtered.filter(ToolWindowItem::isVisible)
    val recentItems = filtered.filter { !it.isVisible && it.hasBeenOpened }
    val newItems = filtered.filter { !it.isVisible && !it.hasBeenOpened }
    val selectableItems = (activeItems + recentItems + newItems).filter(ToolWindowItem::isAvailable)

    return ToolWindowsUiState(
        activeItems = activeItems,
        recentItems = recentItems,
        newItems = newItems,
        selectableItems = selectableItems,
        selectedId = selectedId?.takeIf { id -> selectableItems.any { it.id == id } }
            ?: selectableItems.firstOrNull()?.id,
    )
}

private fun List<ToolWindowItem>.filterAndRank(query: String): List<ToolWindowItem> {
    if (query.isBlank()) return this
    val matcher = ToolWindowMatcher(query)

    return mapNotNull { item -> matcher.matchOrNull(item.searchText)?.let { match -> item to match } }
        .sortedByDescending { (_, match) -> match.degree }
        .map { (item, match) -> item.copy(titleHighlights = match.highlights.clampTo(item.title.length)) }
}

private val ToolWindowItem.searchText: String
    get() = "$title $id"

// Matching runs over "$title $id", so fragments landing in the id part are dropped before rendering.
private fun List<IntRange>.clampTo(length: Int): List<IntRange> =
    filter { it.first < length }.map { it.first..minOf(it.last, length - 1) }

private fun List<ToolWindowItem>.moveSelection(currentId: String?, delta: Int): String? {
    if (isEmpty()) return null
    val current = indexOfFirst { it.id == currentId }
    if (current < 0) return first().id

    return this[Math.floorMod(current + delta, size)].id
}
