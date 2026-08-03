package dev.ashenarx.tools.fleet

import javax.swing.Icon

internal data class ToolWindowItem(
    val id: String,
    val title: String,
    val icon: Icon?,
    val isVisible: Boolean,
    val isActive: Boolean,
    val hasBeenOpened: Boolean,
    val isAvailable: Boolean,
    val shortcut: String? = null,
    val titleHighlights: List<IntRange> = emptyList(),
)
